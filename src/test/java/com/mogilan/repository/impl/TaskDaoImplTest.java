package com.mogilan.repository.impl;

import com.mogilan.db.ConnectionPool;
import com.mogilan.db.impl.ConnectionPoolImpl;
import com.mogilan.model.Client;
import com.mogilan.model.Lawyer;
import com.mogilan.model.Task;
import com.mogilan.repository.TaskDao;
import com.mogilan.repository.mapper.TaskResultSetMapper;
import com.mogilan.repository.mapper.impl.TaskResultSetMapperImpl;
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
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskDaoImplTest {

    ConnectionPool connectionPool;
    TaskDao taskDao;
    TaskResultSetMapper taskResultSetMapper = new TaskResultSetMapperImpl();

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
        taskDao = new TaskDaoImpl(connectionPool, taskResultSetMapper);
        populateContainer();
    }

    @AfterEach
    void afterEach() {
        dropTablesInContainer();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Test
    void findAllSuccess() {
        var actualResult = taskDao.findAll();
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).hasSize(3);
    }

    @Test
    void findAllShouldReturnEmptyListIfTableEmpty() {
        var taskList = taskDao.findAll();
        for (Task task : taskList) {
            taskDao.delete(task.getId());
        }
        var actualResult = taskDao.findAll();
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("findAllByClientIdSuccessArguments")
    void findAllByClientIdSuccess(Long id, int expectingTaskNumber) {
        var actualResult = taskDao.findAllByClientId(id);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).hasSize(expectingTaskNumber);
    }

    @Test
    void findAllByClientIdShouldReturnEmptyListIfNoTaskByClientId() {
        Long id = 1000L;
        var actualResult = taskDao.findAllByClientId(id);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("findAllByLawyerIdSuccessArguments")
    void findAllByLawyerIdSuccess(Long id, int expectingTaskNumber) {
        var actualResult = taskDao.findAllByClientId(id);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).hasSize(expectingTaskNumber);
    }

    @Test
    void findAllByLawyerIdShouldReturnEmptyListIfNoTaskByClientId() {
        Long id = 1000L;
        var actualResult = taskDao.findAllByLawyerId(id);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("findByIdSuccessArguments")
    void findByIdSuccess(Long id, String expectingTitle, int expectingLawyersNumber) {
        var actualResult = taskDao.findById(id);

        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();

        assertThat(actualResult.get().getTitle()).isEqualTo(expectingTitle);

        assertThat(actualResult.get().getLawyers()).isNotNull();
        assertThat(actualResult.get().getLawyers()).hasSize(expectingLawyersNumber);
    }

    @ParameterizedTest
    @ValueSource(longs = {100, 1000, 10000})
    void testFindByIdShouldReturnEmptyOptionalIfElementAbsent(Long id) {
        var actualResult = taskDao.findById(id);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();
    }

    @Test
    void saveSuccess() {
        var prevListSize = taskDao.findAll().size();

        Long clientId = 1L;
        var clientPrevTaskNumber = taskDao.findAllByClientId(clientId).size();
        var client = new Client();
        client.setId(clientId);

        Long firstLawyerId = 4L;
        var firstLawyerPrevTaskNumber = taskDao.findAllByLawyerId(firstLawyerId).size();
        var firstLawyer = new Lawyer();
        firstLawyer.setId(firstLawyerId);

        Long secondLawyerId = 5L;
        var secondLawyerPrevTaskNumber = taskDao.findAllByLawyerId(secondLawyerId).size();
        var secondLawyer = new Lawyer();
        secondLawyer.setId(secondLawyerId);

        var task = new Task("Task 3", "Draft legal documents", TaskPriority.HIGH, TaskStatus.RECEIVED,
                LocalDate.of(2023, 11, 11), LocalDate.of(2023, 11, 21),
                null, 2.0, client, List.of(firstLawyer, secondLawyer));

        var actualResult = taskDao.save(task);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();

        task.setId(actualResult.getId());
        assertThat(actualResult).isEqualTo(task);

        var newListSize = taskDao.findAll().size();
        assertThat(prevListSize + 1).isEqualTo(newListSize);

        var clientNewTaskNumber = taskDao.findAllByClientId(clientId).size();
        assertThat(clientPrevTaskNumber + 1).isEqualTo(clientNewTaskNumber);

        var firstLawyerNewTaskNumber = taskDao.findAllByLawyerId(firstLawyerId).size();
        assertThat(firstLawyerPrevTaskNumber + 1).isEqualTo(firstLawyerNewTaskNumber);

        var secondLawyerNewTaskNumber = taskDao.findAllByLawyerId(secondLawyerId).size();
        assertThat(secondLawyerPrevTaskNumber + 1).isEqualTo(secondLawyerNewTaskNumber);
    }

    @Test
    void saveShouldRedirectToUpdateIfIdAlreadyPresentInTable() {
        Long id = 1L;

        var prevListSize = taskDao.findAll().size();

        var taskById = taskDao.findById(id);
        assertThat(taskById).isPresent();

        Long clientId = 1L;
        var clientPrevTaskNumber = taskDao.findAllByClientId(clientId).size();
        var client = new Client();
        client.setId(clientId);

        Long firstLawyerId = 1L;
        var firstLawyerPrevTaskNumber = taskDao.findAllByLawyerId(firstLawyerId).size();
        var firstLawyer = new Lawyer();
        firstLawyer.setId(firstLawyerId);

        Long secondLawyerId = 5L;
        var secondLawyerPrevTaskNumber = taskDao.findAllByLawyerId(secondLawyerId).size();
        var secondLawyer = new Lawyer();
        secondLawyer.setId(secondLawyerId);

        var task = new Task(id, "Task 3", "Draft legal documents", TaskPriority.HIGH, TaskStatus.RECEIVED,
                LocalDate.of(2023, 11, 11), LocalDate.of(2023, 11, 21),
                null, 2.0, client, List.of(firstLawyer, secondLawyer));

        var actualResult = taskDao.save(task);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(id);

        task.setId(actualResult.getId());
        assertThat(actualResult).isEqualTo(task);

        var newListSize = taskDao.findAll().size();
        assertThat(prevListSize).isEqualTo(newListSize);

        var clientNewTaskNumber = taskDao.findAllByClientId(clientId).size();
        assertThat(clientPrevTaskNumber).isEqualTo(clientNewTaskNumber);

        var firstLawyerNewTaskNumber = taskDao.findAllByLawyerId(firstLawyerId).size();
        assertThat(firstLawyerPrevTaskNumber).isEqualTo(firstLawyerNewTaskNumber);

        var secondLawyerNewTaskNumber = taskDao.findAllByLawyerId(secondLawyerId).size();
        assertThat(secondLawyerPrevTaskNumber + 1).isEqualTo(secondLawyerNewTaskNumber);
    }

    @Test
    void updateSuccess() {
        Long id = 1L;

        var prevListSize = taskDao.findAll().size();

        var taskById = taskDao.findById(id);
        assertThat(taskById).isPresent();
        var prevValue = taskById.get();

        Long clientId = 1L;
        var clientPrevTaskNumber = taskDao.findAllByClientId(clientId).size();
        var client = new Client();
        client.setId(clientId);

        Long firstLawyerId = 1L;
        var firstLawyerPrevTaskNumber = taskDao.findAllByLawyerId(firstLawyerId).size();
        var firstLawyer = new Lawyer();
        firstLawyer.setId(firstLawyerId);

        Long secondLawyerId = 5L;
        var secondLawyerPrevTaskNumber = taskDao.findAllByLawyerId(secondLawyerId).size();
        var secondLawyer = new Lawyer();
        secondLawyer.setId(secondLawyerId);

        var task = new Task(id, "Task 3", "Draft legal documents", TaskPriority.HIGH, TaskStatus.RECEIVED,
                LocalDate.of(2023, 11, 11), LocalDate.of(2023, 11, 21),
                null, 2.0, client, List.of(secondLawyer));

        var actualResult = taskDao.update(task);
        assertTrue(actualResult);

        var newValue = taskDao.findById(id);
        assertThat(newValue).isPresent();

        assertThat(newValue.get()).isEqualTo(task);
        assertThat(newValue.get()).isNotEqualTo(prevValue);

        var newListSize = taskDao.findAll().size();
        assertThat(prevListSize).isEqualTo(newListSize);

        var clientNewTaskNumber = taskDao.findAllByClientId(clientId).size();
        assertThat(clientPrevTaskNumber).isEqualTo(clientNewTaskNumber);

        var firstLawyerNewTaskNumber = taskDao.findAllByLawyerId(firstLawyerId).size();
        assertThat(firstLawyerPrevTaskNumber - 1).isEqualTo(firstLawyerNewTaskNumber);

        var secondLawyerNewTaskNumber = taskDao.findAllByLawyerId(secondLawyerId).size();
        assertThat(secondLawyerPrevTaskNumber + 1).isEqualTo(secondLawyerNewTaskNumber);
    }

    @Test
    void updateShouldReturnFalseIfIdNotPresentInTable() {
        Long id = 1000L;

        var prevListSize = taskDao.findAll().size();

        var taskById = taskDao.findById(id);
        assertThat(taskById).isEmpty();

        var task = new Task(id, "Task 3", "Draft legal documents", TaskPriority.HIGH, TaskStatus.RECEIVED,
                LocalDate.of(2023, 11, 11), LocalDate.of(2023, 11, 21),
                null, 2.0, null, null);

        var actualResult = taskDao.update(task);
        assertThat(actualResult).isNotNull();
        assertFalse(actualResult);

        var newListSize = taskDao.findAll().size();
        assertThat(prevListSize).isEqualTo(newListSize);
    }

    @Test
    void deleteSuccess() {
        Long id = 1L;

        var prevListSize = taskDao.findAll().size();

        var taskById = taskDao.findById(id);
        assertThat(taskById).isPresent();

        Long clientId = 1L;
        var clientPrevTaskNumber = taskDao.findAllByClientId(clientId).size();

        Long firstLawyerId = 1L;
        var firstLawyerPrevTaskNumber = taskDao.findAllByLawyerId(firstLawyerId).size();

        var actualResult = taskDao.delete(id);
        assertTrue(actualResult);

        var newValue = taskDao.findById(id);
        assertThat(newValue).isEmpty();

        var newListSize = taskDao.findAll().size();
        assertThat(prevListSize - 1).isEqualTo(newListSize);

        var clientNewTaskNumber = taskDao.findAllByClientId(clientId).size();
        assertThat(clientPrevTaskNumber - 1).isEqualTo(clientNewTaskNumber);

        var firstLawyerNewTaskNumber = taskDao.findAllByLawyerId(firstLawyerId).size();
        assertThat(firstLawyerPrevTaskNumber - 1).isEqualTo(firstLawyerNewTaskNumber);
    }

    @Test
    void deleteShouldReturnFalseIfIdNotPresentInTable() {
        Long id = 1000L;

        var prevListSize = taskDao.findAll().size();

        var taskById = taskDao.findById(id);
        assertThat(taskById).isEmpty();

        var actualResult = taskDao.delete(id);
        assertFalse(actualResult);

        var newListSize = taskDao.findAll().size();
        assertThat(prevListSize).isEqualTo(newListSize);
    }

    static Stream<Arguments> findAllByClientIdSuccessArguments() {
        return Stream.of(
                Arguments.of(1L, 1),
                Arguments.of(2L, 1),
                Arguments.of(3L, 1)
        );
    }

    static Stream<Arguments> findAllByLawyerIdSuccessArguments() {
        return Stream.of(
                Arguments.of(1L, 1),
                Arguments.of(2L, 1),
                Arguments.of(3L, 1)
        );
    }

    static Stream<Arguments> findByIdSuccessArguments() {
        return Stream.of(
                Arguments.of(1L, "Task 1", 1),
                Arguments.of(2L, "Task 2", 1),
                Arguments.of(3L, "Task 3", 1)
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