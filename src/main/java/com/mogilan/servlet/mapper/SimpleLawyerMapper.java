package com.mogilan.servlet.mapper;

import com.mogilan.servlet.dto.LawyerDto;
import com.mogilan.servlet.dto.SimpleLawyerDto;

import java.util.List;

public interface SimpleLawyerMapper {
    SimpleLawyerDto toSimpleLawyerDto(LawyerDto lawyerDto);

    LawyerDto toLawyerDto(SimpleLawyerDto simpleLawyerDto);

    List<SimpleLawyerDto> toSimpleLawyerDtoList(List<LawyerDto> lawyerDtoList);

    List<LawyerDto> toLawyerDtoList(List<SimpleLawyerDto> simpleLawyerDtoList);

}
