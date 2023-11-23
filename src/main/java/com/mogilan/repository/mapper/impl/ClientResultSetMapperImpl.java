package com.mogilan.repository.mapper.impl;

import com.mogilan.exception.DaoException;
import com.mogilan.model.Client;
import com.mogilan.repository.TaskDao;
import com.mogilan.repository.mapper.ClientResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientResultSetMapperImpl implements ClientResultSetMapper {
    private final TaskDao taskDao;

    public ClientResultSetMapperImpl(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public Client map(ResultSet resultSet) {
        try {
            var id = resultSet.getLong("id");
            var tasks = taskDao.findAllByClientId(id, resultSet.getStatement().getConnection());
            return new Client(
                    id,
                    resultSet.getString("name"),
                    resultSet.getString("description"),
                    tasks
            );
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}
