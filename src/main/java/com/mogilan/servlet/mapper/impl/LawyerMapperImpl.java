package com.mogilan.servlet.mapper.impl;

import com.mogilan.model.Client;
import com.mogilan.model.LawFirm;
import com.mogilan.model.Lawyer;
import com.mogilan.model.Task;
import com.mogilan.servlet.dto.*;
import com.mogilan.servlet.mapper.*;

import java.util.Collections;
import java.util.List;

public class LawyerMapperImpl implements LawyerMapper {
    private final ContactDetailsMapper contactDetailsMapper;
    private final SimpleTaskMapper simpleTaskMapper;
    private final SimpleLawyerMapper simpleLawyerMapper;

    public LawyerMapperImpl(ContactDetailsMapper contactDetailsMapper, SimpleTaskMapper simpleTaskMapper, SimpleLawyerMapper simpleLawyerMapper) {
        this.contactDetailsMapper = contactDetailsMapper;
        this.simpleTaskMapper = simpleTaskMapper;
        this.simpleLawyerMapper = simpleLawyerMapper;
    }

    @Override
    public LawyerDto toDto(Lawyer lawyer) {
        if (lawyer == null) {
            return null;
        }
        var lawyerDto = new LawyerDto(lawyer.getId(), lawyer.getFirstName(), lawyer.getLastName(),
                lawyer.getJobTitle(), lawyer.getHourlyRate(), null,
                contactDetailsMapper.toDto(lawyer.getContacts()), null);

        var lawFirmDto = getLawFirmDto(lawyer, lawyerDto);
        lawyerDto.setLawFirm(lawFirmDto);

        var taskDtoList = getTaskDtoList(lawyer, lawyerDto);
        lawyerDto.setTasks(taskDtoList);

        return lawyerDto;
    }

    @Override
    public Lawyer toEntity(LawyerDto lawyerDto) {
        if (lawyerDto == null) {
            return null;
        }
        var lawyer = new Lawyer(lawyerDto.getId(), lawyerDto.getFirstName(), lawyerDto.getLastName(),
                lawyerDto.getJobTitle(), lawyerDto.getHourlyRate(), null,
                contactDetailsMapper.toEntity(lawyerDto.getContacts()), null);

        var lawFirm = getLawFirm(lawyerDto, lawyer);
        lawyer.setLawFirm(lawFirm);

        var taskEntityList = getTaskEntityList(lawyerDto, lawyer);
        lawyer.setTasks(taskEntityList);

        return lawyer;
    }

    @Override
    public List<LawyerDto> toDtoList(List<Lawyer> lawyerList) {
        if (lawyerList == null) {
            return Collections.emptyList();
        }
        return lawyerList.stream().map(this::toDto).toList();
    }

    @Override
    public List<Lawyer> toEntityList(List<LawyerDto> lawyerDtoList) {
        if (lawyerDtoList == null) {
            return Collections.emptyList();
        }
        return lawyerDtoList.stream().map(this::toEntity).toList();
    }

    private LawFirmDto getLawFirmDto(Lawyer lawyer, LawyerDto resultDto) {
        var lawFirm = lawyer.getLawFirm();
        if (lawFirm == null) {
            return null;
        }
        var simpleLawyerDtoList = simpleLawyerMapper.toSimpleLawyerDtoList(List.of(resultDto));
        return new LawFirmDto(lawFirm.getId(), lawFirm.getName(), lawFirm.getCompanyStartDay(), simpleLawyerDtoList);
    }

    private LawFirm getLawFirm(LawyerDto lawyerDto, Lawyer resultEntity) {
        var lawFirmDto = lawyerDto.getLawFirm();
        if (lawFirmDto == null) {
            return null;
        }
        return new LawFirm(lawFirmDto.getId(), lawFirmDto.getName(), lawFirmDto.getCompanyStartDay(), List.of(resultEntity));
    }

    private List<TaskDto> getTaskDtoList(Lawyer lawyer, LawyerDto resultDto) {
        if (lawyer.getTasks() == null) {
            return Collections.emptyList();
        }
        return lawyer.getTasks().stream()
                .map(task -> getTaskDto(resultDto, task)).toList();
    }

    private TaskDto getTaskDto(LawyerDto resultDto, Task task) {
        if (task == null) {
            return null;
        }
        var taskDto = new TaskDto(task.getId(), task.getTitle(), task.getDescription(), task.getPriority(),
                task.getStatus(), task.getReceiptDate(), task.getDueDate(), task.getCompletionDate(),
                task.getHoursSpentOnTask(), null, List.of(resultDto));

        var clientDto = getClientDto(task, taskDto);
        taskDto.setClient(clientDto);

        return taskDto;
    }

    private ClientDto getClientDto(Task task, TaskDto resultDto) {
        var client = task.getClient();
        if (client == null) {
            return null;
        }
        var simpleTaskDtoList = simpleTaskMapper.toSimpleTaskDtoList(List.of(resultDto));
        return new ClientDto(client.getId(), client.getName(), client.getDescription(), simpleTaskDtoList);
    }


    private List<Task> getTaskEntityList(LawyerDto lawyerDto, Lawyer resultEntity) {
        if (lawyerDto.getTasks() == null) {
            return Collections.emptyList();
        }
        return lawyerDto.getTasks().stream()
                .map(taskDto -> getTask(resultEntity, taskDto)).toList();
    }

    private Task getTask(Lawyer resultEntity, TaskDto taskDto) {
        if (taskDto == null) {
            return null;
        }
        var taskEntity = new Task(taskDto.getId(), taskDto.getTitle(), taskDto.getDescription(), taskDto.getPriority(),
                taskDto.getStatus(), taskDto.getReceiptDate(), taskDto.getDueDate(), taskDto.getCompletionDate(),
                taskDto.getHoursSpentOnTask(), null, List.of(resultEntity));

        var clientDto = getClientEntity(taskDto, taskEntity);
        taskEntity.setClient(clientDto);

        return taskEntity;
    }

    private Client getClientEntity(TaskDto taskDto, Task resultEntity) {
        var clientDto = taskDto.getClient();
        if (clientDto == null) {
            return null;
        }
        return new Client(clientDto.getId(), clientDto.getName(), clientDto.getDescription(), List.of(resultEntity));
    }
}
