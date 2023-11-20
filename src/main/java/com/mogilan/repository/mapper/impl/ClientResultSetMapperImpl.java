package com.mogilan.repository.mapper.impl;

import com.mogilan.exception.DaoException;
import com.mogilan.model.Client;
import com.mogilan.repository.impl.TaskDaoImpl;
import com.mogilan.repository.mapper.ClientResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientResultSetMapperImpl implements ClientResultSetMapper {

    private static final ClientResultSetMapperImpl INSTANCE = new ClientResultSetMapperImpl();
    private final TaskDaoImpl taskDao = TaskDaoImpl.getInstance();

    private ClientResultSetMapperImpl() {
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

    public static ClientResultSetMapperImpl getInstance() {
        return INSTANCE;
    }
}
