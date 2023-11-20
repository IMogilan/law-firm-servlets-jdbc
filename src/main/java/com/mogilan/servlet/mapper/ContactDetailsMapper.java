package com.mogilan.servlet.mapper;

import com.mogilan.model.Client;
import com.mogilan.model.ContactDetails;
import com.mogilan.servlet.dto.ClientDto;
import com.mogilan.servlet.dto.ContactDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

//@Mapper

public interface ContactDetailsMapper {

//    ContactDetailsMapper INSTANCE = Mappers.getMapper(ContactDetailsMapper.class);

    ContactDetailsDto toDto(ContactDetails entity);

    ContactDetails toEntity(ContactDetailsDto dto);

    List<ContactDetailsDto> toDtoList(List<ContactDetails> entityList);

    List<ContactDetails> toEntityList(List<ContactDetailsDto> dtoList);
}
