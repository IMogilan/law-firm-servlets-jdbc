package com.mogilan.service.impl;

import com.mogilan.exception.EntityNotFoundException;
import com.mogilan.repository.TaskDao;
import com.mogilan.service.LawyerService;
import com.mogilan.service.TaskService;
import com.mogilan.servlet.dto.LawyerDto;
import com.mogilan.servlet.dto.TaskDto;
import com.mogilan.servlet.mapper.TaskMapper;

import java.util.List;
import java.util.Objects;

public class TaskServiceImpl implements TaskService {
    private final TaskDao taskDao;
    private final TaskMapper taskMapper;
    private final LawyerService lawyerService;

    public TaskServiceImpl(TaskDao taskDao, TaskMapper taskMapper, LawyerService lawyerService) {
        this.taskDao = taskDao;
        this.taskMapper = taskMapper;
        this.lawyerService = lawyerService;
    }

    @Override
    public TaskDto create(TaskDto newTaskDto) {
        Objects.requireNonNull(newTaskDto);

        var task = taskMapper.toEntity(newTaskDto);
        var savedTask = taskDao.save(task);
        var createdTaskDto = taskMapper.toDto(savedTask);

        return createdTaskDto;
    }

    @Override
    public List<TaskDto> readAll() {
        return taskMapper.toDtoList(taskDao.findAll());
    }

    @Override
    public List<TaskDto> readAllByClientId(Long clientId) {
        Objects.requireNonNull(clientId);

        var tasks = taskDao.findAllByClientId(clientId);
        return taskMapper.toDtoList(tasks);
    }

    @Override
    public List<TaskDto> readAllByLawyerId(Long lawyerId) {
        Objects.requireNonNull(lawyerId);

        var tasks = taskDao.findAllByLawyerId(lawyerId);
        return taskMapper.toDtoList(tasks);
    }

    @Override
    public boolean isLawyerResponsibleForTask(Long taskId, Long lawyerId) {
        var lawyerDtoList = lawyerService.readAllByTaskId(taskId);
        if (lawyerDtoList.isEmpty()) {
            return false;
        }
        var lawyersIds = lawyerDtoList.stream().map(LawyerDto::getId).toList();
        return lawyersIds.contains(lawyerId);
    }

    @Override
    public TaskDto readById(Long id) {
        Objects.requireNonNull(id);

        var task = taskDao.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Task with id = " + id + " not found"));
        return taskMapper.toDto(task);
    }

    @Override
    public void update(Long id, TaskDto taskDto) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(taskDto);
        if (taskDao.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Task with id = " + id + " not found");
        }

        var task = taskMapper.toEntity(taskDto);
        task.setId(id);
        taskDao.update(task);
    }

    @Override
    public void deleteById(Long id) {
        Objects.requireNonNull(id);
        if (taskDao.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Task with id = " + id + " not found");
        }
        taskDao.delete(id);
    }
}
