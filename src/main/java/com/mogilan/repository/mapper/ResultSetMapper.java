package com.mogilan.repository.mapper;

import java.sql.ResultSet;

public interface ResultSetMapper <T> {
    T map (ResultSet resultSet);
}
