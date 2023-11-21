package com.mogilan.servlet.mapper;

import com.mogilan.model.*;
import com.mogilan.servlet.dto.*;

import java.util.List;

public interface ClientMapper {

    ClientDto toDto(Client entity);

    Client toEntity(ClientDto dto);

    List<ClientDto> toDtoList(List<Client> entity);

    List<Client> toEntityList(List<ClientDto> dto);

}
