package com.mogilan.db.impl;

import com.mogilan.db.ConnectionPool;
import com.mogilan.util.PropertiesUtil;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionPoolImpl implements ConnectionPool {
    public static final String DRIVER_KEY = "db.driver";
    public static final String URL_KEY = "db.url";
    public static final String USER_KEY = "db.user";
    public static final String PASSWORD_KEY = "db.password";
    public static final String POOL_SIZE_KEY = "db.pool.size";
    private static final int DEFAULT_POOL_SIZE = 10;
    private final BlockingQueue<Connection> pool;
    private final List<Connection> sourceConnections;

    static {
        loadDriver();
    }

    public ConnectionPoolImpl() {
        var poolSize = getPoolSize();
        sourceConnections = new ArrayList<>(poolSize);
        pool = new ArrayBlockingQueue<>(poolSize);
        populatePool(poolSize);
    }

    public ConnectionPoolImpl(String url, String user, String password, int poolSize) {
        sourceConnections = new ArrayList<>(poolSize);
        pool = new ArrayBlockingQueue<>(poolSize);
        populatePool(url, user, password, poolSize);
    }

    public Connection getConnection() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void closePool() {
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

    private int getPoolSize() {
        var size = PropertiesUtil.get(POOL_SIZE_KEY);
        return ((size == null) || (Integer.parseInt(size) == 0))
                ? DEFAULT_POOL_SIZE
                : Integer.parseInt(size);
    }

    private void populatePool(int poolSize) {
        for (int i = 0; i < poolSize; i++) {
            var connection = open();
            addProxyConnectionToPool(connection);
            sourceConnections.add(connection);
        }
    }

    private void populatePool(String url, String user, String password, int poolSize) {
        for (int i = 0; i < poolSize; i++) {
            var connection = open(url, user, password);
            addProxyConnectionToPool(connection);
            sourceConnections.add(connection);
        }
    }

    private void addProxyConnectionToPool(Connection connection) {
        var proxyConnection = (Connection) Proxy.newProxyInstance(ConnectionPoolImpl.class.getClassLoader(), new Class[]{Connection.class},
                (proxy, method, args) -> method.getName().equals("close")
                        ? pool.add((Connection) proxy)
                        : method.invoke(connection, args));
        pool.add(proxyConnection);
    }

    private Connection open() {
        try {
            return DriverManager.getConnection(
                    PropertiesUtil.get(URL_KEY),
                    PropertiesUtil.get(USER_KEY),
                    PropertiesUtil.get(PASSWORD_KEY));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection open(String url, String user, String password) {
        try {
            return DriverManager.getConnection(
                    url,
                    user,
                    password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
