package com.mogilan.service;

import com.mogilan.servlet.dto.LawFirmDto;

public interface LawFirmService extends CrudService<LawFirmDto, Long> {
    boolean existsById(Long id);
}
