package com.mogilan.service.impl;

import com.mogilan.exception.EntityNotFoundException;
import com.mogilan.repository.ContactDetailsDao;
import com.mogilan.service.ContactDetailsService;
import com.mogilan.servlet.dto.ContactDetailsDto;
import com.mogilan.servlet.mapper.ContactDetailsMapper;

import java.util.List;
import java.util.Objects;

public class ContactDetailsServiceImpl implements ContactDetailsService {
    private final ContactDetailsDao contactDetailsDao;
    private final ContactDetailsMapper contactDetailsMapper;

    public ContactDetailsServiceImpl(ContactDetailsDao contactDetailsDao, ContactDetailsMapper contactDetailsMapper) {
        this.contactDetailsDao = contactDetailsDao;
        this.contactDetailsMapper = contactDetailsMapper;
    }

    @Override
    public ContactDetailsDto create(ContactDetailsDto contactDetailsDto) {
        Objects.requireNonNull(contactDetailsDto);

        var contactDetails = contactDetailsMapper.toEntity(contactDetailsDto);
        var savedContactDetails = contactDetailsDao.save(contactDetails);
        return contactDetailsMapper.toDto(savedContactDetails);
    }

    @Override
    public List<ContactDetailsDto> readAll() {
        return contactDetailsMapper.toDtoList(contactDetailsDao.findAll());
    }

    @Override
    public ContactDetailsDto readById(Long id) {
        Objects.requireNonNull(id);

        var contactDetails = contactDetailsDao.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Contacts details with id = " + id + " not found"));
        return contactDetailsMapper.toDto(contactDetails);
    }

    @Override
    public void update(Long id, ContactDetailsDto contactDetailsDto) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(contactDetailsDto);
        if (contactDetailsDao.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Contacts details with id = " + id + " not found");
        }

        var contactDetails = contactDetailsMapper.toEntity(contactDetailsDto);
        contactDetails.setId(id);
        contactDetailsDao.update(contactDetails);
    }

    @Override
    public void deleteById(Long id) {
        Objects.requireNonNull(id);
        if (contactDetailsDao.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Contacts details with id = " + id + " not found");
        }
        contactDetailsDao.delete(id);
    }
}
