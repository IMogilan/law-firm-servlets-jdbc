package com.mogilan.servlet.mapper.impl;

import com.mogilan.model.Client;
import com.mogilan.model.LawFirm;
import com.mogilan.model.Lawyer;
import com.mogilan.model.Task;
import com.mogilan.servlet.dto.ClientDto;
import com.mogilan.servlet.dto.LawFirmDto;
import com.mogilan.servlet.dto.LawyerDto;
import com.mogilan.servlet.dto.TaskDto;
import com.mogilan.servlet.mapper.ClientMapper;
import com.mogilan.servlet.mapper.ContactDetailsMapper;

import java.util.Collections;
import java.util.List;

public class ClientMapperImpl implements ClientMapper {

    private static final ClientMapperImpl INSTANCE = new ClientMapperImpl();
    private final ContactDetailsMapper contactDetailsMapper = ContactDetailsMapperImpl.getInstance();

    private ClientMapperImpl() {
    }

    @Override
    public ClientDto toDto(Client client) {
        if (client == null) {
            return null;
        }
        var clientDto = new ClientDto(client.getId(), client.getName(), client.getDescription(), null);
        var clientDtoCopy = new ClientDto(client.getId(), client.getName(), client.getDescription(), null);

        var taskDtoList = getTaskDtoList(client, clientDto);
        clientDto.setTasks(taskDtoList);

        return clientDto;
    }

    @Override
    public Client toEntity(ClientDto clientDto) {
        if (clientDto == null) {
            return null;
        }
        var client = new Client(clientDto.getId(), clientDto.getName(), clientDto.getDescription(), null);
        var clientCopy = new Client(clientDto.getId(), clientDto.getName(), clientDto.getDescription(), null);

        var taskEntityList = getTaskEntityList(clientDto, clientCopy);
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

    public static ClientMapperImpl getInstance() {
        return INSTANCE;
    }

    private List<TaskDto> getTaskDtoList(Client client, ClientDto resultDtoCopy) {
        if (client.getTasks() == null) {
            return Collections.emptyList();
        }
        return client.getTasks().stream()
                .map(task -> getTaskDto(resultDtoCopy, task)).toList();
    }

    private TaskDto getTaskDto(ClientDto resultDtoCopy, Task task) {
        if (task == null) {
            return null;
        }
        var taskDto = new TaskDto(task.getId(), task.getTitle(), task.getDescription(), task.getPriority(),
                task.getStatus(), task.getReceiptDate(), task.getDueDate(), task.getCompletionDate(),
                task.getHoursSpentOnTask(), resultDtoCopy, null);
        var taskDtoCopy = new TaskDto(task.getId(), task.getTitle(), task.getDescription(), task.getPriority(),
                task.getStatus(), task.getReceiptDate(), task.getDueDate(), task.getCompletionDate(),
                task.getHoursSpentOnTask(), resultDtoCopy, null);

        var lawyerDtoList = getLawyerDtoList(task, taskDto);
        taskDto.setLawyers(lawyerDtoList);

        return taskDto;
    }

    private List<LawyerDto> getLawyerDtoList(Task task, TaskDto resultDtoCopy) {
        if (task.getLawyers() == null) {
            return Collections.emptyList();
        }
        return task.getLawyers().stream()
                .map(lawyer -> getLawyerDto(resultDtoCopy, lawyer)).toList();
    }

    private LawyerDto getLawyerDto(TaskDto resultDtoCopy, Lawyer lawyer) {
        if (lawyer == null) {
            return null;
        }
        var lawyerDto = new LawyerDto(lawyer.getId(), lawyer.getFirstName(), lawyer.getLastName(),
                lawyer.getJobTitle(), lawyer.getHourlyRate(), null,
                contactDetailsMapper.toDto(lawyer.getContacts()), List.of(resultDtoCopy));
        var lawyerDtoCopy = new LawyerDto(lawyer.getId(), lawyer.getFirstName(), lawyer.getLastName(),
                lawyer.getJobTitle(), lawyer.getHourlyRate(), null,
                contactDetailsMapper.toDto(lawyer.getContacts()), List.of(resultDtoCopy));

        var lawFirmDto = getLawFirmDto(lawyer, lawyerDtoCopy);
        lawyerDto.setLawFirm(lawFirmDto);

        return lawyerDto;
    }

    private LawFirmDto getLawFirmDto(Lawyer lawyer, LawyerDto resultDtoCopy) {
        var lawFirm = lawyer.getLawFirm();
        if (lawFirm == null) {
            return null;
        }
        return new LawFirmDto(lawFirm.getId(), lawFirm.getName(), lawFirm.getCompanyStartDay(), List.of(resultDtoCopy));
    }

    private List<Task> getTaskEntityList(ClientDto clientDto, Client resultEntityCopy) {
        if(clientDto.getTasks() == null){
            return Collections.emptyList();
        }
        return clientDto.getTasks().stream()
                .map(taskDto -> getTask(resultEntityCopy, taskDto)).toList();
    }

    private Task getTask(Client resultEntityCopy, TaskDto taskDto) {
        if (taskDto == null) {
            return null;
        }
        var taskEntity = new Task(taskDto.getId(), taskDto.getTitle(), taskDto.getDescription(), taskDto.getPriority(),
                taskDto.getStatus(), taskDto.getReceiptDate(), taskDto.getDueDate(), taskDto.getCompletionDate(),
                taskDto.getHoursSpentOnTask(), resultEntityCopy, null);
        var taskEntityCopy = new Task(taskDto.getId(), taskDto.getTitle(), taskDto.getDescription(), taskDto.getPriority(),
                taskDto.getStatus(), taskDto.getReceiptDate(), taskDto.getDueDate(), taskDto.getCompletionDate(),
                taskDto.getHoursSpentOnTask(), resultEntityCopy, null);

        var lawyerList = getLawyerList(taskDto, taskEntityCopy);
        taskEntity.setLawyers(lawyerList);

        return taskEntity;
    }

    private List<Lawyer> getLawyerList(TaskDto taskDto, Task taskEntityCopy) {
        if(taskDto.getLawyers() == null){
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
        var lawyerCopy = new Lawyer(lawyerDto.getId(), lawyerDto.getFirstName(), lawyerDto.getLastName(),
                lawyerDto.getJobTitle(), lawyerDto.getHourlyRate(), null,
                contactDetailsMapper.toEntity(lawyerDto.getContacts()), List.of(resultEntity));

        LawFirm lawFirm = getLawFirm(lawyerDto, lawyerCopy);
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
