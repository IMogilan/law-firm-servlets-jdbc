package com.mogilan.repository.impl;

import com.mogilan.db.ConnectionPool;
import com.mogilan.db.impl.ConnectionPoolImpl;
import com.mogilan.model.LawFirm;
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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LawFirmDaoImplTest {
    ConnectionPool connectionPool;
    ContactDetailsDao contactDetailsDao;
    LawFirmDao lawFirmDao;
    TaskDao taskDao;
    LawyerDao lawyerDao;
    ContactDetailsResultSetMapper contactDetailsResultSetMapper = new ContactDetailsResultSetMapperImpl();
    TaskResultSetMapper taskResultSetMapper;
    LawFirmResultSetMapper lawFirmResultSetMapper;
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
        lawFirmResultSetMapper = new LawFirmResultSetMapperImpl(lawyerDao);
        lawFirmDao = new LawFirmDaoImpl(connectionPool, lawFirmResultSetMapper);
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
        var actualResult = lawFirmDao.findAll();
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).hasSize(3);
    }

    @Test
    void findAllShouldReturnEmptyListIfTableEmpty() {
        var lawFirmList = lawFirmDao.findAll();
        for (LawFirm lawFirm : lawFirmList) {
            lawFirmDao.delete(lawFirm.getId());
        }
        var actualResult = lawFirmDao.findAll();
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("findByIdSuccessArguments")
    void findByIdSuccess(Long id, String expectingName, int expectingLawyersNumber) {
        var actualResult = lawFirmDao.findById(id);

        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();

        assertThat(actualResult.get().getName()).isEqualTo(expectingName);

        assertThat(actualResult.get().getLawyers()).isNotNull();
        assertThat(actualResult.get().getLawyers()).hasSize(expectingLawyersNumber);
    }

    @ParameterizedTest
    @ValueSource(longs = {100, 1000, 10000})
    void testFindByIdShouldReturnEmptyOptionalIfElementAbsent(Long id) {
        var actualResult = lawFirmDao.findById(id);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("findByNameSuccessArguments")
    void findByNameSuccess(String name, Long expectingId, int expectingLawyersNumber) {
        var actualResult = lawFirmDao.findByName(name);

        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();

        assertThat(actualResult.get().getId()).isEqualTo(expectingId);

        assertThat(actualResult.get().getLawyers()).isNotNull();
        assertThat(actualResult.get().getLawyers()).hasSize(expectingLawyersNumber);
    }

    @Test
    void findByNameShouldReturnEmptyListIfNoLawFirmWithSuchName() {
        var any = "Any Name";
        var actualResult = lawFirmDao.findByName(any);

        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();
    }

    @Test
    void saveSuccess() {
        var prevListSize = lawFirmDao.findAll().size();

        var lawFirm = new LawFirm("Precedent", LocalDate.of(1993,1,1));
        var actualResult = lawFirmDao.save(lawFirm);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();

        lawFirm.setId(actualResult.getId());
        assertThat(actualResult).isEqualTo(lawFirm);

        var newListSize = lawFirmDao.findAll().size();
        assertThat(prevListSize + 1).isEqualTo(newListSize);
    }

    @Test
    void saveShouldRedirectToUpdateIfIdAlreadyPresentInTable() {
        Long id = 1L;

        var prevListSize = lawFirmDao.findAll().size();

        var lawFirmById = lawFirmDao.findById(id);
        assertThat(lawFirmById).isPresent();

        var lawFirm = new LawFirm(id, "Precedent", LocalDate.of(1993,1,1));
        var actualResult = lawFirmDao.save(lawFirm);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(id);

        assertThat(actualResult).isEqualTo(lawFirm);

        var newListSize = lawFirmDao.findAll().size();
        assertThat(prevListSize).isEqualTo(newListSize);
    }

    @Test
    void updateSuccess() {
        Long id = 1L;

        var prevListSize = lawFirmDao.findAll().size();

        var lawFirmById = lawFirmDao.findById(id);
        assertThat(lawFirmById).isPresent();

        var prevValue = lawFirmById.get();

        var lawFirm = new LawFirm(id, "Precedent", LocalDate.of(1993,1,1));
        var actualResult = lawFirmDao.update(lawFirm);
        assertThat(actualResult).isNotNull();
        assertTrue(actualResult);

        var newValue = lawFirmDao.findById(id);
        assertThat(newValue).isPresent();

        assertThat(newValue.get()).isEqualTo(lawFirm);
        assertThat(newValue.get()).isNotEqualTo(prevValue);

        var newListSize = lawFirmDao.findAll().size();
        assertThat(prevListSize).isEqualTo(newListSize);
    }

    @Test
    void updateShouldReturnFalseIfIdNotPresentInTable() {
        Long id = 100L;

        var prevListSize = lawFirmDao.findAll().size();

        var lawFirmById = lawFirmDao.findById(id);
        assertThat(lawFirmById).isEmpty();

        var lawFirm = new LawFirm(id, "Precedent", LocalDate.of(1993,1,1));
        var actualResult = lawFirmDao.update(lawFirm);
        assertThat(actualResult).isNotNull();
        assertFalse(actualResult);

        var newListSize = lawFirmDao.findAll().size();
        assertThat(prevListSize).isEqualTo(newListSize);
    }

    @Test
    void deleteSuccess() {
        Long id = 1L;

        var prevListSize = lawFirmDao.findAll().size();

        var lawFirmById = lawFirmDao.findById(id);
        assertThat(lawFirmById).isPresent();

        var actualResult = lawFirmDao.delete(id);
        assertThat(actualResult).isNotNull();
        assertTrue(actualResult);

        var newValue = lawFirmDao.findById(id);
        assertThat(newValue).isEmpty();

        var newListSize = lawFirmDao.findAll().size();
        assertThat(prevListSize - 1).isEqualTo(newListSize);
    }

    @Test
    void deleteShouldReturnFalseIfIdNotPresentInTable() {
        Long id = 100L;

        var prevListSize = lawFirmDao.findAll().size();

        var lawFirmById = lawFirmDao.findById(id);
        assertThat(lawFirmById).isEmpty();

        var actualResult = lawFirmDao.delete(id);
        assertThat(actualResult).isNotNull();
        assertFalse(actualResult);

        var newListSize = lawFirmDao.findAll().size();
        assertThat(prevListSize).isEqualTo(newListSize);
    }

    static Stream<Arguments> findByIdSuccessArguments() {
        return Stream.of(
                Arguments.of(1L, "Law Firm A", 3),
                Arguments.of(2L, "Law Firm B", 3),
                Arguments.of(3L, "Law Firm C", 4)
        );
    }
    static Stream<Arguments> findByNameSuccessArguments() {
        return Stream.of(
                Arguments.of("Law Firm A", 1L, 3),
                Arguments.of("Law Firm B", 2L, 3),
                Arguments.of("Law Firm C", 3L, 4)
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