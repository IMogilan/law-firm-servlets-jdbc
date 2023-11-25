package com.mogilan.repository.impl;

import com.mogilan.db.ConnectionPool;
import com.mogilan.db.impl.ConnectionPoolImpl;
import com.mogilan.model.*;
import com.mogilan.repository.ContactDetailsDao;
import com.mogilan.repository.LawFirmDao;
import com.mogilan.repository.LawyerDao;
import com.mogilan.repository.TaskDao;
import com.mogilan.repository.mapper.ContactDetailsResultSetMapper;
import com.mogilan.repository.mapper.LawFirmResultSetMapper;
import com.mogilan.repository.mapper.LawyerResultSetMapper;
import com.mogilan.repository.mapper.TaskResultSetMapper;
import com.mogilan.repository.mapper.impl.ContactDetailsResultSetMapperImpl;
import com.mogilan.repository.mapper.impl.LawFirmResultSetMapperImpl;
import com.mogilan.repository.mapper.impl.LawyerResultSetMapperImpl;
import com.mogilan.repository.mapper.impl.TaskResultSetMapperImpl;
import com.mogilan.servlet.dto.JobTitle;
import com.mogilan.servlet.dto.TaskPriority;
import com.mogilan.servlet.dto.TaskStatus;
import com.mogilan.util.PropertiesUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LawyerDaoImplTest {

    ConnectionPool connectionPool;
    ContactDetailsDao contactDetailsDao;
    TaskDao taskDao;
    LawyerDao lawyerDao;
    ContactDetailsResultSetMapper contactDetailsResultSetMapper = new ContactDetailsResultSetMapperImpl();
    TaskResultSetMapper taskResultSetMapper;
    LawyerResultSetMapper lawyerResultSetMapper;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @BeforeEach
    void beforeEach() {
        connectionPool = new ConnectionPoolImpl(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword(),
                Integer.parseInt(PropertiesUtil.get("db.pool.size"))
        );
        taskResultSetMapper = new TaskResultSetMapperImpl();
        contactDetailsDao = new ContactDetailsDaoImpl(connectionPool, contactDetailsResultSetMapper);
        taskDao = new TaskDaoImpl(connectionPool, taskResultSetMapper);
        lawyerResultSetMapper = new LawyerResultSetMapperImpl(contactDetailsDao, taskDao);
        lawyerDao = new LawyerDaoImpl(connectionPool, lawyerResultSetMapper);
        populateContainer();
    }

    @AfterEach
    void afterEach() {
        dropTablesInContainer();
        connectionPool.closePool();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Test
    void findAllSuccess() {
        var actualResult = lawyerDao.findAll();
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).hasSize(10);
    }


    @Test
    void findAllShouldReturnEmptyListIfTableEmpty() {
        var lawyerList = lawyerDao.findAll();
        for (Lawyer lawyer : lawyerList) {
            lawyerDao.delete(lawyer.getId());
        }
        var actualResult = lawyerDao.findAll();
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("findAllByLawFirmIdSuccessArguments")
    void findAllByLawFirmIdSuccess(Long id, int expectingLawyerNumber) {
        var actualResult = lawyerDao.findAllByLawFirmId(id);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).hasSize(expectingLawyerNumber);
    }


    @Test
    void findAllByLawFirmIdShouldReturnEmptyListIfNoLawyersByLawFirmId() {
        Long id = 1000L;
        var actualResult = lawyerDao.findAllByLawFirmId(id);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("findAllByTaskIdSuccessArguments")
    void findAllByTaskIdSuccess(Long id, int expectingLawyerNumber) {
        var actualResult = lawyerDao.findAllByTaskId(id);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).hasSize(expectingLawyerNumber);
    }

    @Test
    void findAllByTaskIdShouldReturnEmptyListIfNoLawyersByTaskId() {
        Long id = 1000L;
        var actualResult = lawyerDao.findAllByTaskId(id);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("findByIdSuccessArguments")
    void findByIdSuccess(Long id, String expectingFirstName, JobTitle expectingJobTitle, int expectingTaskNumber) {
        var actualResult = lawyerDao.findById(id);

        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();

        assertThat(actualResult.get().getFirstName()).isEqualTo(expectingFirstName);

        assertThat(actualResult.get().getJobTitle()).isEqualTo(expectingJobTitle);

        assertThat(actualResult.get().getTasks()).isNotNull();
        assertThat(actualResult.get().getTasks()).hasSize(expectingTaskNumber);
    }

    @ParameterizedTest
    @ValueSource(longs = {100, 1000, 10000})
    void testFindByIdShouldReturnEmptyOptionalIfElementAbsent(Long id) {
        var actualResult = lawyerDao.findById(id);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();
    }

    @Test
    void saveSuccess() {
        var prevListSize = lawyerDao.findAll().size();

        Long lawFirmId = 1L;
        var lawFirmPrevLawyerNumber = lawyerDao.findAllByLawFirmId(lawFirmId).size();
        var lawFirm = new LawFirm();
        lawFirm.setId(lawFirmId);

        Long firstTaskId = 1L;
        var firstTaskPrevLawyerNumber = lawyerDao.findAllByTaskId(firstTaskId).size();
        var firstTask = new Task();
        firstTask.setId(firstTaskId);

        Long secondTaskId = 2L;
        var secondTaskPrevLawyerNumber = lawyerDao.findAllByTaskId(secondTaskId).size();
        var secondTask = new Task();
        secondTask.setId(secondTaskId);

        var lawyer = new Lawyer("New", "Lawyer", JobTitle.ASSOCIATE, 100.0,
                lawFirm, new ContactDetails(), List.of(firstTask, secondTask));

        var actualResult = lawyerDao.save(lawyer);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();

        lawyer.setId(actualResult.getId());
        assertThat(actualResult).isEqualTo(lawyer);

        var newListSize = lawyerDao.findAll().size();
        assertThat(prevListSize + 1).isEqualTo(newListSize);

        var lawFirmNewTaskNumber = lawyerDao.findAllByLawFirmId(lawFirmId).size();
        assertThat(lawFirmPrevLawyerNumber + 1).isEqualTo(lawFirmNewTaskNumber);

        var firstTaskNewTaskNumber = lawyerDao.findAllByTaskId(firstTaskId).size();
        assertThat(firstTaskPrevLawyerNumber + 1).isEqualTo(firstTaskNewTaskNumber);

        var secondTaskNewLawyerNumber = lawyerDao.findAllByTaskId(secondTaskId).size();
        assertThat(secondTaskPrevLawyerNumber + 1).isEqualTo(secondTaskNewLawyerNumber);
    }

    @Test
    void saveShouldRedirectToUpdateIfIdAlreadyPresentInTable() {
        Long id = 1L;

        var lawyerById = lawyerDao.findById(id);
        assertThat(lawyerById).isPresent();

        var prevListSize = lawyerDao.findAll().size();

        Long lawFirmId = 1L;
        var lawFirmPrevLawyerNumber = lawyerDao.findAllByLawFirmId(lawFirmId).size();
        var lawFirm = new LawFirm();
        lawFirm.setId(lawFirmId);

        Long firstTaskId = 1L;
        var firstTaskPrevLawyerNumber = lawyerDao.findAllByTaskId(firstTaskId).size();
        var firstTask = new Task();
        firstTask.setId(firstTaskId);

        Long secondTaskId = 2L;
        var secondTaskPrevLawyerNumber = lawyerDao.findAllByTaskId(secondTaskId).size();
        var secondTask = new Task();
        secondTask.setId(secondTaskId);

        var lawyer = new Lawyer(id, "New", "Lawyer", JobTitle.ASSOCIATE, 100.0,
                lawFirm, new ContactDetails(), List.of(firstTask, secondTask));

        var actualResult = lawyerDao.save(lawyer);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(id);

        lawyer.setId(actualResult.getId());
        assertThat(actualResult).isEqualTo(lawyer);

        var newListSize = lawyerDao.findAll().size();
        assertThat(prevListSize).isEqualTo(newListSize);

        var lawFirmNewTaskNumber = lawyerDao.findAllByLawFirmId(lawFirmId).size();
        assertThat(lawFirmPrevLawyerNumber).isEqualTo(lawFirmNewTaskNumber);

        var firstTaskNewTaskNumber = lawyerDao.findAllByTaskId(firstTaskId).size();
        assertThat(firstTaskPrevLawyerNumber).isEqualTo(firstTaskNewTaskNumber);

        var secondTaskNewLawyerNumber = lawyerDao.findAllByTaskId(secondTaskId).size();
        assertThat(secondTaskPrevLawyerNumber + 1).isEqualTo(secondTaskNewLawyerNumber);
    }

    @Test
    void updateSuccess() {
        Long id = 1L;

        var lawyerById = lawyerDao.findById(id);
        assertThat(lawyerById).isPresent();
        var prevValue = lawyerById.get();

        var prevListSize = lawyerDao.findAll().size();

        Long lawFirmId = 1L;
        var lawFirmPrevLawyerNumber = lawyerDao.findAllByLawFirmId(lawFirmId).size();
        var lawFirm = new LawFirm();
        lawFirm.setId(lawFirmId);

        Long firstTaskId = 1L;
        var firstTaskPrevLawyerNumber = lawyerDao.findAllByTaskId(firstTaskId).size();
        var firstTask = new Task();
        firstTask.setId(firstTaskId);

        Long secondTaskId = 2L;
        var secondTaskPrevLawyerNumber = lawyerDao.findAllByTaskId(secondTaskId).size();
        var secondTask = new Task();
        secondTask.setId(secondTaskId);

        var lawyer = new Lawyer(id, "New", "Lawyer", JobTitle.ASSOCIATE, 100.0,
                lawFirm, new ContactDetails(), List.of(secondTask));

        var actualResult = lawyerDao.update(lawyer);
        assertTrue(actualResult);

        var newValue = lawyerDao.findById(id);
        assertThat(newValue).isPresent();

        assertThat(newValue.get()).isEqualTo(lawyer);
        assertThat(newValue.get()).isNotEqualTo(prevValue);

        var newListSize = lawyerDao.findAll().size();
        assertThat(prevListSize).isEqualTo(newListSize);

        var lawFirmNewTaskNumber = lawyerDao.findAllByLawFirmId(lawFirmId).size();
        assertThat(lawFirmPrevLawyerNumber).isEqualTo(lawFirmNewTaskNumber);

        var firstTaskNewTaskNumber = lawyerDao.findAllByTaskId(firstTaskId).size();
        assertThat(firstTaskPrevLawyerNumber - 1).isEqualTo(firstTaskNewTaskNumber);

        var secondTaskNewLawyerNumber = lawyerDao.findAllByTaskId(secondTaskId).size();
        assertThat(secondTaskPrevLawyerNumber + 1).isEqualTo(secondTaskNewLawyerNumber);
    }

    @Test
    void updateShouldReturnFalseIfIdNotPresentInTable() {
        Long id = 1000L;

        var lawyerById = lawyerDao.findById(id);
        assertThat(lawyerById).isEmpty();

        var prevListSize = lawyerDao.findAll().size();

        var lawyer = new Lawyer(id, "New", "Lawyer", JobTitle.ASSOCIATE, 100.0,
                null, new ContactDetails(), Collections.emptyList());

        var actualResult = lawyerDao.update(lawyer);
        assertThat(actualResult).isNotNull();
        assertFalse(actualResult);

        var newListSize = lawyerDao.findAll().size();
        assertThat(prevListSize).isEqualTo(newListSize);
    }

    @Test
    void deleteSuccess() {
        Long id = 1L;

        var lawyerById = lawyerDao.findById(id);
        assertThat(lawyerById).isPresent();

        var prevListSize = lawyerDao.findAll().size();

        Long lawFirmId = 1L;
        var lawFirmPrevLawyerNumber = lawyerDao.findAllByLawFirmId(lawFirmId).size();

        Long firstTaskId = 1L;
        var firstTaskPrevLawyerNumber = lawyerDao.findAllByTaskId(firstTaskId).size();

        var actualResult = lawyerDao.delete(id);
        assertTrue(actualResult);

        var newValue = lawyerDao.findById(id);
        assertThat(newValue).isEmpty();

        var newListSize = lawyerDao.findAll().size();
        assertThat(prevListSize - 1).isEqualTo(newListSize);

        var lawFirmNewTaskNumber = lawyerDao.findAllByLawFirmId(lawFirmId).size();
        assertThat(lawFirmPrevLawyerNumber - 1).isEqualTo(lawFirmNewTaskNumber);

        var firstTaskNewTaskNumber = lawyerDao.findAllByTaskId(firstTaskId).size();
        assertThat(firstTaskPrevLawyerNumber - 1).isEqualTo(firstTaskNewTaskNumber);
    }

    @Test
    void deleteShouldReturnFalseIfIdNotPresentInTable() {
        Long id = 1000L;

        var prevListSize = lawyerDao.findAll().size();

        var lawyerById = lawyerDao.findById(id);
        assertThat(lawyerById).isEmpty();

        var actualResult = lawyerDao.delete(id);
        assertFalse(actualResult);

        var newListSize = lawyerDao.findAll().size();
        assertThat(prevListSize).isEqualTo(newListSize);
    }

    static Stream<Arguments> findAllByLawFirmIdSuccessArguments() {
        return Stream.of(
                Arguments.of(1L, 3),
                Arguments.of(2L, 3),
                Arguments.of(3L, 4)
        );
    }

    static Stream<Arguments> findAllByTaskIdSuccessArguments() {
        return Stream.of(
                Arguments.of(1L, 1),
                Arguments.of(2L, 1),
                Arguments.of(3L, 1)
        );
    }

    static Stream<Arguments> findByIdSuccessArguments() {
        return Stream.of(
                Arguments.of(1L, "John", JobTitle.ASSOCIATE, 1),
                Arguments.of(2L, "Marta", JobTitle.PARTNER, 1),
                Arguments.of(3L, "Chris", JobTitle.MANAGING_PARTNER, 1),
                Arguments.of(4L, "Jane", JobTitle.ASSOCIATE, 0)
        );
    }

    private void populateContainer() {
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement()) {
            var createTablesScript = ContactDetailsDaoImplTest.class.getClassLoader().getResourceAsStream("test-create-tables-script.sql");
            String scriptContentSql = new String(createTablesScript.readAllBytes(), StandardCharsets.UTF_8);
            statement.execute(scriptContentSql);

            var populateTablesScript = ContactDetailsDaoImplTest.class.getClassLoader().getResourceAsStream("test-populate-script.sql");
            String populateTablesScriptSql = new String(populateTablesScript.readAllBytes(), StandardCharsets.UTF_8);
            statement.execute(populateTablesScriptSql);
        } catch (Exception e) {
            throw new RuntimeException("Error populating container", e);
        }
    }

    private void dropTablesInContainer() {
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement()) {
            var dropTablesScript = ContactDetailsDaoImplTest.class.getClassLoader().getResourceAsStream("test-drop-script.sql");
            String dropTablesScriptSql = new String(dropTablesScript.readAllBytes(), StandardCharsets.UTF_8);
            statement.execute(dropTablesScriptSql);
        } catch (Exception e) {
            throw new RuntimeException("Error populating container", e);
        }
    }
}