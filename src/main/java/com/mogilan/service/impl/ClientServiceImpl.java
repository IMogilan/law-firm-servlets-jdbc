package com.mogilan.service.impl;

import com.mogilan.exception.EntityNotFoundException;
import com.mogilan.model.Client;
import com.mogilan.repository.ClientDao;
import com.mogilan.service.ClientService;
import com.mogilan.service.TaskService;
import com.mogilan.servlet.dto.ClientDto;
import com.mogilan.servlet.dto.SimpleTaskDto;
import com.mogilan.servlet.dto.TaskDto;
import com.mogilan.servlet.mapper.ClientMapper;
import com.mogilan.servlet.mapper.SimpleTaskMapper;
import com.mogilan.servlet.mapper.TaskMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClientServiceImpl implements ClientService {
    private final ClientDao clientDao;
    private final ClientMapper clientMapper;
    private final TaskService taskService;
    private final TaskMapper taskMapper;
    private final SimpleTaskMapper simpleTaskMapper;

    public ClientServiceImpl(ClientDao clientDao, ClientMapper clientMapper, TaskService taskService, SimpleTaskMapper simpleTaskMapper, TaskMapper taskMapper) {
        this.clientDao = clientDao;
        this.clientMapper = clientMapper;
        this.taskService = taskService;
        this.simpleTaskMapper = simpleTaskMapper;
        this.taskMapper = taskMapper;
    }

    @Override
    public ClientDto create(ClientDto newClientDto) {
        Objects.requireNonNull(newClientDto);

        var client = clientMapper.toEntity(newClientDto);
        var savedClient = clientDao.save(client);
        var createdClientDto = clientMapper.toDto(savedClient);
        var taskDtoList = simpleTaskMapper.toTaskDtoList(newClientDto.getTasks());
        createNewTasks(taskDtoList, createdClientDto);
        var simpleTaskDtoList = simpleTaskMapper.toSimpleTaskDtoList(taskService.readAllByClientId(createdClientDto.getId()));
        createdClientDto.setTasks(simpleTaskDtoList);
        return createdClientDto;
    }


    @Override
    public List<ClientDto> readAll() {
        return clientMapper.toDtoList(clientDao.findAll());
    }

    @Override
    public ClientDto readById(Long id) {
        Objects.requireNonNull(id);

        var client = clientDao.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Client with id = " + id + " not found"));
        return clientMapper.toDto(client);
    }

    @Override
    public void update(Long id, ClientDto clientDto) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(clientDto);
        if (clientDao.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Client with id = " + id + " not found");
        }

        clientDto.setId(id);
        var client = clientMapper.toEntity(clientDto);
        clientDao.update(client);

        updateTaskListOfThisClient(id, clientDto);
    }

    @Override
    public void deleteById(Long id) {
        Objects.requireNonNull(id);
        if (clientDao.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Client with id = " + id + " not found");
        }
        clientDao.delete(id);
    }

    @Override
    public boolean existsById(Long id) {
        Objects.requireNonNull(id);
        return clientDao.findById(id).isPresent();
    }

    @Override
    public boolean existsByName(String name) {
        Objects.requireNonNull(name);
        return clientDao.findByName(name).isPresent();
    }

    private void createNewTasks(List<TaskDto> taskDtoList, ClientDto createdClientDto) {
        if (taskDtoList != null) {
            taskDtoList.forEach(taskDto -> {
                taskDto.setClient(createdClientDto);
                taskService.create(taskDto);
            });
        }
    }

    private void updateTaskListOfThisClient(Long id, ClientDto clientDto) {
        var client = clientMapper.toEntity(clientDto);
        var tasksList = client.getTasks();
        var newTasksList = taskMapper.toDtoList(tasksList);
        var prevTaskList = taskService.readAllByClientId(id);
        createAllIfPrevTaskListEmpty(clientDto, newTasksList, prevTaskList);
        deleteAllIfNewTaskListEmpty(newTasksList, prevTaskList);
        updateTaskList(clientDto, newTasksList, prevTaskList);
    }

    private void createAllIfPrevTaskListEmpty(ClientDto clientDto, List<TaskDto> newTasksList, List<TaskDto> prevTaskList) {
        if ((newTasksList != null && !newTasksList.isEmpty()) && (prevTaskList == null || prevTaskList.isEmpty())) {
            createNewTasks(newTasksList, clientDto);
        }
    }

    private void deleteAllIfNewTaskListEmpty(List<TaskDto> newTasksList, List<TaskDto> prevTaskList) {
        if ((newTasksList == null || newTasksList.isEmpty()) && (prevTaskList != null && !prevTaskList.isEmpty())) {
            prevTaskList.forEach(taskDto -> taskService.deleteById(taskDto.getId()));
        }
    }

    private void updateTaskList(ClientDto clientDto, List<TaskDto> newTasksList, List<TaskDto> prevTaskList) {
        if ((newTasksList != null && !newTasksList.isEmpty()) && (prevTaskList != null && !prevTaskList.isEmpty())) {
            var newTasksWithoutId = newTasksList.stream().filter(taskDto -> taskDto.getId() == null).toList();
            createNewTasks(newTasksWithoutId, clientDto);

            var taskWithIdList = newTasksList.stream().filter(taskDto -> taskDto.getId() != null).toList();
            var tasksWithIdMap = newTasksList.stream().collect(Collectors.toMap(TaskDto::getId, taskDto -> taskDto));

            var newTaskListIds = taskWithIdList.stream().map(TaskDto::getId).toList();
            var prevTaskListIds = prevTaskList.stream().map(TaskDto::getId).toList();

            var retainedTaskIds = new HashSet<>(newTaskListIds);
            retainedTaskIds.retainAll(prevTaskListIds);
            retainedTaskIds.forEach(taskId -> taskService.update(taskId, tasksWithIdMap.get(taskId)));

            var removedTaskIds = new HashSet<>(prevTaskListIds);
            removedTaskIds.removeAll(retainedTaskIds);
            removedTaskIds.forEach(taskService::deleteById);

            var addedTaskIds = new HashSet<>(newTaskListIds);
            addedTaskIds.removeAll(retainedTaskIds);
            addedTaskIds.forEach(taskId -> {
                var addedTask = tasksWithIdMap.get(taskId);
                addedTask.setClient(clientDto);
                taskService.update(taskId, addedTask);
            });
        }
    }
}
