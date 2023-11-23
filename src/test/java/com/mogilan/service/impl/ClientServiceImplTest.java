package com.mogilan.service.impl;

import com.mogilan.exception.EntityNotFoundException;
import com.mogilan.repository.ClientDao;
import com.mogilan.service.ClientService;
import com.mogilan.service.TaskService;
import com.mogilan.servlet.dto.ClientDto;
import com.mogilan.servlet.mapper.*;
import com.mogilan.servlet.mapper.impl.ClientMapperImpl;
import com.mogilan.servlet.mapper.impl.ContactDetailsMapperImpl;
import com.mogilan.servlet.mapper.impl.SimpleLawyerMapperImpl;
import com.mogilan.servlet.mapper.impl.TaskMapperImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    ClientDao clientDao;
    @Mock
    TaskService taskService;
    ContactDetailsMapper contactDetailsMapper = new ContactDetailsMapperImpl();
    SimpleTaskMapper simpleTaskMapper = new SimpleTaskMapperImpl();
    SimpleLawyerMapper simpleLawyerMapper = new SimpleLawyerMapperImpl();
    TaskMapper taskMapper = new TaskMapperImpl(contactDetailsMapper, simpleTaskMapper, simpleLawyerMapper);
    ClientMapper clientMapper = new ClientMapperImpl(contactDetailsMapper, simpleTaskMapper, simpleLawyerMapper);
    ClientService clientService;


    @BeforeEach
    void beforeEach(){
        clientService = new ClientServiceImpl(clientDao, clientMapper, taskService,simpleTaskMapper,taskMapper);
    }

    @Test
    void create() {
    }

    @Test
    void createShouldThrowExceptionIfDtoIsNull() {
        ClientDto clientDto = null;
        Assertions.assertThrows(NullPointerException.class, () -> clientService.create(clientDto));
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
    }

    @Test
    void existsByIdShouldThrowExceptionIfIdIsNull() {
        Long id = null;
        Assertions.assertThrows(NullPointerException.class, () -> clientService.existsById(id));
    }

    @Test
    void existsByName() {
    }

    @Test
    void existsByNameShouldThrowExceptionIfNameIsNull() {
        String name = null;
        Assertions.assertThrows(NullPointerException.class, () -> clientService.existsByName(name));
    }
}