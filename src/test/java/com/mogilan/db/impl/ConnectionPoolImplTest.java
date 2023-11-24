package com.mogilan.db.impl;

import com.mogilan.util.PropertiesUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;

import static org.assertj.core.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class ConnectionPoolImplTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @Test
    void poolSizeCorrectWhenParamConstructor() throws NoSuchFieldException, IllegalAccessException {
        var poolSize = Integer.parseInt(PropertiesUtil.get("db.pool.size"));
        var connectionPool = new ConnectionPoolImpl(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword(),
                poolSize);

        var poolField = connectionPool.getClass().getDeclaredField("pool");
        assertThat(poolField).isNotNull();
        try {
            poolField.setAccessible(true);
            var pool = poolField.get(connectionPool);
            assertThat(pool).isNotNull();
            assertThat(pool).isInstanceOf(BlockingQueue.class);
            var blockingQueue = (BlockingQueue<Connection>) pool;
            assertThat(blockingQueue).isNotEmpty();
            assertThat(blockingQueue).hasSize(poolSize);
        } finally {
            poolField.setAccessible(false);
        }
    }

    @Test
    void creationConnectionPoolShouldThrowExceptionIfParamIncorrect() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            var connectionPool = new ConnectionPoolImpl(
                    "dummy",
                    "dummy",
                    "dummy",
                    0);
        });
    }

    @Test
    void getConnectionSuccess() throws NoSuchFieldException, IllegalAccessException, SQLException {
        var poolSize = Integer.parseInt(PropertiesUtil.get("db.pool.size"));
        var connectionPool = new ConnectionPoolImpl(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword(),
                poolSize);
        try (var connection = connectionPool.getConnection()) {
            assertThat(connection).isNotNull();
            assertThat(connection).isInstanceOf(Connection.class);


            var poolField = connectionPool.getClass().getDeclaredField("pool");
            assertThat(poolField).isNotNull();
            try {
                poolField.setAccessible(true);
                var pool = poolField.get(connectionPool);
                assertThat(pool).isNotNull();
                assertThat(pool).isInstanceOf(BlockingQueue.class);
                var blockingQueue = (BlockingQueue<Connection>) pool;
                assertThat(blockingQueue).isNotEmpty();
                var initialPoolSize = Integer.parseInt(PropertiesUtil.get(ConnectionPoolImpl.POOL_SIZE_KEY));
                assertThat(blockingQueue).hasSize(initialPoolSize - 1);
            } finally {
                poolField.setAccessible(false);
            }
        }
    }

    @Test
    void connectionShouldBeAddedToPoolOnCloseMethodInvocation() throws NoSuchFieldException, IllegalAccessException, SQLException {
        var poolSize = Integer.parseInt(PropertiesUtil.get("db.pool.size"));
        var connectionPool = new ConnectionPoolImpl(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword(),
                poolSize);

        var connection = connectionPool.getConnection();
        assertThat(connection).isNotNull();
        assertThat(connection).isInstanceOf(Connection.class);

        connection.close();

        var poolField = connectionPool.getClass().getDeclaredField("pool");
        assertThat(poolField).isNotNull();
        try {
            poolField.setAccessible(true);
            var pool = poolField.get(connectionPool);
            assertThat(pool).isNotNull();
            assertThat(pool).isInstanceOf(BlockingQueue.class);
            var blockingQueue = (BlockingQueue<Connection>) pool;
            assertThat(blockingQueue).isNotEmpty();
            var initialPoolSize = Integer.parseInt(PropertiesUtil.get(ConnectionPoolImpl.POOL_SIZE_KEY));
            assertThat(blockingQueue).hasSize(initialPoolSize);
        } finally {
            poolField.setAccessible(false);
        }
    }


    @Test
    void closePoolSuccess() throws NoSuchFieldException, IllegalAccessException, SQLException {
        var poolSize = Integer.parseInt(PropertiesUtil.get("db.pool.size"));
        var connectionPool = new ConnectionPoolImpl(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword(),
                poolSize);
        connectionPool.closePool();

        var poolField = connectionPool.getClass().getDeclaredField("pool");
        assertThat(poolField).isNotNull();
        try {
            poolField.setAccessible(true);
            var pool = poolField.get(connectionPool);
            assertThat(pool).isNotNull();
            assertThat(pool).isInstanceOf(BlockingQueue.class);
            var blockingQueue = (BlockingQueue<Connection>) pool;
            assertThat(blockingQueue).isNotEmpty();
            for (Connection connection : blockingQueue) {
                assertThat(connection.isClosed()).isTrue();
            }
        } finally {
            poolField.setAccessible(false);
        }
    }
}