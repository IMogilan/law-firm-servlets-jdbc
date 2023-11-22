package com.mogilan.servlet.mapper.impl;

import com.mogilan.servlet.dto.LawyerDto;
import com.mogilan.servlet.dto.SimpleLawyerDto;
import com.mogilan.servlet.mapper.SimpleLawyerMapper;

import java.util.Collections;
import java.util.List;

public class SimpleLawyerMapperImpl implements SimpleLawyerMapper {

    public SimpleLawyerMapperImpl() {
    }

    @Override
    public SimpleLawyerDto toSimpleLawyerDto(LawyerDto lawyerDto) {
        if (lawyerDto == null) {
            return null;
        }
        return new SimpleLawyerDto(lawyerDto.getId(), lawyerDto.getFirstName(), lawyerDto.getLastName(),
                lawyerDto.getJobTitle(), lawyerDto.getHourlyRate(), lawyerDto.getContacts(), lawyerDto.getTasks());
    }

    @Override
    public LawyerDto toLawyerDto(SimpleLawyerDto simpleLawyerDto) {
        if (simpleLawyerDto == null) {
            return null;
        }
        return new LawyerDto(simpleLawyerDto.getId(), simpleLawyerDto.getFirstName(), simpleLawyerDto.getLastName(),
                simpleLawyerDto.getJobTitle(), simpleLawyerDto.getHourlyRate(), null, simpleLawyerDto.getContacts(),
                simpleLawyerDto.getTasks());
    }

    @Override
    public List<SimpleLawyerDto> toSimpleLawyerDtoList(List<LawyerDto> lawyerDtoList) {
        if (lawyerDtoList == null) {
            return Collections.emptyList();
        }
        return lawyerDtoList.stream().map(this::toSimpleLawyerDto).toList();    }

    @Override
    public List<LawyerDto> toLawyerDtoList(List<SimpleLawyerDto> simpleLawyerDtoList) {
        if (simpleLawyerDtoList == null) {
            return Collections.emptyList();
        }
        return simpleLawyerDtoList.stream().map(this::toLawyerDto).toList();
    }
}
