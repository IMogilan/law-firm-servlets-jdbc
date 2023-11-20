package com.mogilan.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogilan.model.*;
import com.mogilan.repository.impl.*;
import com.mogilan.service.*;
import com.mogilan.service.impl.*;
import com.mogilan.servlet.dto.*;
import com.mogilan.servlet.mapper.ClientMapper;
import com.mogilan.servlet.mapper.ContactDetailsMapper;
import com.mogilan.servlet.mapper.LawyerMapper;
import com.mogilan.servlet.mapper.TaskMapper;
import com.mogilan.servlet.mapper.impl.ClientMapperImpl;
import com.mogilan.servlet.mapper.impl.ContactDetailsMapperImpl;
import com.mogilan.servlet.mapper.impl.LawyerMapperImpl;
import org.mapstruct.factory.Mappers;

import java.io.File;
import java.io.Writer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class TestConnection {

    public static void main(String[] args) {

        LawFirmService lawFirmService = LawFirmServiceImpl.getInstance();
        LawyerService lawyerService = LawyerServiceImpl.getInstance();
        ClientService clientService = ClientServiceImpl.getInstance();
        TaskService taskService = TaskServiceImpl.getInstance();
        ContactDetailsService contactDetailsService = ContactDetailsServiceImpl.getInstance();
//        var taskDto1 = new TaskDto("1","ASAP",TaskPriority.HIGH, TaskStatus.RECEIVED, LocalDate.now(), LocalDate.now(),null, 0.0, null, new ArrayList<>());
//        var taskDto2 = new TaskDto("2","ASAP",TaskPriority.HIGH, TaskStatus.RECEIVED, LocalDate.now(), LocalDate.now(),null, 0.0, null, new ArrayList<>());
//        var taskDto3 = new TaskDto("3","ASAP",TaskPriority.HIGH, TaskStatus.RECEIVED, LocalDate.now(), LocalDate.now(),null, 0.0, null, new ArrayList<>());
//
//        var lawyerDto1 = new LawyerDto("1", "L", JobTitle.MANAGING_PARTNER, 200, null, new ContactDetailsDto(), new ArrayList<>());
//        var lawyerDto2 = new LawyerDto("2", "L", JobTitle.MANAGING_PARTNER, 200, null, new ContactDetailsDto(), new ArrayList<>());
//        var lawyerDto3 = new LawyerDto("3", "L", JobTitle.MANAGING_PARTNER, 200, null, new ContactDetailsDto(), new ArrayList<>());
//
//

        var T1 = taskService.readById(3L);
        var L1 = lawyerService.readById(1L);
        var L2 = lawyerService.readById(2L);
        var L3 = lawyerService.readById(3L);

        var clientDto = clientService.readById(1L);
        System.out.println(clientDto);
//        T1.setLawyers(new ArrayList<>(List.of(L1, L2, L3)));
//        taskService.update(T1.getId(), T1);
//        var taskDtoS1 = taskService.create(taskDto1);
//        lawyerDto1.getTasks().add(taskDtoS1);
//        lawyerService.create(lawyerDto1);
//
//
////
//        var lawyerDtoS2 = lawyerService.create(lawyerDto2);
//        taskDto2.getLawyers().add(lawyerDtoS2);
//        taskService.create(taskDto2);
//
//        var clientDto = new ClientDto("Apple", "Iphone", null);
//        clientService.readAll(clientDto.getId());
//
//        var L = new LawyerDto("A", "L", JobTitle.MANAGING_PARTNER, 200, null, new ContactDetailsDto(), null);
//        var vmp = new LawFirmDto("SPP", LocalDate.now(), List.of(lawyerDto1));
//        lawFirmService.create(vmp);

//        objectMapper.writeValue(Writer, clientDtos);
//        clientDtos.setTasks(List.of(T1));
//        clientService.update(1L, clientDtos);
//        System.out.println(clientDtos);
//
//        var lawyerDto = lawyerService.readById(1L);
//        taskService.create(taskDto);
//        var clientDto1 = clientService.readById(2L);
//        System.out.println(clientDto1);
//        clientDto1.setTasks(List.of(taskDto));
//        clientService.update(clientDto1.getId(), clientDto1);

//        System.out.println(clientService.readById(1L).getTasks());
//        System.out.println(taskService.readById(1L).getLawyers());
//        System.out.println(lawFirmService.readById(1L).getLawyers());
//        System.out.println(lawyerService.readById(1L).getTasks());
//        System.out.println(contactDetailsService.readById(1L));

//        clientService.deleteById(2L);
//        taskService.deleteById(1L);
//        lawFirmService.deleteById(1L);
//        lawyerService.deleteById(1L);
//        contactDetailsService.deleteById(1L);


    }

    private static void testDao() {
        var contactDetailsDao = ContactDetailsDaoImpl.getInstance();
        var lawFirmDao = LawFirmDaoImpl.getInstance();
        var lawyerDao = LawyerDaoImpl.getInstance();
        var clientDao = ClientDaoImpl.getInstance();
        var taskDao = TaskDaoImpl.getInstance();
        var contactDetailsMapper = ContactDetailsMapperImpl.getInstance();
        var clientMapper = ClientMapperImpl.getInstance();
        var lawyerMapper = LawyerMapperImpl.getInstance(); //Mappers.getMapper(LawyerMapper.class);
        var taskMapper = Mappers.getMapper(TaskMapper.class);
//        var lawyerMapper = Mappers.getMapper(LawyerMapper.class);


        var vmp = new LawFirm("VMP", LocalDate.of(1989, 1, 1));
        var spp = new LawFirm("SPP", LocalDate.of(1991, 1, 1));
        var sbh = new LawFirm("SBH", LocalDate.of(2015, 1, 1));

        var KM = new Lawyer("K", "M", JobTitle.MANAGING_PARTNER, 200.0, vmp, null);
        var OG = new Lawyer("O", "G", JobTitle.PARTNER, 150, vmp, null);
        var XS = new Lawyer("X", "S", JobTitle.MANAGING_PARTNER, 200.0, spp, null);
        var AL = new Lawyer("A", "L", JobTitle.PARTNER, 150, spp, null);
        var XP = new Lawyer("X", "P", JobTitle.SENIOR_PARTNER, 200.0, spp, null);
        var TS = new Lawyer("T", "S", JobTitle.MANAGING_PARTNER, 200.0, sbh, null);
        var AH = new Lawyer("A", "H", JobTitle.PARTNER, 150, sbh, null);

        lawFirmDao.save(vmp);
        lawFirmDao.save(spp);
        lawFirmDao.save(sbh);


        var KMSaved = lawyerDao.save(KM);
        var OGSaved = lawyerDao.save(OG);
        var XSSaved = lawyerDao.save(XS);
        var ALSaved = lawyerDao.save(AL);
        var XPSaved = lawyerDao.save(XP);
        var TSSaved = lawyerDao.save(TS);
        var AHSaved = lawyerDao.save(AH);

        System.out.println(lawFirmDao.findAll());
        System.out.println(lawyerDao.findAll());

        var contactDetails1 = new ContactDetails(KMSaved.getId(), "Minsk", null, null, null, "test@email.com");
        var contactDetails2 = new ContactDetails(OGSaved.getId(), "Minsk", null, null, null, "test@email.com");
        var contactDetails3 = new ContactDetails(XSSaved.getId(), "Minsk", null, null, null, "test@email.com");
        var contactDetails4 = new ContactDetails(ALSaved.getId(), "Minsk", null, null, null, "test@email.com");
        var contactDetails5 = new ContactDetails(XPSaved.getId(), "Minsk", null, null, null, "test@email.com");
        var contactDetails6 = new ContactDetails(TSSaved.getId(), "Minsk", null, null, null, "test@email.com");
        var contactDetails7 = new ContactDetails(AHSaved.getId(), "Minsk", null, null, null, "test@email.com");

        contactDetailsDao.save(contactDetails1);
        contactDetailsDao.save(contactDetails2);
        contactDetailsDao.save(contactDetails3);
        contactDetailsDao.save(contactDetails4);
        contactDetailsDao.save(contactDetails5);
        contactDetailsDao.save(contactDetails6);
        contactDetailsDao.save(contactDetails7);

        var lawyer1 = lawyerDao.findById(1L);
        var lawyer2 = lawyerDao.findById(2L);
        var lawyer3 = lawyerDao.findById(3L);
        var lawyer4 = lawyerDao.findById(4L);
        var lawyer5 = lawyerDao.findById(5L);
        var lawyer6 = lawyerDao.findById(6L);

        var client1 = new Client("EY", "Top4");
        var client2 = new Client("KPMG", "Top4");
        var client3 = new Client("Delloite", "Top4");

        var task1 = new Task("first", "X", TaskPriority.LOW, TaskStatus.ACCEPTED,
                LocalDate.of(2023, 11, 11),
                LocalDate.of(2023, 11, 18),
                client1
        );
        var task2 = new Task("second", "X", TaskPriority.HIGH, TaskStatus.RECEIVED,
                LocalDate.of(2023, 11, 11),
                LocalDate.of(2023, 11, 18),
                client2
        );
        var task3 = new Task("third", "X", TaskPriority.MEDIUM, TaskStatus.COMPLETED,
                LocalDate.of(2023, 11, 11),
                LocalDate.of(2023, 11, 18),
                client3
        );

        clientDao.save(client1);
        clientDao.save(client2);
        clientDao.save(client3);

        taskDao.save(task1);
        taskDao.save(task2);
        taskDao.save(task3);
        var contactDetails = contactDetailsDao.findById(1L).get();
        System.out.println(contactDetails);
        var contactDetailsDto = contactDetailsMapper.toDto(contactDetails);
        System.out.println(contactDetailsDto);

        System.out.println();

        var lawyer = lawyerDao.findById(1L);
        System.out.println(lawyer.get());
        var lawyerDto = lawyerMapper.toDto(lawyer.get());
        System.out.println(lawyerDto);

//        System.out.println(taskDao.findAll());

        var client = clientDao.findById(1L).get();
        System.out.println(client);
        var clientDto = clientMapper.toDto(client);
        System.out.println(clientDto);

        var task = taskDao.findById(1L).get();
        System.out.println(task);
        System.out.println(taskMapper.toDto(task));


        System.out.println();

        //
////        lawFirmDao.delete(L);
////        lawyerDao.delete();

        System.out.println(lawyerDao.findById(1L));
        System.out.println(lawFirmDao.findAll());
        System.out.println(lawyerDao.findAll());
        System.out.println(contactDetailsDao.findAll());
    }
}
