package com.mogilan.repository.mapper;

import com.mogilan.model.Task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface TaskResultSetMapper extends ResultSetMapper<Task>{
    List<Task> toTaskList(ResultSet resultSet) throws SQLException;
}
