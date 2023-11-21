package com.mogilan.servlet.mapper;

import com.mogilan.model.ContactDetails;
import com.mogilan.servlet.dto.ContactDetailsDto;

import java.util.List;

public interface ContactDetailsMapper {

    ContactDetailsDto toDto(ContactDetails entity);

    ContactDetails toEntity(ContactDetailsDto dto);

    List<ContactDetailsDto> toDtoList(List<ContactDetails> entityList);

    List<ContactDetails> toEntityList(List<ContactDetailsDto> dtoList);
}
