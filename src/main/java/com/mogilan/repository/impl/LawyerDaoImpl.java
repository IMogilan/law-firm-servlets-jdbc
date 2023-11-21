package com.mogilan.repository.impl;

import com.mogilan.model.Task;
import com.mogilan.repository.LawyerDao;
import com.mogilan.model.Lawyer;
import com.mogilan.exception.DaoException;
import com.mogilan.repository.mapper.LawyerResultSetMapper;
import com.mogilan.repository.mapper.impl.LawyerResultSetMapperImpl;
import com.mogilan.util.ConnectionPool;

import java.sql.*;
import java.util.*;

public class LawyerDaoImpl implements LawyerDao {

    private static final LawyerDaoImpl INSTANCE = new LawyerDaoImpl();
    private static final String FIND_ALL_SQL = """
            SELECT  lawyers.id,
                    first_name,
                    last_name,
                    job_title,
                    hourly_rate,
                    law_firm_id,
                    lf.name,
                    lf.company_start_day
            FROM lawyers
            LEFT JOIN law_firms lf on lf.id = lawyers.law_firm_id
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE lawyers.id = ?
            """;
    private static final String FIND_ALL_BY_LAW_FIRM_ID = FIND_ALL_SQL + """
            WHERE law_firm_id = ?
            """;
    private static final String FIND_ALL_BY_TASK_ID = """
            SELECT  lawyers.id,
                    first_name,
                    last_name,
                    job_title,
                    hourly_rate,
                    law_firm_id,
                    lf.name,
                    lf.company_start_day,
                    task_id
            FROM lawyers
            LEFT JOIN law_firms lf on lf.id = lawyers.law_firm_id
            LEFT JOIN lawyers_tasks lt on lawyers.id = lt.lawyer_id
            WHERE task_id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO lawyers (first_name, last_name, job_title, hourly_rate, law_firm_id)
            VALUES (?,?,?,?,?)
            """;
    private static final String UPDATE_SQL = """
            UPDATE lawyers
            SET first_name = ?,
                last_name = ?,
                job_title = ?,
                hourly_rate = ?,
                law_firm_id = ?
            WHERE id = ?
            """;
    private static final String DELETE_SQL = """
            DELETE
            FROM lawyers
            WHERE id = ?
            """;
    private final LawyerResultSetMapper resultSetMapper = LawyerResultSetMapperImpl.getInstance();

    private LawyerDaoImpl() {
    }

    @Override
    public List<Lawyer> findAll() {
        try (var connection = ConnectionPool.getConnection();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.toLawyerList(resultSet);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Lawyer> findAllByLawFirmId(Long lawFirmId) {
        try (var connection = ConnectionPool.getConnection()) {
            return findAllByLawFirmId(lawFirmId, connection);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Lawyer> findAllByLawFirmId(Long lawFirmId, Connection connection) {
        try (var preparedStatement = connection.prepareStatement(FIND_ALL_BY_LAW_FIRM_ID)) {
            preparedStatement.setLong(1, lawFirmId);
            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.toLawyerList(resultSet);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Lawyer> findAllByTaskId(Long taskId) {
        try (var connection = ConnectionPool.getConnection()) {
            return findAllByTaskId(taskId, connection);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Lawyer> findAllByTaskId(Long taskId, Connection connection) {
        try (var preparedStatement = connection.prepareStatement(FIND_ALL_BY_TASK_ID)) {
            preparedStatement.setLong(1, taskId);
            var resultSet = preparedStatement.executeQuery();

            return resultSetMapper.toLawyerList(resultSet);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Lawyer> findById(Long id) {
        try (var connection = ConnectionPool.getConnection();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            Lawyer lawyer = null;

            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                lawyer = resultSetMapper.map(resultSet);
            }

            return Optional.ofNullable(lawyer);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Lawyer save(Lawyer entity) {
        var id = entity.getId();
        if (id != null && findById(id).isPresent()) {
            update(entity);
            return findById(id).get();
        }
        try (var connection = ConnectionPool.getConnection();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());
            preparedStatement.setString(3, entity.getJobTitle().name());
            preparedStatement.setDouble(4, entity.getHourlyRate());
            preparedStatement.setObject(5, entity.getLawFirm() == null ? null : entity.getLawFirm().getId(), Types.BIGINT);
            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.setId(generatedKeys.getLong("id"));
            }

            var taskIds = getTaskIds(entity.getTasks());
            saveNewLinksToTasks(taskIds, entity.getId(), connection);

            return entity;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(Lawyer entity) {
        try (var connection = ConnectionPool.getConnection();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());
            preparedStatement.setString(3, entity.getJobTitle().name());
            preparedStatement.setDouble(4, entity.getHourlyRate());
            preparedStatement.setObject(5, entity.getLawFirm() == null ? null : entity.getLawFirm().getId(), Types.BIGINT);
            preparedStatement.setLong(6, entity.getId());

            updateLinksToTasks(entity, connection);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (var connection = ConnectionPool.getConnection();
             var preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public static LawyerDaoImpl getInstance() {
        return INSTANCE;
    }

    private void saveNewLinksToTasks(List<Long> taskIds, Long lawyerId, Connection connection) throws SQLException {
        if (!taskIds.isEmpty()) {
            try (var preparedStatementLinks = connection.prepareStatement(getSaveNewLinksToTasksSql(taskIds))) {
                int firstIndex = 1;
                int secondIndex = 2;
                for (Long taskId : taskIds) {
                    preparedStatementLinks.setLong(firstIndex, lawyerId);
                    preparedStatementLinks.setLong(secondIndex, taskId);
                    firstIndex += 2;
                    secondIndex += 2;
                }
                preparedStatementLinks.execute();
            }
        }
    }

    private void updateLinksToTasks(Lawyer entity, Connection connection) throws SQLException {
        var lawyerId = entity.getId();
        var currentTaskIds = getCurrentLinkedTaskIds(connection, lawyerId);

        var newTaskListIds = getTaskIds(entity.getTasks());
        var retainedTaskIds = new ArrayList<>(newTaskListIds);
        retainedTaskIds.retainAll(currentTaskIds);

        var deletedTaskIds = new ArrayList<>(currentTaskIds);
        deletedTaskIds.removeAll(retainedTaskIds);
        deleteLinksToTasks(deletedTaskIds, connection);

        var addedTaskIds = new ArrayList<>(newTaskListIds);
        addedTaskIds.removeAll(retainedTaskIds);
        saveNewLinksToTasks(addedTaskIds, lawyerId, connection);
    }

    private List<Long> getTaskIds(List<Task> tasks) {
        if (tasks == null) {
            return new ArrayList<>();
        }
        return tasks.stream().filter(Objects::nonNull).map(Task::getId).filter(Objects::nonNull).toList();
    }

    private List<Long> getCurrentLinkedTaskIds(Connection connection, Long lawyerId) throws SQLException {
        String findCurrentLinks = """
                SELECT task_id
                from lawyers_tasks
                WHERE lawyer_id = ?
                """;
        try (var findCurrentLinksStatement = connection.prepareStatement(findCurrentLinks)) {
            findCurrentLinksStatement.setLong(1, lawyerId);

            var currentTaskIdsResultSet = findCurrentLinksStatement.executeQuery();
            List<Long> currentTaskIds = new ArrayList<>();
            while (currentTaskIdsResultSet.next()) {
                var taskId = currentTaskIdsResultSet.getLong("task_id");
                currentTaskIds.add(taskId);
            }
            return currentTaskIds;
        }
    }

    private void deleteLinksToTasks(List<Long> removedTaskIds, Connection connection) throws SQLException {
        if (!removedTaskIds.isEmpty()) {
            try (var statement = connection.createStatement()) {
                statement.execute(getDeleteLinksToTasksSql(removedTaskIds));
            }
        }
    }

    private String getSaveNewLinksToTasksSql(List<Long> taskIds) {
        String firstPart = """
                    INSERT INTO lawyers_tasks(lawyer_id, task_id)
                VALUES (? , ?)""";
        String secondPart = ", (?, ?)";

        StringBuilder result = new StringBuilder(firstPart);
        for (int i = 1; i < taskIds.size(); i++) {
            result.append(secondPart);
        }
        return result.toString();
    }

    private String getDeleteLinksToTasksSql(List<Long> removedIds) {
        String firstPart = """
                DELETE
                FROM lawyers_tasks
                WHERE task_id in (""";
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


//    private void saveNewLinksToTasks(Lawyer entity, Connection connection) throws SQLException {
//        var tasksWithId = getTasksWithId(entity);
//        if(!tasksWithId.isEmpty()){
//            try (var preparedStatementLinks = connection.prepareStatement(getSaveNewLinksToTasksSql(tasksWithId))) {
//                int firstIndex = 1;
//                int secondIndex = 2;
//                for (Task task : tasksWithId) {
//                    preparedStatementLinks.setLong(firstIndex++, entity.getId());
//                    preparedStatementLinks.setLong(secondIndex++, task.getId());
//                }
//                preparedStatementLinks.execute();
//            }
//        }
//    }
//
//    private List<Task> getTasksWithId(Lawyer entity) {
//        if (entity.getTasks() == null) {
//            return new ArrayList<>();
//        }
//        return entity.getTasks().stream().filter(Objects::nonNull).filter(task -> task.getId() != null).toList();
//    }
//
//    private String getSaveNewLinksToTasksSql(List<Task> tasks){
//        String firstPart = """
//            INSERT INTO lawyers_tasks(lawyer_id, task_id)
//            VALUES (? , ?)""";
//        String secondPart = ", (?, ?)";
//
//        StringBuilder result = new StringBuilder(firstPart);
//        for (int i = 1; i < tasks.size(); i++) {
//            result.append(secondPart);
//        }
//        return result.toString();
//    }

}
