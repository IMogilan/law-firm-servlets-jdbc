package com.mogilan.repository.impl;

import com.mogilan.db.ConnectionPool;
import com.mogilan.db.impl.ConnectionPoolImpl;
import com.mogilan.model.Client;
import com.mogilan.repository.*;
import com.mogilan.repository.mapper.*;
import com.mogilan.repository.mapper.impl.*;
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
import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ClientDaoImplTest {

    ConnectionPool connectionPool;
    ContactDetailsDao contactDetailsDao;
    TaskDao taskDao;

    ClientDao clientDao;
    ContactDetailsResultSetMapper contactDetailsResultSetMapper = new ContactDetailsResultSetMapperImpl();
    TaskResultSetMapper taskResultSetMapper = new TaskResultSetMapperImpl();
    ClientResultSetMapper clientResultSetMapper;

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
        contactDetailsDao = new ContactDetailsDaoImpl(connectionPool, contactDetailsResultSetMapper);
        taskDao = new TaskDaoImpl(connectionPool, taskResultSetMapper);
        clientResultSetMapper = new ClientResultSetMapperImpl(taskDao);
        clientDao = new ClientDaoImpl(connectionPool, clientResultSetMapper);

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
        var actualResult = clientDao.findAll();
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).hasSize(3);
    }

    @Test
    void findAllShouldReturnEmptyListIfTableEmpty() {
        var clientList = clientDao.findAll();
        for (Client client : clientList) {
            clientDao.delete(client.getId());
        }
        var actualResult = clientDao.findAll();
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("findByNameSuccessArguments")
    void findByNameSuccess(String name, Long expectingId, int expectingTaskNumber) {
        var actualResult = clientDao.findByName(name);

        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();

        assertThat(actualResult.get().getId()).isEqualTo(expectingId);

        assertThat(actualResult.get().getTasks()).isNotNull();
        assertThat(actualResult.get().getTasks()).hasSize(expectingTaskNumber);
    }

    @Test
    void findByNameShouldReturnEmptyListIfNoClientsWithSuchName() {
        var any = "Any Name";
        var actualResult = clientDao.findByName(any);

        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("findByIdSuccessArguments")
    void findByIdSuccess(Long id, String expectingName, int expectingTaskNumber) {
        var actualResult = clientDao.findById(id);

        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();

        assertThat(actualResult.get().getName()).isEqualTo(expectingName);

        assertThat(actualResult.get().getTasks()).isNotNull();
        assertThat(actualResult.get().getTasks()).hasSize(expectingTaskNumber);
    }

    @ParameterizedTest
    @ValueSource(longs = {100, 1000, 10000})
    void testFindByIdShouldReturnEmptyOptionalIfElementAbsent(Long id) {
        var actualResult = clientDao.findById(id);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();
    }

    @Test
    void saveSuccess() {
        var prevListSize = clientDao.findAll().size();

        var newClient = new Client("Nik&Marta", "New client", Collections.emptyList());
        var actualResult = clientDao.save(newClient);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();

        newClient.setId(actualResult.getId());
        assertThat(actualResult).isEqualTo(newClient);

        var newListSize = clientDao.findAll().size();
        assertThat(prevListSize + 1).isEqualTo(newListSize);
    }

    @Test
    void saveShouldRedirectToUpdateIfIdAlreadyPresentInTable() {
        Long id = 1L;

        var prevListSize = clientDao.findAll().size();

        var clientById = clientDao.findById(id);
        assertThat(clientById).isPresent();

        var newClient = new Client(id, "Nik&Marta", "New client", Collections.emptyList());
        var actualResult = clientDao.save(newClient);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(id);

        assertThat(actualResult).isEqualTo(newClient);

        var newListSize = clientDao.findAll().size();
        assertThat(prevListSize).isEqualTo(newListSize);
    }

    @Test
    void updateSuccess() {
        Long id = 1L;

        var prevListSize = clientDao.findAll().size();

        var clientById = clientDao.findById(id);
        assertThat(clientById).isPresent();

        var prevValue = clientById.get();

        var newClient = new Client(id, "Nik&Marta", "New client", Collections.emptyList());
        var actualResult = clientDao.update(newClient);
        assertThat(actualResult).isNotNull();
        assertTrue(actualResult);

        var newValue = clientDao.findById(id);
        assertThat(newValue).isPresent();

        assertThat(newValue.get()).isEqualTo(newClient);
        assertThat(newValue.get()).isNotEqualTo(prevValue);

        var newListSize = clientDao.findAll().size();
        assertThat(prevListSize).isEqualTo(newListSize);
    }

    @Test
    void updateShouldReturnFalseIfIdNotPresentInTable() {
        Long id = 100L;

        var prevListSize = clientDao.findAll().size();

        var clientById = clientDao.findById(id);
        assertThat(clientById).isEmpty();

        var newClient = new Client(id, "Nik&Marta", "New client", Collections.emptyList());
        var actualResult = clientDao.update(newClient);
        assertThat(actualResult).isNotNull();
        assertFalse(actualResult);

        var newListSize = clientDao.findAll().size();
        assertThat(prevListSize).isEqualTo(newListSize);
    }

    @Test
    void deleteSuccess() {
        Long id = 1L;

        var prevListSize = clientDao.findAll().size();

        var clientById = clientDao.findById(id);
        assertThat(clientById).isPresent();

        var actualResult = clientDao.delete(id);
        assertThat(actualResult).isNotNull();
        assertTrue(actualResult);

        var newValue = clientDao.findById(id);
        assertThat(newValue).isEmpty();

        var newListSize = clientDao.findAll().size();
        assertThat(prevListSize - 1).isEqualTo(newListSize);
    }

    @Test
    void deleteShouldReturnFalseIfIdNotPresentInTable() {
        Long id = 100L;

        var prevListSize = clientDao.findAll().size();

        var clientById = clientDao.findById(id);
        assertThat(clientById).isEmpty();

        var actualResult = clientDao.delete(id);
        assertThat(actualResult).isNotNull();
        assertFalse(actualResult);

        var newListSize = clientDao.findAll().size();
        assertThat(prevListSize).isEqualTo(newListSize);
    }

    static Stream<Arguments> findByNameSuccessArguments() {
        return Stream.of(
                Arguments.of("Apple", 1L, 1),
                Arguments.of("Elon M.", 2L, 1),
                Arguments.of("Peter&Mike", 3L, 1)
        );
    }

    static Stream<Arguments> findByIdSuccessArguments() {
        return Stream.of(
                Arguments.of(1L, "Apple", 1),
                Arguments.of(2L, "Elon M.", 1),
                Arguments.of(3L, "Peter&Mike", 1)
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