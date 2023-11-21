package com.mogilan.repository.mapper;

import com.mogilan.model.Lawyer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface LawyerResultSetMapper extends ResultSetMapper<Lawyer> {
    List<Lawyer> toLawyerList(ResultSet resultSet) throws SQLException;
}
