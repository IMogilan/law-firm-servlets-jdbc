package com.mogilan.service;

import com.mogilan.model.Task;
import com.mogilan.servlet.dto.TaskDto;

import java.util.List;

public interface TaskService extends CrudService<TaskDto, Long> {
    List<TaskDto> readAllByClientId(Long clientId);

    List<TaskDto> readAllByLawyerId(Long lawyerId);

    boolean isLawyerResponsibleForTask(Long taskId, Long lawyerId);
}
