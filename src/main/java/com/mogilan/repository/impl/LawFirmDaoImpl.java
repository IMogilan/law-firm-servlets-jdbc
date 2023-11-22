package com.mogilan.repository.impl;

import com.mogilan.db.ConnectionPool;
import com.mogilan.repository.LawFirmDao;
import com.mogilan.model.LawFirm;
import com.mogilan.exception.DaoException;
import com.mogilan.repository.mapper.LawFirmResultSetMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LawFirmDaoImpl implements LawFirmDao {
    private static final String FIND_ALL_SQL = """
            SELECT  id,
                    name,
                    company_start_day
            FROM law_firms
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;
    private static final String FIND_BY_NAME_SQL = FIND_ALL_SQL + """
            WHERE name LIKE ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO law_firms (name, company_start_day)
            VALUES (?,?)
            """;
    private static final String UPDATE_SQL = """
            UPDATE law_firms
            SET name = ?,
                company_start_day = ?
            WHERE id = ?
            """;
    private static final String DELETE_SQL = """
            DELETE
            FROM law_firms
            WHERE id = ?
            """;
    private final ConnectionPool connectionPool;
    private final LawFirmResultSetMapper resultSetMapper;

    public LawFirmDaoImpl(ConnectionPool connectionPool, LawFirmResultSetMapper resultSetMapper) {
        this.connectionPool = connectionPool;
        this.resultSetMapper = resultSetMapper;
    }

    @Override
    public List<LawFirm> findAll() {
        try (var connection = connectionPool.getConnection();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            List<LawFirm> lawFirmList = new ArrayList<>();

            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                var lawFirm = resultSetMapper.map(resultSet);
                lawFirmList.add(lawFirm);
            }
            return lawFirmList;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<LawFirm> findById(Long id) {
        try (var connection = connectionPool.getConnection();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            LawFirm lawFirm = null;

            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                lawFirm = resultSetMapper.map(resultSet);
            }

            return Optional.ofNullable(lawFirm);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<LawFirm> findByName(String name) {
        try (var connection = connectionPool.getConnection();
             var preparedStatement = connection.prepareStatement(FIND_BY_NAME_SQL)) {
            LawFirm lawFirm = null;

            preparedStatement.setString(1, name);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                lawFirm = resultSetMapper.map(resultSet);
            }

            return Optional.ofNullable(lawFirm);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public LawFirm save(LawFirm entity) {
        var id = entity.getId();
        if (id != null && findById(id).isPresent()) {
            update(entity);
            return findById(id).get();
        }
        try (var connection = connectionPool.getConnection();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, entity.getName());
            preparedStatement.setDate(2, Date.valueOf(entity.getCompanyStartDay()));
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
    public boolean update(LawFirm entity) {
        try (var connection = connectionPool.getConnection();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setString(1, entity.getName());
            preparedStatement.setDate(2, Date.valueOf(entity.getCompanyStartDay()));
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
