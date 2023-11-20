package com.mogilan.service;

import com.mogilan.servlet.dto.LawyerDto;

import java.util.List;

public interface LawyerService extends CrudService<LawyerDto, Long>{
    List<LawyerDto> readAllByLawFirmId(Long lawFirmId);
    List<LawyerDto> readAllByTaskId(Long taskId);
}
