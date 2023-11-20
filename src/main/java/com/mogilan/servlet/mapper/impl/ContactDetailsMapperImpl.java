package com.mogilan.servlet.mapper.impl;

import com.mogilan.model.ContactDetails;
import com.mogilan.servlet.dto.ContactDetailsDto;
import com.mogilan.servlet.mapper.ContactDetailsMapper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ContactDetailsMapperImpl implements ContactDetailsMapper {

    private static final ContactDetailsMapperImpl INSTANCE = new ContactDetailsMapperImpl();

    private ContactDetailsMapperImpl() {
    }

    @Override
    public ContactDetailsDto toDto(ContactDetails contactDetails) {
        if (contactDetails == null) {
            return null;
        }
        return new ContactDetailsDto(contactDetails.getId(), contactDetails.getAddress(), contactDetails.getTelNumber(), contactDetails.getMobNumber(), contactDetails.getFaxNumber(), contactDetails.getEmail());
    }

    @Override
    public ContactDetails toEntity(ContactDetailsDto contactDetailsDto) {
        if (contactDetailsDto == null) {
            return null;
        }
        return new ContactDetails(contactDetailsDto.getId(), contactDetailsDto.getAddress(), contactDetailsDto.getTelNumber(), contactDetailsDto.getMobNumber(), contactDetailsDto.getFaxNumber(), contactDetailsDto.getEmail());
    }

    @Override
    public List<ContactDetailsDto> toDtoList(List<ContactDetails> contactDetailsList) {
        if (contactDetailsList == null) {
            return Collections.emptyList();
        }
        return contactDetailsList.stream().map(this::toDto).toList();
    }

    @Override
    public List<ContactDetails> toEntityList(List<ContactDetailsDto> contactDetailsDtoList) {
        if (contactDetailsDtoList == null) {
            return Collections.emptyList();
        }
        return contactDetailsDtoList.stream().map(this::toEntity).toList();
    }

    public static ContactDetailsMapperImpl getInstance(){
        return INSTANCE;
    }
}
