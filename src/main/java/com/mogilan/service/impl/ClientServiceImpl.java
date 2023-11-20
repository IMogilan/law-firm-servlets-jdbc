package com.mogilan.service.impl;

import com.mogilan.exception.EntityNotFoundException;
import com.mogilan.repository.ClientDao;
import com.mogilan.repository.impl.ClientDaoImpl;
import com.mogilan.service.ClientService;
import com.mogilan.service.TaskService;
import com.mogilan.servlet.dto.ClientDto;
import com.mogilan.servlet.dto.TaskDto;
import com.mogilan.servlet.mapper.ClientMapper;
import com.mogilan.servlet.mapper.impl.ClientMapperImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClientServiceImpl implements ClientService {
    private static final ClientServiceImpl INSTANCE = new ClientServiceImpl();
    private final ClientDao clientDao = ClientDaoImpl.getInstance();
    private final ClientMapper clientMapper = ClientMapperImpl.getInstance();
    private final static TaskService taskService = TaskServiceImpl.getInstance();

    private ClientServiceImpl() {
    }

    @Override
    public ClientDto create(ClientDto newClientDto) {
        Objects.requireNonNull(newClientDto);

        var client = clientMapper.toEntity(newClientDto);
        var savedClient = clientDao.save(client);
        var createdClientDto = clientMapper.toDto(savedClient);

        createNewTasks(newClientDto.getTasks(), createdClientDto);
        createdClientDto.setTasks(taskService.readAllByClientId(createdClientDto.getId()));
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

    public static ClientServiceImpl getInstance() {
        return INSTANCE;
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
        var newTasksList = clientDto.getTasks();
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

            newTasksList.removeAll(newTasksWithoutId);
            var tasksWithId = newTasksList.stream().collect(Collectors.toMap(TaskDto::getId, taskDto -> taskDto));

            var newTaskListIds = newTasksList.stream().map(TaskDto::getId).toList();
            var prevTaskListIds = prevTaskList.stream().map(TaskDto::getId).toList();

            var retainedTaskIds = new HashSet<>(newTaskListIds);
            retainedTaskIds.retainAll(prevTaskListIds);
            retainedTaskIds.forEach(taskId -> taskService.update(taskId, tasksWithId.get(taskId)));

            var removedTaskIds = new HashSet<>(prevTaskListIds);
            removedTaskIds.removeAll(retainedTaskIds);
            removedTaskIds.forEach(taskService::deleteById);

            var addedTaskIds = new HashSet<>(newTaskListIds);
            addedTaskIds.removeAll(retainedTaskIds);
            addedTaskIds.forEach(taskId -> {
                var addedTask = tasksWithId.get(taskId);
                addedTask.setClient(clientDto);
                taskService.update(taskId, addedTask);
            });
        }
    }
}
