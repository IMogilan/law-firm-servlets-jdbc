package com.mogilan.servlet.mapper;

import com.mogilan.model.ContactDetails;
import com.mogilan.model.LawFirm;
import com.mogilan.servlet.dto.ContactDetailsDto;
import com.mogilan.servlet.dto.LawFirmDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

//@Mapper
public interface LawFirmMapper {
//    LawFirmMapper INSTANCE = Mappers.getMapper(LawFirmMapper.class);

    LawFirmDto toDto(LawFirm entity);

    LawFirm toEntity(LawFirmDto dto);

    List<LawFirmDto> toDtoList(List<LawFirm> entity);

    List<LawFirm> toEntityList(List<LawFirmDto> dto);
}
