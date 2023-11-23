package com.mogilan.service.impl;

import com.mogilan.exception.EntityNotFoundException;
import com.mogilan.repository.LawFirmDao;
import com.mogilan.service.LawFirmService;
import com.mogilan.service.LawyerService;
import com.mogilan.servlet.dto.ClientDto;
import com.mogilan.servlet.dto.ContactDetailsDto;
import com.mogilan.servlet.dto.LawFirmDto;
import com.mogilan.servlet.mapper.*;
import com.mogilan.servlet.mapper.impl.ContactDetailsMapperImpl;
import com.mogilan.servlet.mapper.impl.LawFirmMapperImpl;
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
class LawFirmServiceImplTest {

    @Mock
    LawFirmDao lawFirmDao;
    ContactDetailsMapper contactDetailsMapper = new ContactDetailsMapperImpl();
    SimpleTaskMapper simpleTaskMapper = new SimpleTaskMapperImpl();
    SimpleLawyerMapper simpleLawyerMapper = new SimpleLawyerMapperImpl();
    @Mock
    LawyerService lawyerService;
    LawyerMapper lawyerMapper = new LawyerMapperImpl(contactDetailsMapper, simpleTaskMapper, simpleLawyerMapper);
    LawFirmMapper lawFirmMapper = new LawFirmMapperImpl(lawyerMapper,simpleLawyerMapper);
    LawFirmService lawFirmService;

    @BeforeEach
    void beforeEach(){
        lawFirmService = new LawFirmServiceImpl(lawFirmDao, lawFirmMapper, lawyerService, simpleLawyerMapper, lawyerMapper);
    }

    @Test
    void create() {
    }

    @Test
    void createShouldThrowExceptionIfDtoIsNull() {
        LawFirmDto lawFirmDto = null;
        Assertions.assertThrows(NullPointerException.class, () -> lawFirmService.create(lawFirmDto));
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
    }

    @Test
    void existsByIdShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        Assertions.assertThrows(NullPointerException.class, () -> lawFirmService.existsById(id));
    }
}