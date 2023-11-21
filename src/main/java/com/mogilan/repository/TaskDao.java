package com.mogilan.repository;

import com.mogilan.model.Task;

import java.sql.Connection;
import java.util.List;

public interface TaskDao extends CrudDao<Task, Long> {
    List<Task> findAllByClientId(Long clientId);

    List<Task> findAllByClientId(Long clientId, Connection connection);

    List<Task> findAllByLawyerId(Long lawyerId);

    List<Task> findAllByLawyerId(Long lawyerId, Connection connection);
}
