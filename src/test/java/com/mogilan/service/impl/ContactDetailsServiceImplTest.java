package com.mogilan.service.impl;

import com.mogilan.exception.EntityNotFoundException;
import com.mogilan.model.ContactDetails;
import com.mogilan.repository.ContactDetailsDao;
import com.mogilan.service.ContactDetailsService;
import com.mogilan.servlet.dto.ContactDetailsDto;
import com.mogilan.servlet.mapper.ContactDetailsMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactDetailsServiceImplTest {

    @Mock
    ContactDetailsDao contactDetailsDao;
    @Mock
    ContactDetailsMapper contactDetailsMapper;
    ContactDetailsService contactDetailsService;

    @Captor
    ArgumentCaptor<ContactDetails> captor;


    @BeforeEach
    void beforeEach(){
        contactDetailsService = new ContactDetailsServiceImpl(contactDetailsDao, contactDetailsMapper);;
    }

    @Test
    void createSuccess() {
        var dto = getDto();
        var entity = getEntity();
        doReturn(entity).when(contactDetailsMapper).toEntity(dto);

        var entityWithId = getEntityWithId();
        doReturn(entityWithId).when(contactDetailsDao).save(entity);

        var dtoWithId = getDtoWithId();
        doReturn(dtoWithId).when(contactDetailsMapper).toDto(entityWithId);

        var actualResult = contactDetailsService.create(dto);

        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(dtoWithId.getId());
    }

    @Test
    void createShouldThrowExceptionIfDtoIsNull() {
        ContactDetailsDto contactDetailsDto = null;
        Assertions.assertThrows(NullPointerException.class, () -> contactDetailsService.create(contactDetailsDto));
    }

    @Test
    void readAllSuccess() {
        var entityList = getEntityList();
        doReturn(entityList).when(contactDetailsDao).findAll();
        var dtoList = getDtoList();
        doReturn(dtoList).when(contactDetailsMapper).toDtoList(entityList);

        var actualResult = contactDetailsService.readAll();
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isNotEmpty();
        assertThat(actualResult).hasSize(entityList.size());
    }

    @Test
    void readById() {
        Long id = 1L;
        var entityWithId = getEntityWithId();
        var dtoWithId = getDtoWithId();
        doReturn(Optional.of(entityWithId)).when(contactDetailsDao).findById(id);
        doReturn(dtoWithId).when(contactDetailsMapper).toDto(entityWithId);

        var actualResult = contactDetailsService.readById(id);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(id);
    }

    @Test
    void readByIdShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        Assertions.assertThrows(NullPointerException.class, () -> contactDetailsService.readById(id));
    }

    @Test
    void readByIdShouldThrowExceptionIfEntityNotFound() {
        Long id = 1000L;
        doReturn(Optional.empty()).when(contactDetailsDao).findById(id);
        Assertions.assertThrows(EntityNotFoundException.class, () -> contactDetailsService.readById(id));
    }

    @Test
    void updateSuccess() {
        Long id = 1L;
        var entity = getEntity();
        var dto = getDto();
        doReturn(entity).when(contactDetailsMapper).toEntity(dto);
        doReturn(Optional.of(entity)).when(contactDetailsDao).findById(id);

        contactDetailsService.update(id, dto);

        verify(contactDetailsDao, times(1)).update(captor.capture());
        var value = captor.getValue();
        assertThat(value).isNotNull();
        assertThat(value.getId()).isEqualTo(id);
    }

    @Test
    void updateShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        ContactDetailsDto any = new ContactDetailsDto();
        Assertions.assertThrows(NullPointerException.class, () -> contactDetailsService.update(id, any));
    }

    @Test
    void updateShouldThrowExceptionIfDtoIsNull() {
        Long id = 1L;
        ContactDetailsDto any = null;
        Assertions.assertThrows(NullPointerException.class, () -> contactDetailsService.update(id, any));
    }

    @Test
    void updateShouldThrowExceptionIfEntityNotFound() {
        Long id = 1000L;
        ContactDetailsDto any = new ContactDetailsDto();
        doReturn(Optional.empty()).when(contactDetailsDao).findById(id);
        Assertions.assertThrows(EntityNotFoundException.class, () -> contactDetailsService.update(id, any));
    }

    @Test
    void deleteByIdSuccess() {
        Long id = 1L;
        var entity = getEntity();
        doReturn(Optional.of(entity)).when(contactDetailsDao).findById(id);

        contactDetailsService.deleteById(id);

        verify(contactDetailsDao, times(1)).delete(id);
    }

    @Test
    void deleteShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        Assertions.assertThrows(NullPointerException.class, () -> contactDetailsService.deleteById(id));
    }

    @Test
    void deleteShouldThrowExceptionIfEntityNotFound() {
        Long id = 1000L;
        doReturn(Optional.empty()).when(contactDetailsDao).findById(id);
        Assertions.assertThrows(EntityNotFoundException.class, () -> contactDetailsService.deleteById(id));
    }

    private ContactDetailsDto getDto() {
        return new ContactDetailsDto("Address", "777", "888", "999", "test@email.com");
    }

    private ContactDetailsDto getDtoWithId() {
        return new ContactDetailsDto(1L, "Address", "777", "888", "999", "test@email.com");
    }

    private ContactDetails getEntity() {
        return new ContactDetails("Address", "777", "888", "999", "test@email.com");
    }

    private ContactDetails getEntityWithId() {
        return new ContactDetails(1L, "Address", "777", "888", "999", "test@email.com");
    }

    private List<ContactDetailsDto> getDtoList() {
        return List.of(getDtoWithId(),getDtoWithId(),getDtoWithId(),getDtoWithId(),getDtoWithId());
    }

    private List<ContactDetails> getEntityList() {
        return List.of(getEntityWithId(),getEntityWithId(),getEntityWithId(),getEntityWithId(),getEntityWithId());
    }
}