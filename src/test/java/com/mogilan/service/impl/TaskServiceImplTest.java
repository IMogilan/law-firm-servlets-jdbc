package com.mogilan.service.impl;

import com.mogilan.exception.EntityNotFoundException;
import com.mogilan.model.*;
import com.mogilan.repository.TaskDao;
import com.mogilan.service.LawyerService;
import com.mogilan.service.TaskService;
import com.mogilan.servlet.dto.*;
import com.mogilan.servlet.mapper.*;
import com.mogilan.servlet.mapper.impl.ContactDetailsMapperImpl;
import com.mogilan.servlet.mapper.impl.SimpleLawyerMapperImpl;
import com.mogilan.servlet.mapper.impl.TaskMapperImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {
    @Mock
    TaskDao taskDao;
    @Mock
    TaskMapper taskMapper;
    @Mock
    LawyerService lawyerService;
    @Captor
    ArgumentCaptor<Task> taskArgumentCaptor;
    TaskService taskService;

    @BeforeEach
    void beforeEach(){
        taskService = new TaskServiceImpl(taskDao, taskMapper, lawyerService);
    }

    @Test
    void create() {
        var dto = getDto();
        var entity = getEntity();
        doReturn(entity).when(taskMapper).toEntity(dto);

        var entityWithId = getEntityWithId();
        doReturn(entityWithId).when(taskDao).save(entity);

        var dtoWithId = getDtoWithId();
        doReturn(dtoWithId).when(taskMapper).toDto(entityWithId);

        var actualResult = taskService.create(dto);

        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(dtoWithId.getId());
    }

    @Test
    void createShouldThrowExceptionIfDtoIsNull() {
        TaskDto taskDto = null;
        Assertions.assertThrows(NullPointerException.class, () -> taskService.create(taskDto));
    }

    @Test
    void readAll() {
        var entityList = getEntityList();
        doReturn(entityList).when(taskDao).findAll();
        var dtoList = getDtoList();
        doReturn(dtoList).when(taskMapper).toDtoList(entityList);

        var actualResult = taskService.readAll();
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).hasSize(entityList.size());
    }

    @Test
    void readAllByClientId() {
        Long id = 1L;
        var entityList = getEntityList();
        doReturn(entityList).when(taskDao).findAllByClientId(id);
        var dtoList = getDtoList();
        doReturn(dtoList).when(taskMapper).toDtoList(entityList);

        var actualResult = taskService.readAllByClientId(id);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).hasSize(entityList.size());
    }

    @Test
    void readAllByClientIdShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        Assertions.assertThrows(NullPointerException.class, () -> taskService.readAllByClientId(id));
    }

    @Test
    void readAllByLawyerId() {
        Long id = 1L;
        var entityList = getEntityList();
        doReturn(entityList).when(taskDao).findAllByLawyerId(id);
        var dtoList = getDtoList();
        doReturn(dtoList).when(taskMapper).toDtoList(entityList);

        var actualResult = taskService.readAllByLawyerId(id);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).hasSize(entityList.size());
    }

    @Test
    void readAllByLawyerIdShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        Assertions.assertThrows(NullPointerException.class, () -> taskService.readAllByLawyerId(id));
    }

    @Test
    void isLawyerResponsibleForTaskReturnsTrue() {
        Long taskId = 1L;
        Long lawyerId = 1L;
        var lawyerDto1 = new LawyerDto();
        lawyerDto1.setId(lawyerId);
        var lawyerDto2 = new LawyerDto();
        lawyerDto2.setId(2L);
        doReturn(List.of(lawyerDto1, lawyerDto2)).when(lawyerService).readAllByTaskId(taskId);

        var actualResult = taskService.isLawyerResponsibleForTask(taskId, lawyerId);
        assertTrue(actualResult);
    }

    @Test
    void isLawyerResponsibleForTaskReturnsFalse() {
        Long taskId = 1L;
        Long lawyerId = 1L;
        doReturn(Collections.emptyList()).when(lawyerService).readAllByTaskId(taskId);

        var actualResult = taskService.isLawyerResponsibleForTask(taskId, lawyerId);
        assertFalse(actualResult);
    }

    @Test
    void isLawyerResponsibleForTaskShouldThrowExceptionIfTaskIdIsNull() {
        Long taskId = null;
        Long lawyerId = 1L;
        Assertions.assertThrows(NullPointerException.class, () -> taskService.isLawyerResponsibleForTask(taskId, lawyerId));
    }
    @Test
    void isLawyerResponsibleForTaskShouldThrowExceptionIfLawyerIdIsNull() {
        Long taskId = 1L;
        Long lawyerId = null;
        Assertions.assertThrows(NullPointerException.class, () -> taskService.isLawyerResponsibleForTask(taskId, lawyerId));
    }

    @Test
    void readById() {
        Long id = 1L;
        var entityWithId = getEntityWithId();
        var dtoWithId = getDtoWithId();
        doReturn(Optional.of(entityWithId)).when(taskDao).findById(id);
        doReturn(dtoWithId).when(taskMapper).toDto(entityWithId);

        var actualResult = taskService.readById(id);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(id);
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
        Long id = 1L;
        var entity = getEntity();
        var dto = getDto();
        doReturn(Optional.of(entity)).when(taskDao).findById(id);
        doReturn(entity).when(taskMapper).toEntity(dto);

        taskService.update(id, dto);

        verify(taskDao, times(1)).update(taskArgumentCaptor.capture());
        var value = taskArgumentCaptor.getValue();
        assertThat(value).isNotNull();
        assertThat(value.getId()).isEqualTo(id);
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
        Long id = 1L;
        var entity = getEntity();
        doReturn(Optional.of(entity)).when(taskDao).findById(id);

        taskService.deleteById(id);

        verify(taskDao, times(1)).delete(id);
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

    private TaskDto getDto() {
        return new TaskDto("Make draft", "ASP", TaskPriority.HIGH, TaskStatus.ACCEPTED,
                LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 1), null, 5.0,
                new ClientDto(1L, "AAA", "BBB", List.of(new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto())),
                List.of(
                        new LawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0,
                                new LawFirmDto(1L, "AAA", null, null),
                                new ContactDetailsDto(1L, "1", "777", "777", "777", "test@mail.com"),
                                List.of(new TaskDto(), new TaskDto(), new TaskDto())),
                        new LawyerDto(2L, "2", "1", JobTitle.PARTNER, 200.0,
                                new LawFirmDto(1L, "BBB", null, null),
                                null,
                                null)
                )
        );
    }

    private TaskDto getDtoWithId() {
        return new TaskDto(1L,"Make draft", "ASP", TaskPriority.HIGH, TaskStatus.ACCEPTED,
                LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 1), null, 5.0,
                new ClientDto(1L, "AAA", "BBB", List.of(new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto())),
                List.of(
                        new LawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0,
                                new LawFirmDto(1L, "AAA", null, null),
                                new ContactDetailsDto(1L, "1", "777", "777", "777", "test@mail.com"),
                                List.of(new TaskDto(), new TaskDto(), new TaskDto())),
                        new LawyerDto(2L, "2", "1", JobTitle.PARTNER, 200.0,
                                new LawFirmDto(1L, "BBB", null, null),
                                null,
                                null)
                )
        );
    }

    private Task getEntity() {
        return new Task("Make draft", "ASP", TaskPriority.HIGH, TaskStatus.ACCEPTED,
                LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 1), null, 5.0,
                new Client(1L, "AAA", "BBB", List.of(new Task(), new Task(), new Task(), new Task())),
                List.of(
                        new Lawyer(1L, "1", "1", JobTitle.ASSOCIATE, 100.0,
                                new LawFirm(1L, "AAA", null, null),
                                new ContactDetails(1L, "1", "777", "777", "777", "test@mail.com"),
                                List.of(new Task(), new Task(), new Task())),
                        new Lawyer(2L, "2", "1", JobTitle.PARTNER, 200.0,
                                new LawFirm(1L, "BBB", null, null),
                                null,
                                null)
                )
        );
    }

    private Task getEntityWithId() {
        return new Task(1L, "Make draft", "ASP", TaskPriority.HIGH, TaskStatus.ACCEPTED,
                LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 1), null, 5.0,
                new Client(1L, "AAA", "BBB", List.of(new Task(), new Task(), new Task(), new Task())),
                List.of(
                        new Lawyer(1L, "1", "1", JobTitle.ASSOCIATE, 100.0,
                                new LawFirm(1L, "AAA", null, null),
                                new ContactDetails(1L, "1", "777", "777", "777", "test@mail.com"),
                                List.of(new Task(), new Task(), new Task())),
                        new Lawyer(2L, "2", "1", JobTitle.PARTNER, 200.0,
                                new LawFirm(1L, "BBB", null, null),
                                null,
                                null)
                )
        );
    }

    private List<TaskDto> getDtoList() {
        return List.of(getDtoWithId(), getDtoWithId(), getDtoWithId(), getDtoWithId(), getDtoWithId());
    }

    private List<Task> getEntityList() {
        return List.of(getEntityWithId(), getEntityWithId(), getEntityWithId(), getEntityWithId(), getEntityWithId());
    }
}