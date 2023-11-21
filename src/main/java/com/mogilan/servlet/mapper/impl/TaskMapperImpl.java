package com.mogilan.servlet.mapper.impl;

import com.mogilan.model.Client;
import com.mogilan.model.LawFirm;
import com.mogilan.model.Lawyer;
import com.mogilan.model.Task;
import com.mogilan.servlet.dto.ClientDto;
import com.mogilan.servlet.dto.LawFirmDto;
import com.mogilan.servlet.dto.LawyerDto;
import com.mogilan.servlet.dto.TaskDto;
import com.mogilan.servlet.mapper.ContactDetailsMapper;
import com.mogilan.servlet.mapper.TaskMapper;

import java.util.Collections;
import java.util.List;

public class TaskMapperImpl implements TaskMapper {

    private static final TaskMapperImpl INSTANCE = new TaskMapperImpl();
    private final ContactDetailsMapper contactDetailsMapper = ContactDetailsMapperImpl.getInstance();

    private TaskMapperImpl() {
    }

    @Override
    public TaskDto toDto(Task task) {
        if (task == null) {
            return null;
        }
        var taskDto = new TaskDto(task.getId(), task.getTitle(), task.getDescription(), task.getPriority(),
                task.getStatus(), task.getReceiptDate(), task.getDueDate(), task.getCompletionDate(),
                task.getHoursSpentOnTask(), null, null);

        var clientDto = getClientDto(task, taskDto);
        taskDto.setClient(clientDto);

        var lawyerDtoList = getLawyerDtoList(task, taskDto);
        taskDto.setLawyers(lawyerDtoList);

        return taskDto;
    }

    @Override
    public Task toEntity(TaskDto taskDto) {
        if (taskDto == null) {
            return null;
        }
        var task = new Task(taskDto.getId(), taskDto.getTitle(), taskDto.getDescription(), taskDto.getPriority(),
                taskDto.getStatus(), taskDto.getReceiptDate(), taskDto.getDueDate(), taskDto.getCompletionDate(),
                taskDto.getHoursSpentOnTask(), null, null);
        var clientDto = getClientEntity(taskDto, task);
        task.setClient(clientDto);

        var lawyerList = getLawyerList(taskDto, task);
        task.setLawyers(lawyerList);

        return task;
    }

    @Override
    public List<TaskDto> toDtoList(List<Task> entityList) {
        if (entityList == null) {
            return Collections.emptyList();
        }
        return entityList.stream().map(this::toDto).toList();
    }

    @Override
    public List<Task> toEntityList(List<TaskDto> dtoList) {
        if (dtoList == null) {
            return Collections.emptyList();
        }
        return dtoList.stream().map(this::toEntity).toList();
    }

    public static TaskMapperImpl getInstance() {
        return INSTANCE;
    }

    private ClientDto getClientDto(Task task, TaskDto resultDto) {
        var client = task.getClient();
        if (client == null) {
            return null;
        }
        return new ClientDto(client.getId(), client.getName(), client.getDescription(), List.of(resultDto));
    }

    private Client getClientEntity(TaskDto taskDto, Task resultEntity) {
        var clientDto = taskDto.getClient();
        if (clientDto == null) {
            return null;
        }
        return new Client(clientDto.getId(), clientDto.getName(), clientDto.getDescription(), List.of(resultEntity));
    }

    private List<LawyerDto> getLawyerDtoList(Task task, TaskDto resultDto) {
        if (task.getLawyers() == null) {
            return Collections.emptyList();
        }
        return task.getLawyers().stream()
                .map(lawyer -> getLawyerDto(resultDto, lawyer)).toList();
    }

    private LawyerDto getLawyerDto(TaskDto resultDto, Lawyer lawyer) {
        if (lawyer == null) {
            return null;
        }
        var lawyerDto = new LawyerDto(lawyer.getId(), lawyer.getFirstName(), lawyer.getLastName(),
                lawyer.getJobTitle(), lawyer.getHourlyRate(), null,
                contactDetailsMapper.toDto(lawyer.getContacts()), List.of(resultDto));

        var lawFirmDto = getLawFirmDto(lawyer, lawyerDto);
        lawyerDto.setLawFirm(lawFirmDto);

        return lawyerDto;
    }

    private LawFirmDto getLawFirmDto(Lawyer lawyer, LawyerDto resultDto) {
        var lawFirm = lawyer.getLawFirm();
        if (lawFirm == null) {
            return null;
        }
        return new LawFirmDto(lawFirm.getId(), lawFirm.getName(), lawFirm.getCompanyStartDay(), List.of(resultDto));
    }

    private List<Lawyer> getLawyerList(TaskDto taskDto, Task resultEntity) {
        if (taskDto.getLawyers() == null) {
            return Collections.emptyList();
        }
        return taskDto.getLawyers().stream()
                .map(lawyerDto -> getLawyer(resultEntity, lawyerDto)).toList();
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

    private LawFirm getLawFirm(LawyerDto lawyerDto, Lawyer resultEntity) {
        var lawFirmDto = lawyerDto.getLawFirm();
        if (lawFirmDto == null) {
            return null;
        }
        return new LawFirm(lawFirmDto.getId(), lawFirmDto.getName(), lawFirmDto.getCompanyStartDay(), List.of(resultEntity));
    }

}
