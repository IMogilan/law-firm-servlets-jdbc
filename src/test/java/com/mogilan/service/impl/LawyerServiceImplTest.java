package com.mogilan.service.impl;

import com.mogilan.exception.EntityNotFoundException;
import com.mogilan.repository.LawyerDao;
import com.mogilan.service.ContactDetailsService;
import com.mogilan.service.LawyerService;
import com.mogilan.servlet.dto.ClientDto;
import com.mogilan.servlet.dto.ContactDetailsDto;
import com.mogilan.servlet.dto.LawyerDto;
import com.mogilan.servlet.mapper.*;
import com.mogilan.servlet.mapper.impl.ContactDetailsMapperImpl;
import com.mogilan.servlet.mapper.impl.LawyerMapperImpl;
import com.mogilan.servlet.mapper.impl.SimpleLawyerMapperImpl;
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
class LawyerServiceImplTest {

    @Mock
    LawyerDao lawyerDao;
    @Mock
    ContactDetailsService contactDetailsService;
    ContactDetailsMapper contactDetailsMapper = new ContactDetailsMapperImpl();
    SimpleTaskMapper simpleTaskMapper = new SimpleTaskMapperImpl();
    SimpleLawyerMapper simpleLawyerMapper = new SimpleLawyerMapperImpl();
    LawyerMapper lawyerMapper = new LawyerMapperImpl(contactDetailsMapper, simpleTaskMapper, simpleLawyerMapper);
    LawyerService lawyerService;

    @BeforeEach
    void beforeEach(){
        lawyerService = new LawyerServiceImpl(lawyerDao, contactDetailsService, lawyerMapper);;
    }

    @Test
    void create() {
    }

    @Test
    void createShouldThrowExceptionIfDtoIsNull() {
        LawyerDto lawyerDto = null;
        Assertions.assertThrows(NullPointerException.class, () -> lawyerService.create(lawyerDto));
    }

    @Test
    void readAll() {
    }

    @Test
    void readAllByLawFirmId() {
    }

    @Test
    void readAllByLawFirmIdShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        Assertions.assertThrows(NullPointerException.class, () -> lawyerService.readAllByLawFirmId(id));
    }

    @Test
    void readAllByTaskId() {
    }

    @Test
    void readAllByTaskIdShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        Assertions.assertThrows(NullPointerException.class, () -> lawyerService.readAllByTaskId(id));
    }

    @Test
    void readById() {
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
}