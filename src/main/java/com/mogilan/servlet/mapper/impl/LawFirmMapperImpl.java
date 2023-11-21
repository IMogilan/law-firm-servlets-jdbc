package com.mogilan.servlet.mapper.impl;

import com.mogilan.model.LawFirm;
import com.mogilan.servlet.dto.LawFirmDto;
import com.mogilan.servlet.mapper.LawFirmMapper;
import com.mogilan.servlet.mapper.LawyerMapper;

import java.util.Collections;
import java.util.List;

public class LawFirmMapperImpl implements LawFirmMapper {

    private final LawyerMapper lawyerMapper;

    public LawFirmMapperImpl(LawyerMapper lawyerMapper) {
        this.lawyerMapper = lawyerMapper;
    }

    @Override
    public LawFirmDto toDto(LawFirm lawFirm) {
        if (lawFirm == null) {
            return null;
        }
        return new LawFirmDto(lawFirm.getId(), lawFirm.getName(), lawFirm.getCompanyStartDay(), lawyerMapper.toDtoList(lawFirm.getLawyers()));
    }

    @Override
    public LawFirm toEntity(LawFirmDto lawFirmDto) {
        if (lawFirmDto == null) {
            return null;
        }
        return new LawFirm(lawFirmDto.getId(), lawFirmDto.getName(), lawFirmDto.getCompanyStartDay(), lawyerMapper.toEntityList(lawFirmDto.getLawyers()));
    }

    @Override
    public List<LawFirmDto> toDtoList(List<LawFirm> lawFirmList) {
        if (lawFirmList == null) {
            return Collections.emptyList();
        }
        return lawFirmList.stream().map(this::toDto).toList();
    }

    @Override
    public List<LawFirm> toEntityList(List<LawFirmDto> lawFirmDtoList) {

        if (lawFirmDtoList == null) {
            return Collections.emptyList();
        }
        return lawFirmDtoList.stream().map(this::toEntity).toList();
    }
}
