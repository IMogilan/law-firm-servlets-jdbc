package com.mogilan.service;

import com.mogilan.servlet.dto.ClientDto;

public interface ClientService extends CrudService<ClientDto, Long>{
    boolean existsById(Long id);
    boolean existsByName(String name);
}
