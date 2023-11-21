package com.mogilan.servlet.mapper;

import com.mogilan.model.Lawyer;
import com.mogilan.servlet.dto.LawyerDto;

import java.util.List;

public interface LawyerMapper {

    LawyerDto toDto(Lawyer entity);

    Lawyer toEntity(LawyerDto dto);

    List<LawyerDto> toDtoList(List<Lawyer> entity);

    List<Lawyer> toEntityList(List<LawyerDto> dto);
}
