package com.mogilan.servlet.mapper.impl;

import com.mogilan.model.Client;
import com.mogilan.model.LawFirm;
import com.mogilan.model.Lawyer;
import com.mogilan.model.Task;
import com.mogilan.servlet.dto.*;
import com.mogilan.servlet.mapper.ClientMapper;
import com.mogilan.servlet.mapper.ContactDetailsMapper;
import com.mogilan.servlet.mapper.SimpleLawyerMapper;
import com.mogilan.servlet.mapper.SimpleTaskMapper;

import java.util.Collections;
import java.util.List;

public class ClientMapperImpl implements ClientMapper {

    private final ContactDetailsMapper contactDetailsMapper;
    private final SimpleTaskMapper simpleTaskMapper;
    private final SimpleLawyerMapper simpleLawyerMapper;

    public ClientMapperImpl(ContactDetailsMapper contactDetailsMapper, SimpleTaskMapper simpleTaskMapper, SimpleLawyerMapper simpleLawyerMapper) {
        this.contactDetailsMapper = contactDetailsMapper;
        this.simpleTaskMapper = simpleTaskMapper;
        this.simpleLawyerMapper = simpleLawyerMapper;
    }

    @Override
    public ClientDto toDto(Client client) {
        if (client == null) {
            return null;
        }
        var clientDto = new ClientDto(client.getId(), client.getName(), client.getDescription(), null);

        var taskDtoList = getTaskDtoList(client, clientDto);
        var simpleTaskDtoList = simpleTaskMapper.toSimpleTaskDtoList(taskDtoList);
        clientDto.setTasks(simpleTaskDtoList);

        return clientDto;
    }

    @Override
    public Client toEntity(ClientDto clientDto) {
        if (clientDto == null) {
            return null;
        }
        var client = new Client(clientDto.getId(), clientDto.getName(), clientDto.getDescription(), null);

        var taskEntityList = getTaskEntityList(clientDto, client);
        client.setTasks(taskEntityList);

        return client;
    }

    @Override
    public List<ClientDto> toDtoList(List<Client> clientList) {
        if (clientList == null) {
            return Collections.emptyList();
        }
        return clientList.stream().map(this::toDto).toList();
    }

    @Override
    public List<Client> toEntityList(List<ClientDto> clientDtoList) {
        if (clientDtoList == null) {
            return Collections.emptyList();
        }
        return clientDtoList.stream().map(this::toEntity).toList();
    }

    private List<TaskDto> getTaskDtoList(Client client, ClientDto clientDto) {
        if (client.getTasks() == null) {
            return Collections.emptyList();
        }
        return client.getTasks().stream()
                .map(task -> getTaskDto(clientDto, task)).toList();
    }

    private TaskDto getTaskDto(ClientDto clientDto, Task task) {
        if (task == null) {
            return null;
        }
        var taskDto = new TaskDto(task.getId(), task.getTitle(), task.getDescription(), task.getPriority(),
                task.getStatus(), task.getReceiptDate(), task.getDueDate(), task.getCompletionDate(),
                task.getHoursSpentOnTask(), clientDto, null);

        var lawyerDtoList = getLawyerDtoList(task, taskDto);
        taskDto.setLawyers(lawyerDtoList);

        return taskDto;
    }

    private List<LawyerDto> getLawyerDtoList(Task task, TaskDto taskDto) {
        if (task.getLawyers() == null) {
            return Collections.emptyList();
        }
        return task.getLawyers().stream()
                .map(lawyer -> getLawyerDto(taskDto, lawyer)).toList();
    }

    private LawyerDto getLawyerDto(TaskDto resultDtoCopy, Lawyer lawyer) {
        if (lawyer == null) {
            return null;
        }
        var lawyerDto = new LawyerDto(lawyer.getId(), lawyer.getFirstName(), lawyer.getLastName(),
                lawyer.getJobTitle(), lawyer.getHourlyRate(), null,
                contactDetailsMapper.toDto(lawyer.getContacts()), List.of(resultDtoCopy));

        var lawFirmDto = getLawFirmDto(lawyer, lawyerDto);
        lawyerDto.setLawFirm(lawFirmDto);

        return lawyerDto;
    }

    private LawFirmDto getLawFirmDto(Lawyer lawyer, LawyerDto lawyerDto) {
        var lawFirm = lawyer.getLawFirm();
        if (lawFirm == null) {
            return null;
        }
        var simpleLawyerDtoList = simpleLawyerMapper.toSimpleLawyerDtoList(List.of(lawyerDto));
        return new LawFirmDto(lawFirm.getId(), lawFirm.getName(), lawFirm.getCompanyStartDay(), simpleLawyerDtoList);
    }

    private List<Task> getTaskEntityList(ClientDto clientDto, Client client) {
        if (clientDto.getTasks() == null) {
            return Collections.emptyList();
        }
        return clientDto.getTasks().stream()
                .map(taskDto -> getTask(client, taskDto)).toList();
    }

    private Task getTask(Client client, SimpleTaskDto taskDto) {
        if (taskDto == null) {
            return null;
        }
        var taskEntity = new Task(taskDto.getId(), taskDto.getTitle(), taskDto.getDescription(), taskDto.getPriority(),
                taskDto.getStatus(), taskDto.getReceiptDate(), taskDto.getDueDate(), taskDto.getCompletionDate(),
                taskDto.getHoursSpentOnTask(), client, null);

        var lawyerList = getLawyerList(taskDto, taskEntity);
        taskEntity.setLawyers(lawyerList);

        return taskEntity;
    }

    private List<Lawyer> getLawyerList(SimpleTaskDto taskDto, Task taskEntityCopy) {
        if (taskDto.getLawyers() == null) {
            return Collections.emptyList();
        }
        return taskDto.getLawyers().stream()
                .map(lawyerDto -> getLawyer(taskEntityCopy, lawyerDto)).toList();
    }

    private Lawyer getLawyer(Task resultEntity, LawyerDto lawyerDto) {
        if (lawyerDto == null) {
            return null;
        }
        var lawyer = new Lawyer(lawyerDto.getId(), lawyerDto.getFirstName(), lawyerDto.getLastName(),
                lawyerDto.getJobTitle(), lawyerDto.getHourlyRate(), null,
                contactDetailsMapper.toEntity(lawyerDto.getContacts()), List.of(resultEntity));

        LawFirm lawFirm = getLawFirm(lawyerDto, lawyer);
        lawyer.setLawFirm(lawFirm);

        return lawyer;
    }

    private LawFirm getLawFirm(LawyerDto lawyerDto, Lawyer lawyerCopy) {
        var lawFirmDto = lawyerDto.getLawFirm();
        if (lawFirmDto == null) {
            return null;
        }
        return new LawFirm(lawFirmDto.getId(), lawFirmDto.getName(), lawFirmDto.getCompanyStartDay(), List.of(lawyerCopy));
    }
}
