package com.mogilan.service.impl;

import com.mogilan.exception.EntityNotFoundException;
import com.mogilan.repository.LawyerDao;
import com.mogilan.service.ContactDetailsService;
import com.mogilan.service.LawyerService;
import com.mogilan.servlet.dto.ContactDetailsDto;
import com.mogilan.servlet.dto.LawyerDto;
import com.mogilan.servlet.mapper.LawyerMapper;

import java.util.List;
import java.util.Objects;

public class LawyerServiceImpl implements LawyerService {
    private final LawyerDao lawyerDao;
    private final ContactDetailsService contactDetailsService;
    private final LawyerMapper lawyerMapper;

    public LawyerServiceImpl(LawyerDao lawyerDao, ContactDetailsService contactDetailsService, LawyerMapper lawyerMapper) {
        this.lawyerDao = lawyerDao;
        this.contactDetailsService = contactDetailsService;
        this.lawyerMapper = lawyerMapper;
    }

    @Override
    public LawyerDto create(LawyerDto lawyerDto) {
        Objects.requireNonNull(lawyerDto);

        var lawyer = lawyerMapper.toEntity(lawyerDto);
        var saveLawyer = lawyerDao.save(lawyer);
        var createdLawyerDto = lawyerMapper.toDto(saveLawyer);

        ContactDetailsDto createdContactDetailsDto = getCreatedContactDetailsDto(lawyerDto, createdLawyerDto.getId());
        createdLawyerDto.setContacts(createdContactDetailsDto);
        return createdLawyerDto;
    }

    @Override
    public List<LawyerDto> readAll() {
        return lawyerMapper.toDtoList(lawyerDao.findAll());
    }

    @Override
    public List<LawyerDto> readAllByLawFirmId(Long lawFirmId) {
        Objects.requireNonNull(lawFirmId);

        var lawyers = lawyerDao.findAllByLawFirmId(lawFirmId);
        return lawyerMapper.toDtoList(lawyers);
    }

    @Override
    public List<LawyerDto> readAllByTaskId(Long taskId) {
        Objects.requireNonNull(taskId);

        var lawyers = lawyerDao.findAllByTaskId(taskId);
        return lawyerMapper.toDtoList(lawyers);
    }

    @Override
    public LawyerDto readById(Long id) {
        Objects.requireNonNull(id);

        var lawyer = lawyerDao.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Lawyer with id = " + id + " not found"));
        return lawyerMapper.toDto(lawyer);
    }

    @Override
    public void update(Long id, LawyerDto lawyerDto) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(lawyerDto);
        if (lawyerDao.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Lawyer with id = " + id + " not found");
        }

        var lawyer = lawyerMapper.toEntity(lawyerDto);
        lawyer.setId(id);
        lawyerDao.update(lawyer);

        updateContactDetailsDto(id, lawyerDto);
    }

    @Override
    public void deleteById(Long id) {
        Objects.requireNonNull(id);
        if (lawyerDao.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Lawyer with id = " + id + " not found");
        }
        lawyerDao.delete(id);
    }

    private ContactDetailsDto getCreatedContactDetailsDto(LawyerDto lawyerDto, Long lawyerDtoId) {
        ContactDetailsDto contactDetailsDto = lawyerDto.getContacts() == null ? new ContactDetailsDto() : lawyerDto.getContacts();
        contactDetailsDto.setId(lawyerDtoId);
        return contactDetailsService.create(contactDetailsDto);
    }

    private void updateContactDetailsDto(Long id, LawyerDto lawyerDto) {
        ContactDetailsDto contactDetailsDto = lawyerDto.getContacts() == null ? new ContactDetailsDto() : lawyerDto.getContacts();
        if (contactDetailsDto.getId() == null) {
            contactDetailsDto.setId(id);
            contactDetailsService.create(contactDetailsDto);
        } else {
            contactDetailsDto.setId(id);
            contactDetailsService.update(id, contactDetailsDto);
        }
    }
}
