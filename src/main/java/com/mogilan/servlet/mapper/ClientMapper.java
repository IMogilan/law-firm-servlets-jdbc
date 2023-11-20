package com.mogilan.servlet.mapper;

import com.mogilan.model.*;
import com.mogilan.servlet.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

//@Mapper
public interface ClientMapper {

//    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    ClientDto toDto(Client entity);

    Client toEntity(ClientDto dto);

    List<ClientDto> toDtoList(List<Client> entity);

    List<Client> toEntityList(List<ClientDto> dto);

//    ClientDto toClientDto(Client client);
//
//    Client toClient(ClientDto clientDto);
//
//    List<ClientDto> toClientDtoList(List<Client> clientList);
//
//    List<Client> toClientList(List<ClientDto> clientDtoList);

//    ContactDetailsDto toContactDetailsDto(ContactDetails contactDetails);
//
//    ContactDetails toToContactDetails(ContactDetailsDto contactDetailsDto);
//
//    List<ContactDetailsDto> toContactDetailsDtoList(List<ContactDetails> contactDetailsList);
//
//    List<ContactDetails> toContactDetailsList(List<ContactDetailsDto> contactDetailsDtoList);
//
//    LawFirmDto lawFirmToLawFirmDto(LawFirm lawFirm);
//
//    LawFirm lawFirmDtoToLawFirm(LawFirmDto lawFirmDto);
//
//    List<LawFirmDto> toLawFirmDtoList(List<LawFirm> lawFirmList);
//
//    List<LawFirm> toLawFirmList(List<LawFirmDto> lawFirmDtoList);
//
//    LawyerDto lawyerToLawyerDto(Lawyer lawyer);
//
//    Lawyer lawyerDtoToLawyer(LawyerDto lawyerDto);
//
//    List<LawyerDto> toLawyerDtoList(List<Lawyer> lawyerList);
//
//    List<Lawyer> toLawyerList(List<LawyerDto> lawyerDtoList);
//
//    @Mapping(target = "responsibleLawyers", source = "task.lawyers")
//    TaskDto taskToTaskDto(Task task);
//
//    @Mapping(target = "lawyers", source = "taskDto.responsibleLawyers")
//    Task taskDtoToTask(TaskDto taskDto);
//
//    List<TaskDto> toTaskDtoList(List<Task> taskList);
//
//    List<Task> toTaskList(List<TaskDto> taskDtoList);

}
