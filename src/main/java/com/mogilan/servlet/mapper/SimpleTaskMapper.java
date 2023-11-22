package com.mogilan.servlet.mapper;

import com.mogilan.servlet.dto.TaskDto;
import com.mogilan.servlet.dto.SimpleTaskDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SimpleTaskMapper {
    SimpleTaskMapper INSTANCE = Mappers.getMapper(SimpleTaskMapper.class);

    SimpleTaskDto toSimpleTaskDto(TaskDto taskDto);

    TaskDto toTask(SimpleTaskDto simpleTaskDto);

    List<SimpleTaskDto> toSimpleTaskDtoList(List<TaskDto> taskDtoList);

    List<TaskDto> toTaskDtoList(List<SimpleTaskDto> simpleTaskDtoList);
}
