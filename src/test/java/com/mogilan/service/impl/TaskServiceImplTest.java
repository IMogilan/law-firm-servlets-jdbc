package com.mogilan.service.impl;

import com.mogilan.exception.EntityNotFoundException;
import com.mogilan.repository.TaskDao;
import com.mogilan.service.LawyerService;
import com.mogilan.service.TaskService;
import com.mogilan.servlet.dto.ClientDto;
import com.mogilan.servlet.dto.ContactDetailsDto;
import com.mogilan.servlet.dto.TaskDto;
import com.mogilan.servlet.mapper.*;
import com.mogilan.servlet.mapper.impl.ContactDetailsMapperImpl;
import com.mogilan.servlet.mapper.impl.SimpleLawyerMapperImpl;
import com.mogilan.servlet.mapper.impl.TaskMapperImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {
    @Mock
    TaskDao taskDao;
    ContactDetailsMapper contactDetailsMapper = new ContactDetailsMapperImpl();
    SimpleTaskMapper simpleTaskMapper = new SimpleTaskMapperImpl();
    SimpleLawyerMapper simpleLawyerMapper = new SimpleLawyerMapperImpl();
    TaskMapper taskMapper = new TaskMapperImpl(contactDetailsMapper, simpleTaskMapper, simpleLawyerMapper);
    @Mock
    LawyerService lawyerService;
    TaskService taskService;

    @BeforeEach
    void beforeEach(){
        taskService = new TaskServiceImpl(taskDao, taskMapper, lawyerService);
    }

    @Test
    void create() {
    }

    @Test
    void createShouldThrowExceptionIfDtoIsNull() {
        TaskDto taskDto = null;
        Assertions.assertThrows(NullPointerException.class, () -> taskService.create(taskDto));
    }

    @Test
    void readAll() {
    }

    @Test
    void readAllByClientId() {
    }

    @Test
    void readAllByClientIdShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        Assertions.assertThrows(NullPointerException.class, () -> taskService.readAllByClientId(id));
    }

    @Test
    void readAllByLawyerId() {
    }

    @Test
    void readAllByLawyerIdShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        Assertions.assertThrows(NullPointerException.class, () -> taskService.readAllByLawyerId(id));
    }

    @Test
    void isLawyerResponsibleForTask() {
    }

    @Test
    void readById() {
    }

    @Test
    void readByIdShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        Assertions.assertThrows(NullPointerException.class, () -> taskService.readById(id));
    }

    @Test
    void readByIdShouldThrowExceptionIfEntityNotFound() {
        Long id = 1000L;
        doReturn(Optional.empty()).when(taskDao).findById(id);
        Assertions.assertThrows(EntityNotFoundException.class, () -> taskService.readById(id));
    }

    @Test
    void update() {
    }

    @Test
    void updateShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        TaskDto any = new TaskDto();
        Assertions.assertThrows(NullPointerException.class, () -> taskService.update(id, any));
    }

    @Test
    void updateShouldThrowExceptionIfDtoIsNull() {
        Long id = 1L;
        TaskDto any = null;
        Assertions.assertThrows(NullPointerException.class, () -> taskService.update(id, any));
    }

    @Test
    void updateShouldThrowExceptionIfEntityNotFound() {
        Long id = 1000L;
        TaskDto any = new TaskDto();
        doReturn(Optional.empty()).when(taskDao).findById(id);
        Assertions.assertThrows(EntityNotFoundException.class, () -> taskService.update(id, any));
    }

    @Test
    void deleteById() {
    }

    @Test
    void deleteShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        Assertions.assertThrows(NullPointerException.class, () -> taskService.deleteById(id));
    }

    @Test
    void deleteShouldThrowExceptionIfEntityNotFound() {
        Long id = 1000L;
        doReturn(Optional.empty()).when(taskDao).findById(id);
        Assertions.assertThrows(EntityNotFoundException.class, () -> taskService.deleteById(id));
    }
}