package com.mogilan.repository.impl;

import com.mogilan.model.Client;
import com.mogilan.model.ContactDetails;
import com.mogilan.repository.ClientDao;
import com.mogilan.util.ConnectionPool;
import com.mogilan.util.PropertiesUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

class ContactDetailsDaoImplTest {

    ClientDaoImpl clientDao;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();

        setToConnectionPoolPropertiesFromPostgresContainer();

        populateContainer(postgres);
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();

        dropTablesInContainer(postgres);

        PropertiesUtil.setDefaultProperties();
    }

    @Test
    void findAll() {

        var all = clientDao.findAll();
        Assertions.assertThat(all).isNotEmpty();
    }

    @Test
    void findById() {
    }

    @Test
    void testFindById() {
    }

    @Test
    void save() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    @Test
    void getInstance() {
    }

    private static void setToConnectionPoolPropertiesFromPostgresContainer() {
        PropertiesUtil.set(ConnectionPool.URL_KEY, postgres.getJdbcUrl());
        PropertiesUtil.set(ConnectionPool.USER_KEY, postgres.getUsername());
        PropertiesUtil.set(ConnectionPool.PASSWORD_KEY, postgres.getPassword());
    }

    private static void insertTestData() {
        ContactDetails contact1 = new ContactDetails(null, "Address 1", "123456789", "987654321", "555555555", "test1@example.com");
        ContactDetails contact2 = new ContactDetails(null, "Address 2", "987654321", "123456789", "666666666", "test2@example.com");

//        clie.save(contact1);
//        contactDetailsDao.save(contact2);
    }

    private static void populateContainer(PostgreSQLContainer<?> container) {
        try (Connection connection = ConnectionPool.getConnection();
             Statement statement = connection.createStatement()) {
            var scriptStream = ContactDetailsDaoImplTest.class.getClassLoader().getResourceAsStream("SQL-script.sql");
            String scriptContent = new String(scriptStream.readAllBytes(), StandardCharsets.UTF_8);
            statement.execute(scriptContent);
        } catch (Exception e) {
            throw new RuntimeException("Error populating container", e);
        }
    }

    private static void dropTablesInContainer(PostgreSQLContainer<?> container) {
        try (Connection connection = ConnectionPool.getConnection();
             Statement statement = connection.createStatement()) {
            var scriptStream = ContactDetailsDaoImplTest.class.getClassLoader().getResourceAsStream("Drop-script.sql");
            String scriptContent = new String(scriptStream.readAllBytes(), StandardCharsets.UTF_8);
            statement.execute(scriptContent);
        } catch (Exception e) {
            throw new RuntimeException("Error populating container", e);
        }
    }

}