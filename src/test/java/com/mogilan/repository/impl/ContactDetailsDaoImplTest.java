package com.mogilan.repository.impl;

import com.mogilan.db.ConnectionPool;
import com.mogilan.db.impl.ConnectionPoolImpl;
import com.mogilan.model.ContactDetails;
import com.mogilan.repository.ContactDetailsDao;
import com.mogilan.repository.mapper.ContactDetailsResultSetMapper;
import com.mogilan.repository.mapper.impl.ContactDetailsResultSetMapperImpl;
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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class ContactDetailsDaoImplTest {

    ConnectionPool connectionPool;
    ContactDetailsDao contactDetailsDao;
    ContactDetailsResultSetMapper contactDetailsResultSetMapper = new ContactDetailsResultSetMapperImpl();

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
        var actualResult = contactDetailsDao.findAll();
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).hasSize(9);
    }

    @Test
    void findAllShouldReturnEmptyListIfTableEmpty() {
        var contactDetailsList = contactDetailsDao.findAll();
        for (ContactDetails contactDetails : contactDetailsList) {
            contactDetailsDao.delete(contactDetails.getId());
        }
        var actualResult = contactDetailsDao.findAll();
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("findByIdSuccessArguments")
    void findByIdSuccess(Long id, ContactDetails expectingResult) {
        var actualResult = contactDetailsDao.findById(id);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(expectingResult);
    }

    @ParameterizedTest
    @ValueSource(longs = {100, 1000, 10000})
    void testFindByIdShouldReturnEmptyOptionalIfElementAbsent(Long id) {
        var actualResult = contactDetailsDao.findById(id);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEmpty();
    }

    @Test
    void saveSuccess() {
        Long id = 10L;

        var prevListSize = contactDetailsDao.findAll().size();

        var contactDetailsById = contactDetailsDao.findById(id);
        assertThat(contactDetailsById).isEmpty();

        var contactDetails = new ContactDetails(id, "123 Main St, Cityville", "123-456-7890", "987-654-3210", "555-123-4567", "john.doe@example.com");
        var actualResult = contactDetailsDao.save(contactDetails);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(id);

        contactDetails.setId(id);
        assertThat(actualResult).isEqualTo(contactDetails);

        var newListSize = contactDetailsDao.findAll().size();
        assertThat(prevListSize + 1).isEqualTo(newListSize);
    }

    @Test
    void saveShouldRedirectToUpdateIfIdAlreadyPresentInTable() {
        Long id = 1L;

        var prevListSize = contactDetailsDao.findAll().size();

        var contactDetailsById = contactDetailsDao.findById(id);
        assertThat(contactDetailsById).isPresent();

        var contactDetails = new ContactDetails(id, "123 Main St, Cityville", "123-456-7890", "987-654-3210", "555-123-4567", "john.doe@example.com");
        var actualResult = contactDetailsDao.save(contactDetails);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(id);

        assertThat(actualResult).isEqualTo(contactDetails);

        var newListSize = contactDetailsDao.findAll().size();
        assertThat(prevListSize).isEqualTo(newListSize);
    }

    @Test
    void updateSuccess() {
        Long id = 1L;

        var prevListSize = contactDetailsDao.findAll().size();

        var contactDetailsById = contactDetailsDao.findById(id);
        assertThat(contactDetailsById).isPresent();

        var prevValue = contactDetailsById.get();

        var contactDetails = new ContactDetails(id, prevValue.getAddress(), null, null, null, "piter@example.com");
        var actualResult = contactDetailsDao.update(contactDetails);
        assertThat(actualResult).isNotNull();
        assertTrue(actualResult);

        var newValue = contactDetailsDao.findById(id);
        assertThat(newValue).isPresent();

        assertThat(newValue.get()).isEqualTo(contactDetails);
        assertThat(newValue.get()).isNotEqualTo(prevValue);

        var newListSize = contactDetailsDao.findAll().size();
        assertThat(prevListSize).isEqualTo(newListSize);
    }

    @Test
    void updateShouldReturnFalseIfIdNotPresentInTable() {
        Long id = 100L;

        var prevListSize = contactDetailsDao.findAll().size();

        var contactDetailsById = contactDetailsDao.findById(id);
        assertThat(contactDetailsById).isEmpty();

        var contactDetails = new ContactDetails(id, "Any", null, null, null, "piter@example.com");
        var actualResult = contactDetailsDao.update(contactDetails);
        assertThat(actualResult).isNotNull();
        assertFalse(actualResult);

        var newListSize = contactDetailsDao.findAll().size();
        assertThat(prevListSize).isEqualTo(newListSize);
    }

    @Test
    void deleteSuccess() {
        Long id = 1L;

        var prevListSize = contactDetailsDao.findAll().size();

        var contactDetailsById = contactDetailsDao.findById(id);
        assertThat(contactDetailsById).isPresent();

        var actualResult = contactDetailsDao.delete(id);
        assertThat(actualResult).isNotNull();
        assertTrue(actualResult);

        var newValue = contactDetailsDao.findById(id);
        assertThat(newValue).isEmpty();

        var newListSize = contactDetailsDao.findAll().size();
        assertThat(prevListSize - 1).isEqualTo(newListSize);
    }

    @Test
    void deleteShouldReturnFalseIfIdNotPresentInTable() {
        Long id = 100L;

        var prevListSize = contactDetailsDao.findAll().size();

        var contactDetailsById = contactDetailsDao.findById(id);
        assertThat(contactDetailsById).isEmpty();

        var actualResult = contactDetailsDao.delete(id);
        assertThat(actualResult).isNotNull();
        assertFalse(actualResult);

        var newListSize = contactDetailsDao.findAll().size();
        assertThat(prevListSize).isEqualTo(newListSize);
    }

    static Stream<Arguments> findByIdSuccessArguments() {
        return Stream.of(
                Arguments.of(
                        1L,
                        new ContactDetails(1L, "123 Main St, Cityville", "123-456-7890", "987-654-3210", "555-123-4567", "john.doe@example.com")
                ),
                Arguments.of(
                        2L,
                        new ContactDetails(2L, "123 Main St, Cityville", "123-456-7890", "987-654-3210", "555-123-4567", "john.doe@example.com")
                ),
                Arguments.of(
                        3L,
                        new ContactDetails(3L, "123 Main St, Cityville", "123-456-7890", "987-654-3210", "555-123-4567", "john.doe@example.com")
                )
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