package com.mogilan.repository.impl;

import com.mogilan.db.ConnectionPool;
import com.mogilan.repository.ClientDao;
import com.mogilan.model.Client;
import com.mogilan.exception.DaoException;
import com.mogilan.repository.mapper.ClientResultSetMapper;
import com.mogilan.repository.mapper.impl.ClientResultSetMapperImpl;
import com.mogilan.db.impl.ConnectionPoolImpl;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientDaoImpl implements ClientDao {
    private static final String FIND_ALL_SQL = """
            SELECT  id,
                    name,
                    description
            FROM clients
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;
    private static final String FIND_BY_NAME_SQL = FIND_ALL_SQL + """
            WHERE name LIKE ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO clients (name, description)
            VALUES (?,?)
            """;
    private static final String UPDATE_SQL = """
            UPDATE clients
            SET name = ?,
                description = ?
            WHERE id = ?
            """;
    private static final String DELETE_SQL = """
            DELETE
            FROM clients
            WHERE id = ?
            """;
    private final ConnectionPool connectionPool;
    private final ClientResultSetMapper resultSetMapper;

    public ClientDaoImpl(ConnectionPool connectionPool, ClientResultSetMapper resultSetMapper) {
        this.connectionPool = connectionPool;
        this.resultSetMapper = resultSetMapper;
    }

    @Override
    public List<Client> findAll() {
        try (var connection = connectionPool.getConnection();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            List<Client> clients = new ArrayList<>();

            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                var client = resultSetMapper.map(resultSet);
                clients.add(client);
            }
            return clients;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Client> findById(Long id) {
        try (var connection = connectionPool.getConnection();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            Client client = null;

            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                client = resultSetMapper.map(resultSet);
            }

            return Optional.ofNullable(client);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Client> findByName(String name) {
        try (var connection = connectionPool.getConnection();
             var preparedStatement = connection.prepareStatement(FIND_BY_NAME_SQL)) {
            Client client = null;

            preparedStatement.setString(1, name);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                client = resultSetMapper.map(resultSet);
            }

            return Optional.ofNullable(client);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Client save(Client entity) {
        var id = entity.getId();
        if (id != null && findById(id).isPresent()) {
            update(entity);
            return findById(id).get();
        }
        try (var connection = connectionPool.getConnection();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, entity.getName());
            preparedStatement.setString(2, entity.getDescription());
            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.setId(generatedKeys.getLong("id"));
            }
            return entity;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(Client entity) {
        try (var connection = connectionPool.getConnection();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setString(1, entity.getName());
            preparedStatement.setString(2, entity.getDescription());
            preparedStatement.setLong(3, entity.getId());

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (var connection = connectionPool.getConnection();
             var preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}
