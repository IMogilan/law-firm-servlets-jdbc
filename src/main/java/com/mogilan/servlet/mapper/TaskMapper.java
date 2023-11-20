package com.mogilan.servlet.mapper;

import com.mogilan.model.Lawyer;
import com.mogilan.model.Task;
import com.mogilan.servlet.dto.LawyerDto;
import com.mogilan.servlet.dto.TaskDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

//@Mapper(componentModel = "default", uses = {ClientMapper.class, LawyerMapper.class})
public interface TaskMapper {
//    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    @Mapping(target = "responsibleLawyers", source = "entity.lawyers")
    TaskDto toDto(Task entity);

    @Mapping(target = "lawyers", source = "dto.responsibleLawyers")
    Task toEntity(TaskDto dto);

    List<TaskDto> toDtoList(List<Task> entity);

    List<Task> toEntityList(List<TaskDto> dto);
}
