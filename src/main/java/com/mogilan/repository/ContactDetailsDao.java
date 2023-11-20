package com.mogilan.repository;

import com.mogilan.model.ContactDetails;

import java.sql.Connection;
import java.util.Optional;

public interface ContactDetailsDao extends CrudDao<ContactDetails, Long> {
    Optional<ContactDetails> findById(Long id, Connection connection);
}
