package com.mogilan.service.impl;

import com.mogilan.exception.EntityNotFoundException;
import com.mogilan.repository.ContactDetailsDao;
import com.mogilan.service.ContactDetailsService;
import com.mogilan.servlet.dto.ContactDetailsDto;
import com.mogilan.servlet.mapper.ContactDetailsMapper;
import com.mogilan.servlet.mapper.impl.ContactDetailsMapperImpl;
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
class ContactDetailsServiceImplTest {

    @Mock
    ContactDetailsDao contactDetailsDao;
    ContactDetailsMapper contactDetailsMapper = new ContactDetailsMapperImpl();
    ContactDetailsService contactDetailsService;


    @BeforeEach
    void beforeEach(){
        contactDetailsService = new ContactDetailsServiceImpl(contactDetailsDao, contactDetailsMapper);;
    }

    @Test
    void createSuccess() {
    }

    @Test
    void createShouldThrowExceptionIfDtoIsNull() {
        ContactDetailsDto contactDetailsDto = null;
        Assertions.assertThrows(NullPointerException.class, () -> contactDetailsService.create(contactDetailsDto));
    }

    @Test
    void readAll() {
    }

    @Test
    void readById() {
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
    void update() {
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
    void deleteById() {
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
}