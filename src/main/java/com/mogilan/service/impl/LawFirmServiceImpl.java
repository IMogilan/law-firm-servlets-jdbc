package com.mogilan.service.impl;

import com.mogilan.exception.EntityNotFoundException;
import com.mogilan.repository.LawFirmDao;
import com.mogilan.repository.impl.LawFirmDaoImpl;
import com.mogilan.service.LawFirmService;
import com.mogilan.service.LawyerService;
import com.mogilan.servlet.dto.LawFirmDto;
import com.mogilan.servlet.dto.LawyerDto;
import com.mogilan.servlet.mapper.LawFirmMapper;
import com.mogilan.servlet.mapper.impl.LawFirmMapperImpl;

import java.util.*;
import java.util.stream.Collectors;

public class LawFirmServiceImpl implements LawFirmService {

    private static final LawFirmServiceImpl INSTANCE = new LawFirmServiceImpl();
    private final LawFirmDao lawFirmDao = LawFirmDaoImpl.getInstance();
    private final LawFirmMapper lawFirmMapper = LawFirmMapperImpl.getInstance();
    private final static LawyerService lawyerService = LawyerServiceImpl.getInstance();

    private LawFirmServiceImpl() {
    }

    @Override
    public LawFirmDto create(LawFirmDto lawFirmDto) {
        Objects.requireNonNull(lawFirmDto);

        var newLawFirm = lawFirmMapper.toEntity(lawFirmDto);
        var savedLawFirm = lawFirmDao.save(newLawFirm);
        var createdLawFirmDto = lawFirmMapper.toDto(savedLawFirm);

        createNewLawyers(lawFirmDto.getLawyers(), createdLawFirmDto);
        createdLawFirmDto.setLawyers(lawyerService.readAllByLawFirmId(createdLawFirmDto.getId()));
        return createdLawFirmDto;
    }

    @Override
    public List<LawFirmDto> readAll() {
        return lawFirmMapper.toDtoList(lawFirmDao.findAll());
    }

    @Override
    public LawFirmDto readById(Long id) {
        Objects.requireNonNull(id);

        var lawFirm = lawFirmDao.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Law firm with id = " + id + " not found"));
        return lawFirmMapper.toDto(lawFirm);
    }

    @Override
    public void update(Long id, LawFirmDto lawFirmDto) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(lawFirmDto);
        if (lawFirmDao.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Law firm with id = " + id + " not found");
        }

        lawFirmDto.setId(id);
        var lawFirm = lawFirmMapper.toEntity(lawFirmDto);
        lawFirmDao.update(lawFirm);

        updateLawyerListOfThisLawFirm(id, lawFirmDto);
    }

    @Override
    public void deleteById(Long id) {
        Objects.requireNonNull(id);
        if (lawFirmDao.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Law firm with id = " + id + " not found");
        }
        lawFirmDao.delete(id);
    }

    @Override
    public boolean existsById(Long id) {
        Objects.requireNonNull(id);

        return lawFirmDao.findById(id).isEmpty();
    }

    public static LawFirmServiceImpl getInstance() {
        return INSTANCE;
    }

    private void createNewLawyers(List<LawyerDto> newLawyers, LawFirmDto lawFirmDto) {
        if (newLawyers != null) {
            newLawyers.stream().filter(lawyerDto -> lawyerDto.getId() == null).forEach(lawyerDto -> {
                lawyerDto.setLawFirm(lawFirmDto);
                lawyerService.create(lawyerDto);
            });
        }
    }

    private void updateLawyerListOfThisLawFirm(Long id, LawFirmDto lawFirmDto) {
        var newLawyersList = lawFirmDto.getLawyers();
        var prevLawyersList = lawyerService.readAllByLawFirmId(id);
        createAllIfPrevLawyerListEmpty(lawFirmDto, newLawyersList, prevLawyersList);
        deleteAllIfNewLawyerListEmpty(newLawyersList, prevLawyersList);
        updateLawyerList(lawFirmDto, newLawyersList, prevLawyersList);
    }

    private void createAllIfPrevLawyerListEmpty(LawFirmDto lawFirmDto, List<LawyerDto> newLawyersList, List<LawyerDto> prevLawyersList) {
        if ((newLawyersList != null && !newLawyersList.isEmpty()) && (prevLawyersList == null || prevLawyersList.isEmpty())) {
            createNewLawyers(newLawyersList, lawFirmDto);
        }
    }

    private void deleteAllIfNewLawyerListEmpty(List<LawyerDto> newLawyersList, List<LawyerDto> prevLawyersList) {
        if ((newLawyersList == null || newLawyersList.isEmpty()) && (prevLawyersList != null && !prevLawyersList.isEmpty())) {
            prevLawyersList.forEach(lawyerDto -> lawyerService.deleteById(lawyerDto.getId()));
        }
    }

    private void updateLawyerList(LawFirmDto lawFirmDto, List<LawyerDto> newLawyersList, List<LawyerDto> prevLawyersList) {
        if ((newLawyersList != null && !newLawyersList.isEmpty()) && (prevLawyersList != null && !prevLawyersList.isEmpty())) {
            var newLawyersWithoutId = newLawyersList.stream().filter(lawyerDto -> lawyerDto.getId() == null).toList();
            createNewLawyers(newLawyersWithoutId, lawFirmDto);

            newLawyersList.removeAll(newLawyersWithoutId);
            var lawyersWithId = newLawyersList.stream().collect(Collectors.toMap(LawyerDto::getId, lawyerDto -> lawyerDto));

            var newLawyerListIds = newLawyersList.stream().map(LawyerDto::getId).toList();
            var prevLawyersListIds = prevLawyersList.stream().map(LawyerDto::getId).toList();

            var retainedLayersIds = new HashSet<>(newLawyerListIds);
            retainedLayersIds.retainAll(prevLawyersListIds);
            retainedLayersIds.forEach(lawyerId -> lawyerService.update(lawyerId, lawyersWithId.get(lawyerId)));

            var removedLayers = new HashSet<>(prevLawyersListIds);
            removedLayers.removeAll(retainedLayersIds);
            removedLayers.forEach(lawyerService::deleteById);

            var addedLawyers = new HashSet<>(newLawyerListIds);
            addedLawyers.removeAll(retainedLayersIds);
            addedLawyers.forEach(lawyerId -> {
                var addedLawyer = lawyersWithId.get(lawyerId);
                addedLawyer.setLawFirm(lawFirmDto);
                lawyerService.update(lawyerId, addedLawyer);
            });
        }
    }
}
