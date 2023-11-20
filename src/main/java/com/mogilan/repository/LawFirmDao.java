package com.mogilan.repository;

import com.mogilan.model.LawFirm;

import java.util.Optional;

public interface LawFirmDao extends CrudDao<LawFirm, Long> {
    Optional<LawFirm> findByName(String name);
}
