package com.mogilan.repository.impl;

import com.mogilan.db.ConnectionPool;
import com.mogilan.model.Lawyer;
import com.mogilan.repository.TaskDao;
import com.mogilan.model.Task;
import com.mogilan.exception.DaoException;
import com.mogilan.repository.mapper.impl.TaskResultSetMapperImpl;
import com.mogilan.db.impl.ConnectionPoolImpl;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class TaskDaoImpl implements TaskDao {
    private static final String FIND_ALL_SQL = """
             SELECT t.id,
                   t.title,
                   t.description task_description,
                   t.priority,
                   t.status,
                   t.receipt_date,
                   t.due_date,
                   t.completion_date,
                   t.hours_spent_on_task,
                   t.client_id,
                   c.name        client_name,
                   c.description client_description,
                   lt.lawyer_id,
                   l.first_name,
                   l.last_name,
                   l.job_title,
                   l.hourly_rate,
                   l.law_firm_id,
                   lf.name       law_firm_name,
                   lf.company_start_day,
                   cd.id contact_details_id,
                   address,
                   tel_number,
                   mob_number,
                   fax_number,
                   email
            FROM tasks t
                     LEFT JOIN clients c on c.id = t.client_id
                     LEFT JOIN lawyers_tasks lt on t.id = lt.task_id
                     LEFT JOIN lawyers l on l.id = lt.lawyer_id
                     LEFT JOIN law_firms lf on lf.id = l.law_firm_id
                     LEFT JOIN contact_details cd on l.id = cd.id
             """;
    private static final String FIND_ALL_BY_CLIENT_ID = FIND_ALL_SQL + """
            WHERE t.client_id = ?
            """;

    private static final String FIND_ALL_BY_LAWYER_ID = FIND_ALL_SQL + """
            WHERE lt.lawyer_id = ?
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE t.id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO tasks (title, description, priority, status, receipt_date, due_date, completion_date, hours_spent_on_task, client_id)
            VALUES (?,?,?,?,?,?,?,?,?)
            """;
    private static final String UPDATE_SQL = """
            UPDATE tasks
            SET title = ?,
                description = ?,
                priority = ?,
                status = ?,
                receipt_date = ?,
                due_date = ?,
                completion_date = ?,
                hours_spent_on_task = ?,
                client_id = ?
            WHERE id = ?
            """;
    private static final String DELETE_SQL = """
            DELETE
            FROM tasks
            WHERE id = ?
            """;

    private final ConnectionPool connectionPool;
    private final TaskResultSetMapperImpl resultSetMapper;

    public TaskDaoImpl(ConnectionPool connectionPool, TaskResultSetMapperImpl resultSetMapper) {
        this.connectionPool = connectionPool;
        this.resultSetMapper = resultSetMapper;
    }

    @Override
    public List<Task> findAll() {
        try (var connection = connectionPool.getConnection();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.toTaskList(resultSet);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Task> findAllByClientId(Long clientId) {
        try (var connection = connectionPool.getConnection()) {

            return findAllByClientId(clientId, connection);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Task> findAllByClientId(Long clientId, Connection connection) {
        try (var preparedStatement = connection.prepareStatement(FIND_ALL_BY_CLIENT_ID)) {
            preparedStatement.setLong(1, clientId);
            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.toTaskList(resultSet);

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Task> findAllByLawyerId(Long lawyerId) {
        try (var connection = connectionPool.getConnection()) {

            return findAllByLawyerId(lawyerId, connection);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Task> findAllByLawyerId(Long lawyerId, Connection connection) {
        try (var preparedStatement = connection.prepareStatement(FIND_ALL_BY_LAWYER_ID)) {
            preparedStatement.setLong(1, lawyerId);
            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.toTaskList(resultSet);

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Task> findById(Long id) {
        try (var connection = connectionPool.getConnection();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            Task task = null;

            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                task = resultSetMapper.map(resultSet);
            }

            return Optional.ofNullable(task);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Task save(Task entity) {
        var id = entity.getId();
        if (id != null && findById(id).isPresent()) {
            update(entity);
            return findById(id).get();
        }
        try (var connection = connectionPool.getConnection();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, entity.getTitle());
            preparedStatement.setString(2, entity.getDescription());
            preparedStatement.setString(3, entity.getPriority().name());
            preparedStatement.setString(4, entity.getStatus().name());
            preparedStatement.setDate(5, Date.valueOf(entity.getReceiptDate()));
            preparedStatement.setDate(6, Date.valueOf(entity.getDueDate()));
            preparedStatement.setObject(7, entity.getCompletionDate() == null ? null : Date.valueOf(entity.getCompletionDate()), Types.DATE);
            preparedStatement.setObject(8, entity.getHoursSpentOnTask(), Types.DECIMAL);
            preparedStatement.setObject(9, entity.getClient() == null ? null : entity.getClient().getId(), Types.BIGINT);
            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.setId(generatedKeys.getLong("id"));
            }

            var lawyerIds = getLawyerIds(entity.getLawyers());
            saveNewLinksToLawyers(lawyerIds, entity.getId(), connection);
            return entity;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(Task entity) {
        try (var connection = connectionPool.getConnection();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, entity.getTitle());
            preparedStatement.setString(2, entity.getDescription());
            preparedStatement.setString(3, entity.getPriority().name());
            preparedStatement.setString(4, entity.getStatus().name());
            preparedStatement.setDate(5, Date.valueOf(entity.getReceiptDate()));
            preparedStatement.setDate(6, Date.valueOf(entity.getDueDate()));
            preparedStatement.setObject(7, entity.getCompletionDate() == null ? null : Date.valueOf(entity.getCompletionDate()), Types.DATE);
            preparedStatement.setObject(8, entity.getHoursSpentOnTask(), Types.DECIMAL);
            preparedStatement.setObject(9, entity.getClient() == null ? null : entity.getClient().getId(), Types.BIGINT);
            preparedStatement.setLong(10, entity.getId());

            updateLinksToLawyers(entity, connection);

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

    private void saveNewLinksToLawyers(List<Long> lawyerIds, Long taskId, Connection connection) throws SQLException {
        if (!lawyerIds.isEmpty()) {
            try (var preparedStatementLinks = connection.prepareStatement(getSaveNewLinksToLawyersSql(lawyerIds))) {
                int firstIndex = 1;
                int secondIndex = 2;
                for (Long lawyerId : lawyerIds) {
                    preparedStatementLinks.setLong(firstIndex, lawyerId);
                    preparedStatementLinks.setLong(secondIndex, taskId);
                    firstIndex += 2;
                    secondIndex += 2;
                }
                preparedStatementLinks.execute();
            }
        }
    }

    private void updateLinksToLawyers(Task entity, Connection connection) throws SQLException {
        var taskId = entity.getId();
        var currentLawyersIds = getCurrentLinkedLawyerIds(connection, taskId);

        var newLawyerListIds = getLawyerIds(entity.getLawyers());
        var retainedLawyersId = new ArrayList<>(newLawyerListIds);
        retainedLawyersId.retainAll(currentLawyersIds);

        var deletedLawyersIds = new ArrayList<>(currentLawyersIds);
        deletedLawyersIds.removeAll(retainedLawyersId);
        deleteLinksToLawyers(deletedLawyersIds, connection);

        var addedLawyersIds = new ArrayList<>(newLawyerListIds);
        addedLawyersIds.removeAll(retainedLawyersId);
        saveNewLinksToLawyers(addedLawyersIds, taskId, connection);
    }

    private List<Long> getLawyerIds(List<Lawyer> lawyers) {
        if (lawyers == null) {
            return new ArrayList<>();
        }
        return lawyers.stream().filter(Objects::nonNull).map(Lawyer::getId).filter(Objects::nonNull).toList();
    }

    private List<Long> getCurrentLinkedLawyerIds(Connection connection, Long taskId) throws SQLException {
        String findCurrentLinks = """
                SELECT lawyer_id
                from lawyers_tasks
                WHERE task_id = ?
                """;
        try (var findCurrentLinksStatement = connection.prepareStatement(findCurrentLinks)) {
            findCurrentLinksStatement.setLong(1, taskId);

            var currentLawyersIdsResultSet = findCurrentLinksStatement.executeQuery();
            List<Long> currentLawyersIds = new ArrayList<>();
            while (currentLawyersIdsResultSet.next()) {
                var lawyerId = currentLawyersIdsResultSet.getLong("lawyer_id");
                currentLawyersIds.add(lawyerId);
            }
            return currentLawyersIds;
        }
    }

    private void deleteLinksToLawyers(List<Long> removedLawyersIds, Connection connection) throws SQLException {
        if (!removedLawyersIds.isEmpty()) {
            try (var statement = connection.createStatement()) {
                statement.execute(getRemoveLinksToLawyersSql(removedLawyersIds));
            }
        }
    }

    private String getSaveNewLinksToLawyersSql(List<Long> lawyerIds) {
        String firstPart = """
                    INSERT INTO lawyers_tasks(lawyer_id, task_id)
                VALUES (? , ?)""";
        String secondPart = ", (?, ?)";

        StringBuilder result = new StringBuilder(firstPart);
        for (int i = 1; i < lawyerIds.size(); i++) {
            result.append(secondPart);
        }
        return result.toString();
    }

    private String getRemoveLinksToLawyersSql(List<Long> removedIds) {
        String firstPart = """
                DELETE
                FROM lawyers_tasks
                WHERE lawyer_id in (""";
        String lastPart = ")";

        StringBuilder builder = new StringBuilder(firstPart);
        for (int i = 0; i < removedIds.size(); i++) {
            builder.append(removedIds.get(i));
            if (i != removedIds.size() - 1) {
                builder.append(", ");
            } else {
                builder.append(lastPart);
            }
        }
        return builder.toString();
    }
}
