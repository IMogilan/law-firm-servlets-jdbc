package com.mogilan.service.impl;

import com.mogilan.exception.EntityNotFoundException;
import com.mogilan.model.*;
import com.mogilan.repository.LawFirmDao;
import com.mogilan.service.LawFirmService;
import com.mogilan.service.LawyerService;
import com.mogilan.servlet.dto.*;
import com.mogilan.servlet.mapper.*;
import com.mogilan.servlet.mapper.impl.ContactDetailsMapperImpl;
import com.mogilan.servlet.mapper.impl.LawFirmMapperImpl;
import com.mogilan.servlet.mapper.impl.LawyerMapperImpl;
import com.mogilan.servlet.mapper.impl.SimpleLawyerMapperImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LawFirmServiceImplTest {

    @Mock
    LawFirmDao lawFirmDao;
    @Mock
    SimpleLawyerMapper simpleLawyerMapper;
    @Mock
    LawyerService lawyerService;
    @Mock
    LawyerMapper lawyerMapper;
    @Mock
    LawFirmMapper lawFirmMapper;
    @Captor
    ArgumentCaptor<LawFirm> captor;
    LawFirmService lawFirmService;

    @BeforeEach
    void beforeEach() {
        lawFirmService = new LawFirmServiceImpl(lawFirmDao, lawFirmMapper, lawyerService, simpleLawyerMapper, lawyerMapper);
    }

    @Test
    void createSuccess() {
        var dto = getDto();
        var entity = getEntity();
        doReturn(entity).when(lawFirmMapper).toEntity(dto);

        var entityWithId = getEntityWithId();
        doReturn(entityWithId).when(lawFirmDao).save(entity);

        var dtoWithId = getDtoWithId();
        doReturn(dtoWithId).when(lawFirmMapper).toDto(entityWithId);

        var lawyerDtoList = List.of(new LawyerDto(), new LawyerDto());
        doReturn(lawyerDtoList).when(simpleLawyerMapper).toLawyerDtoList(dto.getLawyers());

        doReturn(lawyerDtoList).when(lawyerService).readAllByLawFirmId(dtoWithId.getId());
        doReturn(dto.getLawyers()).when(simpleLawyerMapper).toSimpleLawyerDtoList(lawyerDtoList);

        var actualResult = lawFirmService.create(dto);

        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(dtoWithId.getId());
        assertThat(actualResult.getLawyers()).isNotNull();
        assertThat(actualResult.getLawyers()).isNotEmpty();
        assertThat(actualResult.getLawyers().size()).isEqualTo(lawyerDtoList.size());
        verify(lawyerService, times(1)).readAllByLawFirmId(dtoWithId.getId());
        verify(lawyerService, times(2)).create(any(LawyerDto.class));
    }

    @Test
    void createShouldThrowExceptionIfDtoIsNull() {
        LawFirmDto lawFirmDto = null;
        Assertions.assertThrows(NullPointerException.class, () -> lawFirmService.create(lawFirmDto));
    }

    @Test
    void readAllSuccess() {
        var entityList = getEntityList();
        doReturn(entityList).when(lawFirmDao).findAll();
        var dtoList = getDtoList();
        doReturn(dtoList).when(lawFirmMapper).toDtoList(entityList);

        var actualResult = lawFirmService.readAll();
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).hasSize(entityList.size());
    }

    @Test
    void readById() {
        Long id = 1L;
        var entityWithId = getEntityWithId();
        var dtoWithId = getDtoWithId();
        doReturn(Optional.of(entityWithId)).when(lawFirmDao).findById(id);
        doReturn(dtoWithId).when(lawFirmMapper).toDto(entityWithId);

        var actualResult = lawFirmService.readById(id);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(id);
    }

    @Test
    void readByIdShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        Assertions.assertThrows(NullPointerException.class, () -> lawFirmService.readById(id));
    }

    @Test
    void readByIdShouldThrowExceptionIfEntityNotFound() {
        Long id = 1000L;
        doReturn(Optional.empty()).when(lawFirmDao).findById(id);
        Assertions.assertThrows(EntityNotFoundException.class, () -> lawFirmService.readById(id));
    }

    @Test
    void update() {
        Long id = 1L;
        var entity = getEntity();
        var dto = getDto();
        doReturn(entity).when(lawFirmMapper).toEntity(dto);
        doReturn(Optional.of(entity)).when(lawFirmDao).findById(id);

        var lawyerDto1 = new LawyerDto();
        lawyerDto1.setId(1L);
        var lawyerDto2 = new LawyerDto();
        lawyerDto2.setId(2L);
        var lawyerDto3 = new LawyerDto();
        lawyerDto2.setId(3L);
        var prevList = new ArrayList<>(List.of(lawyerDto1, lawyerDto2));
        doReturn(prevList).when(lawyerMapper).toDtoList(any());
        var newTaskList = new ArrayList<>(List.of(lawyerDto2, lawyerDto3));
        doReturn(newTaskList).when(lawyerService).readAllByLawFirmId(id);

        lawFirmService.update(id, dto);

        verify(lawFirmDao, times(1)).update(captor.capture());
        var value = captor.getValue();
        assertThat(value).isNotNull();
        assertThat(value.getId()).isEqualTo(id);

        verify(lawyerService, times(2)).update(any(), any(LawyerDto.class));
        verify(lawyerService, times(1)).deleteById(any());
    }

    @Test
    void updateShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        LawFirmDto any = new LawFirmDto();
        Assertions.assertThrows(NullPointerException.class, () -> lawFirmService.update(id, any));
    }

    @Test
    void updateShouldThrowExceptionIfDtoIsNull() {
        Long id = 1L;
        LawFirmDto any = null;
        Assertions.assertThrows(NullPointerException.class, () -> lawFirmService.update(id, any));
    }

    @Test
    void updateShouldThrowExceptionIfEntityNotFound() {
        Long id = 1000L;
        LawFirmDto any = new LawFirmDto();
        doReturn(Optional.empty()).when(lawFirmDao).findById(id);
        Assertions.assertThrows(EntityNotFoundException.class, () -> lawFirmService.update(id, any));
    }

    @Test
    void deleteById() {
        Long id = 1L;
        var entity = getEntity();
        doReturn(Optional.of(entity)).when(lawFirmDao).findById(id);

        lawFirmService.deleteById(id);

        verify(lawFirmDao, times(1)).delete(id);
    }

    @Test
    void deleteShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        Assertions.assertThrows(NullPointerException.class, () -> lawFirmService.deleteById(id));
    }

    @Test
    void deleteShouldThrowExceptionIfEntityNotFound() {
        Long id = 1000L;
        doReturn(Optional.empty()).when(lawFirmDao).findById(id);
        Assertions.assertThrows(EntityNotFoundException.class, () -> lawFirmService.deleteById(id));
    }

    @Test
    void existsById() {
        Long id = 1L;
        var entity = getEntity();
        doReturn(Optional.of(entity)).when(lawFirmDao).findById(id);

        var actualResult = lawFirmService.existsById(id);
        Assertions.assertTrue(actualResult);
    }

    @Test
    void existsByIdShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        Assertions.assertThrows(NullPointerException.class, () -> lawFirmService.existsById(id));
    }

    private LawFirmDto getDto() {
        return new LawFirmDto("AAA", LocalDate.of(2000, 1, 1),
                List.of(
                        new SimpleLawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0,
                                new ContactDetailsDto(1L, "1", "777", "777", "777", "test@mail.com"),
                                List.of(new TaskDto(), new TaskDto(), new TaskDto())),
                        new SimpleLawyerDto(2L, "2", "1", JobTitle.PARTNER, 200.0,
                                null,
                                null)
                )
        );
    }

    private LawFirmDto getDtoWithId() {
        return new LawFirmDto(1L, "AAA", LocalDate.of(2000, 1, 1),
                List.of(
                        new SimpleLawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0,
                                new ContactDetailsDto(1L, "1", "777", "777", "777", "test@mail.com"),
                                List.of(new TaskDto(), new TaskDto(), new TaskDto())),
                        new SimpleLawyerDto(2L, "2", "1", JobTitle.PARTNER, 200.0,
                                null,
                                null)
                )
        );
    }

    private LawFirm getEntity() {
        return new LawFirm("AAA", LocalDate.of(2000, 1, 1),
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

    private LawFirm getEntityWithId() {
        return new LawFirm(1L, "AAA", LocalDate.of(2000, 1, 1),
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

    private List<LawFirmDto> getDtoList() {
        return List.of(getDtoWithId(), getDtoWithId(), getDtoWithId(), getDtoWithId(), getDtoWithId());
    }

    private List<LawFirm> getEntityList() {
        return List.of(getEntityWithId(), getEntityWithId(), getEntityWithId(), getEntityWithId(), getEntityWithId());
    }
}