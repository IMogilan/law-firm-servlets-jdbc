package com.mogilan.servlet.mapper;

import com.mogilan.model.Task;
import com.mogilan.servlet.dto.TaskDto;
import org.mapstruct.Mapping;

import java.util.List;

public interface TaskMapper {

    @Mapping(target = "responsibleLawyers", source = "entity.lawyers")
    TaskDto toDto(Task entity);

    @Mapping(target = "lawyers", source = "dto.responsibleLawyers")
    Task toEntity(TaskDto dto);

    List<TaskDto> toDtoList(List<Task> entity);

    List<Task> toEntityList(List<TaskDto> dto);
}
