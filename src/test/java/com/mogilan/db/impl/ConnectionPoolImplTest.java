package com.mogilan.db.impl;

import com.mogilan.util.PropertiesUtil;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;

import static org.assertj.core.api.Assertions.*;

class ConnectionPoolImplTest {

    @Test
    void poolSizeCorrectWhenDefaultConstructor() throws NoSuchFieldException, IllegalAccessException {
        var connectionPool = new ConnectionPoolImpl();

        var poolField = connectionPool.getClass().getDeclaredField("pool");
        assertThat(poolField).isNotNull();
        try {
            poolField.setAccessible(true);
            var pool = poolField.get(connectionPool);
            assertThat(pool).isNotNull();
            assertThat(pool).isInstanceOf(BlockingQueue.class);
            var blockingQueue = (BlockingQueue<Connection>) pool;
            assertThat(blockingQueue).isNotEmpty();
            assertThat(blockingQueue).hasSize(Integer.parseInt(PropertiesUtil.get(ConnectionPoolImpl.POOL_SIZE_KEY)));
        } finally {
            poolField.setAccessible(false);
        }
    }

    @Test
    void poolSizeCorrectWhenParamConstructor() throws NoSuchFieldException, IllegalAccessException {
        int givenSize = 10;
        var connectionPool = new ConnectionPoolImpl(
                PropertiesUtil.get(ConnectionPoolImpl.URL_KEY),
                PropertiesUtil.get(ConnectionPoolImpl.USER_KEY),
                PropertiesUtil.get(ConnectionPoolImpl.PASSWORD_KEY),
                givenSize);

        var poolField = connectionPool.getClass().getDeclaredField("pool");
        assertThat(poolField).isNotNull();
        try {
            poolField.setAccessible(true);
            var pool = poolField.get(connectionPool);
            assertThat(pool).isNotNull();
            assertThat(pool).isInstanceOf(BlockingQueue.class);
            var blockingQueue = (BlockingQueue<Connection>) pool;
            assertThat(blockingQueue).isNotEmpty();
            assertThat(blockingQueue).hasSize(givenSize);
        } finally {
            poolField.setAccessible(false);
        }
    }

        @Test
        void getConnectionSuccess () throws NoSuchFieldException, IllegalAccessException {
            var connectionPool = new ConnectionPoolImpl();
            var connection = connectionPool.getConnection();
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

    @Test
    void connectionShouldBeAddedToPoolOnCloseMethodInvocation () throws NoSuchFieldException, IllegalAccessException, SQLException {
        var connectionPool = new ConnectionPoolImpl();

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
        void closePoolSuccess () throws NoSuchFieldException, IllegalAccessException, SQLException {
            var connectionPool = new ConnectionPoolImpl();
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