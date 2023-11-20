package com.mogilan.repository.mapper.impl;

import com.mogilan.model.*;
import com.mogilan.repository.ContactDetailsDao;
import com.mogilan.repository.LawyerDao;
import com.mogilan.repository.impl.ContactDetailsDaoImpl;
import com.mogilan.repository.impl.LawyerDaoImpl;
import com.mogilan.servlet.dto.JobTitle;
import com.mogilan.servlet.dto.TaskPriority;
import com.mogilan.servlet.dto.TaskStatus;
import com.mogilan.exception.DaoException;
import com.mogilan.repository.mapper.TaskResultSetMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskResultSetMapperImpl implements TaskResultSetMapper {

    private static final TaskResultSetMapperImpl INSTANCE = new TaskResultSetMapperImpl();

    private TaskResultSetMapperImpl() {
    }

    @Override
    public Task map(ResultSet resultSet) {
        try {
            Task task = getTask(resultSet);
            do {
                Lawyer lawyer = getLawyer(resultSet, task);
                if (lawyer != null) {
                    task.getLawyers().add(lawyer);
                }
            }
            while (resultSet.next());
            return task;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Task> toTaskList(ResultSet resultSet) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        long prevTaskId = 0L;
        while (resultSet.next()) {
            try {
                var taskId = resultSet.getLong("id");

                if (taskId != prevTaskId) {
                    prevTaskId = taskId;

                    Task task = getTask(resultSet);
                    tasks.add(task);
                }
                var currentTask = tasks.get(tasks.size() - 1);

                Lawyer lawyer = getLawyer(resultSet, currentTask);
                if (lawyer != null) {
                    currentTask.getLawyers().add(lawyer);
                }

            } catch (SQLException e) {
                throw new DaoException(e);
            }
        }
        return tasks;
    }

    private static Task getTask(ResultSet resultSet) throws SQLException {
        var client = new Client(
                resultSet.getObject("client_id", Long.class),
                resultSet.getString("client_name"),
                resultSet.getObject("client_description", String.class)
        );

        var task = new Task(
                resultSet.getLong("id"),
                resultSet.getString("title"),
                resultSet.getObject("task_description", String.class),
                TaskPriority.valueOf(resultSet.getString("priority")),
                TaskStatus.valueOf(resultSet.getString("status")),
                resultSet.getDate("receipt_date").toLocalDate(),
                resultSet.getDate("due_date").toLocalDate(),
                resultSet.getObject("completion_date", Date.class) == null ? null :
                        resultSet.getObject("completion_date", Date.class).toLocalDate(),
                resultSet.getObject("hours_spent_on_task", Double.class),
                client.getId() == null ? null : client,
                new ArrayList<>()
        );
        return task;
    }

    private static Lawyer getLawyer(ResultSet resultSet, Task currentTask) throws SQLException {
        var contactDetails = resultSet.getObject("contact_details_id", Long.class) == null ?
                null :
                new ContactDetails(
                        resultSet.getObject("contact_details_id", Long.class),
                        resultSet.getObject("address", String.class),
                        resultSet.getObject("tel_number", String.class),
                        resultSet.getObject("mob_number", String.class),
                        resultSet.getObject("fax_number", String.class),
                        resultSet.getObject("email", String.class));

        var lawFirm = resultSet.getObject("law_firm_id", Long.class) == null ?
                null :
                new LawFirm(
                        resultSet.getObject("law_firm_id", Long.class),
                        resultSet.getString("law_firm_name"),
                        resultSet.getObject("company_start_day", Date.class) == null ? null :
                                resultSet.getObject("company_start_day", Date.class).toLocalDate()
                );

        var lawyer = resultSet.getObject("lawyer_id", Long.class) == null ?
                null :
                new Lawyer(
                resultSet.getObject("lawyer_id", Long.class),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getString("job_title") == null ? null : JobTitle.valueOf(resultSet.getString("job_title")),
                resultSet.getDouble("hourly_rate"),
                        lawFirm,
                        contactDetails,
                List.of(currentTask)
        );
        return lawyer;
    }

    public static TaskResultSetMapperImpl getInstance() {
        return INSTANCE;
    }
}
