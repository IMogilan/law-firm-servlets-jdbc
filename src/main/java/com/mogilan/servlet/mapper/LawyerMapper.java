package com.mogilan.servlet.mapper;

import com.mogilan.model.LawFirm;
import com.mogilan.model.Lawyer;
import com.mogilan.servlet.dto.LawFirmDto;
import com.mogilan.servlet.dto.LawyerDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

//@Mapper
public interface LawyerMapper {
//    LawyerMapper INSTANCE = Mappers.getMapper(LawyerMapper.class);

    LawyerDto toDto(Lawyer entity);

    Lawyer toEntity(LawyerDto dto);

    List<LawyerDto> toDtoList(List<Lawyer> entity);

    List<Lawyer> toEntityList(List<LawyerDto> dto);
}
