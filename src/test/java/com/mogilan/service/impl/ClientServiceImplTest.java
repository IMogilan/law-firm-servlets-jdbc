package com.mogilan.service.impl;

import com.mogilan.exception.EntityNotFoundException;
import com.mogilan.model.Client;
import com.mogilan.model.Task;
import com.mogilan.repository.ClientDao;
import com.mogilan.service.ClientService;
import com.mogilan.service.TaskService;
import com.mogilan.servlet.dto.ClientDto;
import com.mogilan.servlet.dto.SimpleTaskDto;
import com.mogilan.servlet.dto.TaskDto;
import com.mogilan.servlet.mapper.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    ClientDao clientDao;
    @Mock
    TaskService taskService;
    @Mock
    SimpleTaskMapper simpleTaskMapper;
    @Mock
    TaskMapper taskMapper;
    @Mock
    ClientMapper clientMapper;
    @Captor
    ArgumentCaptor<Client> captor;
    @Captor
    ArgumentCaptor<TaskDto> taskDtoArgumentCaptor;
    ClientService clientService;


    @BeforeEach
    void beforeEach() {
        clientService = new ClientServiceImpl(clientDao, clientMapper, taskService, simpleTaskMapper, taskMapper);
    }

    @Test
    void createSuccess() {
        var dto = getDto();
        var entity = getEntity();
        doReturn(entity).when(clientMapper).toEntity(dto);

        var entityWithId = getEntityWithId();
        doReturn(entityWithId).when(clientDao).save(entity);

        var dtoWithId = getDtoWithId();
        doReturn(dtoWithId).when(clientMapper).toDto(entityWithId);

        var taskDtoList = List.of(new TaskDto(), new TaskDto(), new TaskDto(), new TaskDto(), new TaskDto());
        doReturn(taskDtoList).when(simpleTaskMapper).toTaskDtoList(dto.getTasks());

        doReturn(taskDtoList).when(taskService).readAllByClientId(dtoWithId.getId());
        doReturn(dto.getTasks()).when(simpleTaskMapper).toSimpleTaskDtoList(taskDtoList);

        var actualResult = clientService.create(dto);

        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(dtoWithId.getId());
        assertThat(actualResult.getTasks()).isNotNull();
        assertThat(actualResult.getTasks()).isNotEmpty();
        assertThat(actualResult.getTasks().size()).isEqualTo(taskDtoList.size());
        verify(taskService, times(1)).readAllByClientId(dtoWithId.getId());
        verify(taskService, times(5)).create(any(TaskDto.class));
    }

    @Test
    void createShouldThrowExceptionIfDtoIsNull() {
        ClientDto clientDto = null;
        Assertions.assertThrows(NullPointerException.class, () -> clientService.create(clientDto));
    }

    @Test
    void readAllSuccess() {
        var entityList = getEntityList();
        doReturn(entityList).when(clientDao).findAll();
        var dtoList = getDtoList();
        doReturn(dtoList).when(clientMapper).toDtoList(entityList);

        var actualResult = clientService.readAll();
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).hasSize(entityList.size());
    }

    @Test
    void readById() {
        Long id = 1L;
        var entityWithId = getEntityWithId();
        var dtoWithId = getDtoWithId();
        doReturn(Optional.of(entityWithId)).when(clientDao).findById(id);
        doReturn(dtoWithId).when(clientMapper).toDto(entityWithId);

        var actualResult = clientService.readById(id);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(id);
    }

    @Test
    void readByIdShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        Assertions.assertThrows(NullPointerException.class, () -> clientService.readById(id));
    }

    @Test
    void readByIdShouldThrowExceptionIfEntityNotFound() {
        Long id = 1000L;
        doReturn(Optional.empty()).when(clientDao).findById(id);
        Assertions.assertThrows(EntityNotFoundException.class, () -> clientService.readById(id));
    }

    @Test
    void update() {
        Long id = 1L;
        var entity = getEntity();
        var dto = getDto();
        doReturn(entity).when(clientMapper).toEntity(dto);
        doReturn(Optional.of(entity)).when(clientDao).findById(id);
        var taskDto1 = new TaskDto();
        taskDto1.setId(1L);
        var taskDto2 = new TaskDto();
        taskDto2.setId(2L);
        var taskDto3 = new TaskDto();
        taskDto2.setId(3L);
        var prevTaskList = new ArrayList<>(List.of(taskDto1, taskDto2));
        doReturn(prevTaskList).when(taskMapper).toDtoList(any());
        var newTaskList = new ArrayList<>(List.of(taskDto2, taskDto3));
        doReturn(newTaskList).when(taskService).readAllByClientId(id);

        clientService.update(id, dto);

        verify(clientDao, times(1)).update(captor.capture());
        var value = captor.getValue();
        assertThat(value).isNotNull();
        assertThat(value.getId()).isEqualTo(id);

        verify(taskService, times(2)).update(any(), any(TaskDto.class));
        verify(taskService, times(1)).deleteById(any());
    }

    @Test
    void updateShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        ClientDto any = new ClientDto();
        Assertions.assertThrows(NullPointerException.class, () -> clientService.update(id, any));
    }

    @Test
    void updateShouldThrowExceptionIfDtoIsNull() {
        Long id = 1L;
        ClientDto any = null;
        Assertions.assertThrows(NullPointerException.class, () -> clientService.update(id, any));
    }

    @Test
    void updateShouldThrowExceptionIfEntityNotFound() {
        Long id = 1000L;
        ClientDto any = new ClientDto();
        doReturn(Optional.empty()).when(clientDao).findById(id);
        Assertions.assertThrows(EntityNotFoundException.class, () -> clientService.update(id, any));
    }

    @Test
    void deleteById() {
        Long id = 1L;
        var entity = getEntity();
        doReturn(Optional.of(entity)).when(clientDao).findById(id);

        clientService.deleteById(id);

        verify(clientDao, times(1)).delete(id);
    }

    @Test
    void deleteShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        Assertions.assertThrows(NullPointerException.class, () -> clientService.deleteById(id));
    }

    @Test
    void deleteShouldThrowExceptionIfEntityNotFound() {
        Long id = 1000L;
        doReturn(Optional.empty()).when(clientDao).findById(id);
        Assertions.assertThrows(EntityNotFoundException.class, () -> clientService.deleteById(id));
    }

    @Test
    void existsById() {
        Long id = 1L;
        var entity = getEntity();
        doReturn(Optional.of(entity)).when(clientDao).findById(id);

        var actualResult = clientService.existsById(id);
        Assertions.assertTrue(actualResult);
    }

    @Test
    void existsByIdShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        Assertions.assertThrows(NullPointerException.class, () -> clientService.existsById(id));
    }

    @Test
    void existsByName() {
        String name = "Any";
        var entity = getEntity();
        doReturn(Optional.of(entity)).when(clientDao).findByName(name);

        var actualResult = clientService.existsByName(name);
        Assertions.assertTrue(actualResult);
    }

    @Test
    void existsByNameShouldThrowExceptionIfNameIsNull() {
        String name = null;
        Assertions.assertThrows(NullPointerException.class, () -> clientService.existsByName(name));
    }

    private ClientDto getDto() {
        return new ClientDto(
                1L, "AAA", "BBB", List.of(
                new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto()));
    }

    private ClientDto getDtoWithId() {
        return new ClientDto(
                1L, "AAA", "BBB", List.of(
                new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto()));
    }

    private Client getEntity() {
        return new Client(
                "AAA", "BBB", List.of(
                new Task(), new Task(), new Task(), new Task(), new Task()));
    }

    private Client getEntityWithId() {
        return new Client(
                1L, "AAA", "BBB", List.of(
                new Task(), new Task(), new Task(), new Task(), new Task()));
    }

    private List<ClientDto> getDtoList() {
        return List.of(getDtoWithId(), getDtoWithId(), getDtoWithId(), getDtoWithId(), getDtoWithId());
    }

    private List<Client> getEntityList() {
        return List.of(getEntityWithId(), getEntityWithId(), getEntityWithId(), getEntityWithId(), getEntityWithId());
    }
}