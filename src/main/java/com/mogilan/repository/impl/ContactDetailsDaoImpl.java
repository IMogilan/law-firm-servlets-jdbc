package com.mogilan.repository.impl;

import com.mogilan.db.ConnectionPool;
import com.mogilan.repository.ContactDetailsDao;
import com.mogilan.model.*;
import com.mogilan.exception.DaoException;
import com.mogilan.repository.mapper.ContactDetailsResultSetMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContactDetailsDaoImpl implements ContactDetailsDao {
    private static final String FIND_ALL_SQL = """
            SELECT  id,
                    address,
                    tel_number,
                    mob_number,
                    fax_number,
                    email
            FROM contact_details
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO contact_details (id, address, tel_number, mob_number, fax_number, email)
            VALUES (?,?,?,?,?,?)
            """;
    private static final String UPDATE_SQL = """
            UPDATE contact_details
            SET address = ?,
                tel_number = ?,
                mob_number = ?,
                fax_number = ?,
                email = ?
            WHERE id = ?
            """;
    private static final String DELETE_SQL = """
            DELETE
            FROM contact_details
            WHERE id = ?
            """;
    private final ConnectionPool connectionPool;
    private final ContactDetailsResultSetMapper resultSetMapper;

    public ContactDetailsDaoImpl(ConnectionPool connectionPool, ContactDetailsResultSetMapper resultSetMapper) {
        this.connectionPool = connectionPool;
        this.resultSetMapper = resultSetMapper;
    }

    @Override
    public List<ContactDetails> findAll() {
        try (var connection = connectionPool.getConnection();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            List<ContactDetails> contactDetailsList = new ArrayList<>();

            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                var contactDetails = resultSetMapper.map(resultSet);
                contactDetailsList.add(contactDetails);
            }
            return contactDetailsList;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<ContactDetails> findById(Long id) {
        try (var connection = connectionPool.getConnection()) {
            return findById(id, connection);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<ContactDetails> findById(Long id, Connection connection) {
        try (var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            ContactDetails contactDetails = null;

            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                contactDetails = resultSetMapper.map(resultSet);
            }

            return Optional.ofNullable(contactDetails);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public ContactDetails save(ContactDetails entity) {
        var id = entity.getId();
        if (id != null && findById(id).isPresent()) {
            update(entity);
            return findById(id).get();
        }
        try (var connection = connectionPool.getConnection();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, entity.getId());
            preparedStatement.setString(2, entity.getAddress());
            preparedStatement.setString(3, entity.getTelNumber());
            preparedStatement.setString(4, entity.getMobNumber());
            preparedStatement.setString(5, entity.getFaxNumber());
            preparedStatement.setString(6, entity.getEmail());

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
    public boolean update(ContactDetails entity) {
        try (var connection = connectionPool.getConnection();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setString(1, entity.getAddress());
            preparedStatement.setString(2, entity.getTelNumber());
            preparedStatement.setString(3, entity.getMobNumber());
            preparedStatement.setString(4, entity.getFaxNumber());
            preparedStatement.setString(5, entity.getEmail());
            preparedStatement.setLong(6, entity.getId());

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
