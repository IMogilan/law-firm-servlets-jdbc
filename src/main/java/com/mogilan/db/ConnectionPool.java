package com.mogilan.db;

import java.sql.Connection;

public interface ConnectionPool {
    Connection getConnection();

    void closePool();
}
