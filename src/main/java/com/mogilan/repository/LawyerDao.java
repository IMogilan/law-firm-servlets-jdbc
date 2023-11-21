package com.mogilan.repository;

import com.mogilan.model.Lawyer;

import java.sql.Connection;
import java.util.List;

public interface LawyerDao extends CrudDao<Lawyer, Long> {
    List<Lawyer> findAllByLawFirmId(Long lawFirmId);

    List<Lawyer> findAllByLawFirmId(Long lawFirmId, Connection connection);

    List<Lawyer> findAllByTaskId(Long taskId);

    List<Lawyer> findAllByTaskId(Long taskId, Connection connection);
}
