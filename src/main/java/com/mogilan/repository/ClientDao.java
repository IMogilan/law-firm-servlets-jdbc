package com.mogilan.repository;

import com.mogilan.model.Client;

import java.util.Optional;

public interface ClientDao extends CrudDao<Client, Long> {
    Optional<Client> findByName(String name);
}
