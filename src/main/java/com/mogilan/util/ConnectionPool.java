package com.mogilan.util;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public final class ConnectionPool {
    public static final String DRIVER_KEY = "db.driver";
    public static final String URL_KEY = "db.url";
    public static final String USER_KEY = "db.user";
    public static final String PASSWORD_KEY = "db.password";
    public static final String POOL_SIZE_KEY = "db.pool.size";
    private static final int DEFAULT_POOL_SIZE = 10;
    private static final BlockingQueue<Connection> pool;
    private static final List<Connection> sourceConnections;

    static {
        loadDriver();
        var poolSize = getPoolSize();
        sourceConnections = new ArrayList<>(poolSize);
        pool = new ArrayBlockingQueue<>(poolSize);
        populatePool(poolSize);
    }

    private ConnectionPool() {
    }

    public static Connection getConnection() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closePool() {
        for (Connection connection : sourceConnections) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void loadDriver() {
        try {
            Class.forName(PropertiesUtil.get(DRIVER_KEY));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static int getPoolSize() {
        var size = PropertiesUtil.get(POOL_SIZE_KEY);
        return ((size == null) || (Integer.parseInt(size) == 0))
                ? DEFAULT_POOL_SIZE
                : Integer.parseInt(size);
    }

    private static void populatePool(int poolSize) {
        for (int i = 0; i < poolSize; i++) {
            var connection = open();
            var proxyConnection = (Connection) Proxy.newProxyInstance(ConnectionPool.class.getClassLoader(), new Class[]{Connection.class},
                    (proxy, method, args) -> method.getName().equals("close")
                            ? pool.add((Connection) proxy)
                            : method.invoke(connection, args));
            pool.add(proxyConnection);
            sourceConnections.add(connection);
        }
    }

    private static Connection open() {
        try {
            return DriverManager.getConnection(
                    PropertiesUtil.get(URL_KEY),
                    PropertiesUtil.get(USER_KEY),
                    PropertiesUtil.get(PASSWORD_KEY));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
