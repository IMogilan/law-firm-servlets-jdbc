package com.mogilan.servlet.mapper.impl;

import com.mogilan.model.LawFirm;
import com.mogilan.model.Lawyer;
import com.mogilan.servlet.dto.LawFirmDto;
import com.mogilan.servlet.dto.LawyerDto;
import com.mogilan.servlet.dto.SimpleLawyerDto;
import com.mogilan.servlet.mapper.LawFirmMapper;
import com.mogilan.servlet.mapper.LawyerMapper;
import com.mogilan.servlet.mapper.SimpleLawyerMapper;

import java.util.Collections;
import java.util.List;

public class LawFirmMapperImpl implements LawFirmMapper {

    private final LawyerMapper lawyerMapper;
    private final SimpleLawyerMapper simpleLawyerMapper;

    public LawFirmMapperImpl(LawyerMapper lawyerMapper, SimpleLawyerMapper simpleLawyerMapper) {
        this.lawyerMapper = lawyerMapper;
        this.simpleLawyerMapper = simpleLawyerMapper;
    }

    @Override
    public LawFirmDto toDto(LawFirm lawFirm) {
        if (lawFirm == null) {
            return null;
        }
        var lawyerDtoList = lawyerMapper.toDtoList(lawFirm.getLawyers());
        var simpleLawyerDtoList = simpleLawyerMapper.toSimpleLawyerDtoList(lawyerDtoList);
        return new LawFirmDto(lawFirm.getId(), lawFirm.getName(), lawFirm.getCompanyStartDay(), simpleLawyerDtoList);
    }

    @Override
    public LawFirm toEntity(LawFirmDto lawFirmDto) {
        if (lawFirmDto == null) {
            return null;
        }
        var simpleLawyerDtoList = lawFirmDto.getLawyers();
        var lawFirm = new LawFirm(lawFirmDto.getId(), lawFirmDto.getName(), lawFirmDto.getCompanyStartDay(), null);
        if(!simpleLawyerDtoList.isEmpty()){
            var lawyerDtoList = simpleLawyerDtoList.stream()
                    .map(simpleLawyerMapper::toLawyerDto)
                    .peek(lawyerDto -> lawyerDto.setLawFirm(lawFirmDto))
                    .toList();
            var lawyerList = lawyerMapper.toEntityList(lawyerDtoList);
            lawFirm.setLawyers(lawyerList);
        }
        return lawFirm;
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
