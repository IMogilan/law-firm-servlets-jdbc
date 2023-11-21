package com.mogilan.servlet.mapper;

import com.mogilan.model.LawFirm;
import com.mogilan.servlet.dto.LawFirmDto;

import java.util.List;

public interface LawFirmMapper {

    LawFirmDto toDto(LawFirm entity);

    LawFirm toEntity(LawFirmDto dto);

    List<LawFirmDto> toDtoList(List<LawFirm> entity);

    List<LawFirm> toEntityList(List<LawFirmDto> dto);
}
