package com.mogilan.service.impl;

import com.mogilan.exception.EntityNotFoundException;
import com.mogilan.model.ContactDetails;
import com.mogilan.model.LawFirm;
import com.mogilan.model.Lawyer;
import com.mogilan.model.Task;
import com.mogilan.repository.LawyerDao;
import com.mogilan.service.ContactDetailsService;
import com.mogilan.service.LawyerService;
import com.mogilan.servlet.dto.*;
import com.mogilan.servlet.mapper.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LawyerServiceImplTest {

    @Mock
    LawyerDao lawyerDao;
    @Mock
    ContactDetailsService contactDetailsService;
    @Mock
    LawyerMapper lawyerMapper;
    @Captor
    ArgumentCaptor<ContactDetailsDto> contactDetailsDtoArgumentCaptor;
    @Captor
    ArgumentCaptor<Lawyer> lawyerArgumentCaptor;
    @InjectMocks
    LawyerServiceImpl lawyerService;

    @Test
    void createSuccess() {
        var dto = getDto();
        var entity = getEntity();
        doReturn(entity).when(lawyerMapper).toEntity(dto);

        var entityWithId = getEntityWithId();
        doReturn(entityWithId).when(lawyerDao).save(entity);

        var dtoWithId = getDtoWithId();
        doReturn(dtoWithId).when(lawyerMapper).toDto(entityWithId);

        var actualResult = lawyerService.create(dto);

        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(dtoWithId.getId());

        verify(contactDetailsService, times(1)).create(contactDetailsDtoArgumentCaptor.capture());
        var value = contactDetailsDtoArgumentCaptor.getValue();
        assertThat(value).isNotNull();
        assertThat(value.getId()).isEqualTo(actualResult.getId());
    }

    @Test
    void createShouldThrowExceptionIfDtoIsNull() {
        LawyerDto lawyerDto = null;
        Assertions.assertThrows(NullPointerException.class, () -> lawyerService.create(lawyerDto));
    }

    @Test
    void readAll() {
        var entityList = getEntityList();
        doReturn(entityList).when(lawyerDao).findAll();
        var dtoList = getDtoList();
        doReturn(dtoList).when(lawyerMapper).toDtoList(entityList);

        var actualResult = lawyerService.readAll();
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).hasSize(entityList.size());
    }

    @Test
    void readAllByLawFirmId() {
        Long id = 1L;
        var entityList = getEntityList();
        doReturn(entityList).when(lawyerDao).findAllByLawFirmId(id);
        var dtoList = getDtoList();
        doReturn(dtoList).when(lawyerMapper).toDtoList(entityList);

        var actualResult = lawyerService.readAllByLawFirmId(id);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).hasSize(entityList.size());
    }

    @Test
    void readAllByLawFirmIdShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        Assertions.assertThrows(NullPointerException.class, () -> lawyerService.readAllByLawFirmId(id));
    }

    @Test
    void readAllByTaskId() {
        Long id = 1L;
        var entityList = getEntityList();
        doReturn(entityList).when(lawyerDao).findAllByTaskId(id);
        var dtoList = getDtoList();
        doReturn(dtoList).when(lawyerMapper).toDtoList(entityList);

        var actualResult = lawyerService.readAllByTaskId(id);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).hasSize(entityList.size());
    }

    @Test
    void readAllByTaskIdShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        Assertions.assertThrows(NullPointerException.class, () -> lawyerService.readAllByTaskId(id));
    }

    @Test
    void readById() {
        Long id = 1L;
        var entityWithId = getEntityWithId();
        var dtoWithId = getDtoWithId();
        doReturn(Optional.of(entityWithId)).when(lawyerDao).findById(id);
        doReturn(dtoWithId).when(lawyerMapper).toDto(entityWithId);

        var actualResult = lawyerService.readById(id);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(id);
    }

    @Test
    void readByIdShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        Assertions.assertThrows(NullPointerException.class, () -> lawyerService.readById(id));
    }

    @Test
    void readByIdShouldThrowExceptionIfEntityNotFound() {
        Long id = 1000L;
        doReturn(Optional.empty()).when(lawyerDao).findById(id);
        Assertions.assertThrows(EntityNotFoundException.class, () -> lawyerService.readById(id));
    }

    @Test
    void update() {
        Long id = 1L;
        var entity = getEntity();
        var dto = getDto();
        doReturn(Optional.of(entity)).when(lawyerDao).findById(id);
        doReturn(entity).when(lawyerMapper).toEntity(dto);

        lawyerService.update(id, dto);

        verify(lawyerDao, times(1)).update(lawyerArgumentCaptor.capture());
        var value = lawyerArgumentCaptor.getValue();
        assertThat(value).isNotNull();
        assertThat(value.getId()).isEqualTo(id);
    }

    @Test
    void updateWithCreationNewContactDetails() {
        Long id = 1L;
        var entity = getEntity();
        entity.setContacts(null);
        var dto = getDto();
        dto.setContacts(null);
        doReturn(Optional.of(entity)).when(lawyerDao).findById(id);
        doReturn(entity).when(lawyerMapper).toEntity(dto);

        lawyerService.update(id, dto);

        verify(lawyerDao, times(1)).update(lawyerArgumentCaptor.capture());
        var value = lawyerArgumentCaptor.getValue();
        assertThat(value).isNotNull();
        assertThat(value.getId()).isEqualTo(id);
    }

    @Test
    void updateShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        LawyerDto any = new LawyerDto();
        Assertions.assertThrows(NullPointerException.class, () -> lawyerService.update(id, any));
    }

    @Test
    void updateShouldThrowExceptionIfDtoIsNull() {
        Long id = 1L;
        LawyerDto any = null;
        Assertions.assertThrows(NullPointerException.class, () -> lawyerService.update(id, any));
    }

    @Test
    void updateShouldThrowExceptionIfEntityNotFound() {
        Long id = 1000L;
        LawyerDto any = new LawyerDto();
        doReturn(Optional.empty()).when(lawyerDao).findById(id);
        Assertions.assertThrows(EntityNotFoundException.class, () -> lawyerService.update(id, any));
    }

    @Test
    void deleteById() {
        Long id = 1L;
        var entity = getEntity();
        doReturn(Optional.of(entity)).when(lawyerDao).findById(id);

        lawyerService.deleteById(id);

        verify(lawyerDao, times(1)).delete(id);
    }

    @Test
    void deleteShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        Assertions.assertThrows(NullPointerException.class, () -> lawyerService.deleteById(id));
    }

    @Test
    void deleteShouldThrowExceptionIfEntityNotFound() {
        Long id = 1000L;
        doReturn(Optional.empty()).when(lawyerDao).findById(id);
        Assertions.assertThrows(EntityNotFoundException.class, () -> lawyerService.deleteById(id));
    }


    private LawyerDto getDto() {
        return new LawyerDto("1", "1", JobTitle.ASSOCIATE, 100.0,
                new LawFirmDto(1L, "AAA", null, null),
                new ContactDetailsDto(1L, "1", "777", "777", "777", "test@mail.com"),
                List.of(new TaskDto(), new TaskDto(), new TaskDto()));
    }

    private LawyerDto getDtoWithId() {
        return new LawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0,
                new LawFirmDto(1L, "AAA", null, null),
                new ContactDetailsDto(1L, "1", "777", "777", "777", "test@mail.com"),
                List.of(new TaskDto(), new TaskDto(), new TaskDto()));
    }

    private Lawyer getEntity() {
        return new Lawyer("1", "1", JobTitle.ASSOCIATE, 100.0,
                new LawFirm(1L, "AAA", null, null),
                new ContactDetails(1L, "1", "777", "777", "777", "test@mail.com"),
                List.of(new Task(), new Task(), new Task()));
    }

    private Lawyer getEntityWithId() {
        return new Lawyer(1L, "1", "1", JobTitle.ASSOCIATE, 100.0,
                new LawFirm(1L, "AAA", null, null),
                new ContactDetails(1L, "1", "777", "777", "777", "test@mail.com"),
                List.of(new Task(), new Task(), new Task()));
    }

    private List<LawyerDto> getDtoList() {
        return List.of(getDtoWithId(), getDtoWithId(), getDtoWithId(), getDtoWithId(), getDtoWithId());
    }

    private List<Lawyer> getEntityList() {
        return List.of(getEntityWithId(), getEntityWithId(), getEntityWithId(), getEntityWithId(), getEntityWithId());
    }
}