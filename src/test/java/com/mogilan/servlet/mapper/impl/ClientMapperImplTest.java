package com.mogilan.servlet.mapper.impl;

import com.mogilan.model.*;
import com.mogilan.servlet.dto.*;
import com.mogilan.servlet.mapper.ClientMapper;
import com.mogilan.servlet.mapper.ContactDetailsMapper;
import com.mogilan.servlet.mapper.SimpleLawyerMapper;
import com.mogilan.servlet.mapper.SimpleTaskMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ClientMapperImplTest {

    ContactDetailsMapper contactDetailsMapper = new ContactDetailsMapperImpl();
    SimpleTaskMapper simpleTaskMapper = SimpleTaskMapper.INSTANCE;
    SimpleLawyerMapper simpleLawyerMapper = new SimpleLawyerMapperImpl();

    ClientMapper clientMapper = new ClientMapperImpl(contactDetailsMapper, simpleTaskMapper, simpleLawyerMapper);

    @ParameterizedTest
    @MethodSource("toDtoSuccessArguments")
    void toDtoSuccess(Client given, ClientDto expectedResult) {
        var actualResult = clientMapper.toDto(given);
        assertThat(actualResult).isEqualTo(expectedResult);
        if(actualResult != null && given != null){
            if(given.getTasks() == null) {
                assertThat(actualResult.getTasks()).isEmpty();
            } else {
                assertThat(actualResult.getTasks().size()).isEqualTo(given.getTasks().size());
            }
        }
    }

    @ParameterizedTest
    @MethodSource("toEntitySuccessArguments")
    void toEntitySuccess(ClientDto given, Client expectedResult) {
        var actualResult = clientMapper.toEntity(given);
        assertThat(actualResult).isEqualTo(expectedResult);
        if(actualResult != null && given != null){
            if(given.getTasks() == null) {
                assertThat(actualResult.getTasks()).isEmpty();
            } else {
                assertThat(actualResult.getTasks().size()).isEqualTo(given.getTasks().size());
            }
        }
    }

    @ParameterizedTest
    @MethodSource("toDtoListSuccessArguments")
    void toDtoList(List<Client> given, List<ClientDto> expectedResult) {
        var actualResult = clientMapper.toDtoList(given);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @MethodSource("toEntityListSuccessArguments")
    void toEntityList(List<ClientDto> given, List<Client> expectedResult) {
        var actualResult = clientMapper.toEntityList(given);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    static Stream<Arguments> toDtoSuccessArguments() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(
                        new Client(
                                1L, "AAA", "BBB", List.of(
                                new Task(1L, "1", "1", TaskPriority.HIGH, TaskStatus.RECEIVED, null, null, null, 1.0, null,
                                        List.of(new Lawyer(1L,"1", "1", JobTitle.ASSOCIATE, 100.0, new LawFirm(), new ContactDetails()))), new Task(), new Task(), new Task())),
                        new ClientDto(
                                1L, "AAA", "BBB", List.of(
                                new SimpleTaskDto(1L, "1", "1", TaskPriority.HIGH, TaskStatus.RECEIVED, null, null, null, 1.0,
                                        List.of(new LawyerDto(1L,"1", "1", JobTitle.ASSOCIATE, 100.0, new LawFirmDto(), new ContactDetailsDto(), null))), new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto()))
                )
        );
    }

    static Stream<Arguments> toEntitySuccessArguments() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(
                        new ClientDto(
                                1L, "AAA", "BBB", List.of(
                                new SimpleTaskDto(1L, "1", "1", TaskPriority.HIGH, TaskStatus.RECEIVED, null, null, null, 1.0,
                                        List.of(new LawyerDto(1L,"1", "1", JobTitle.ASSOCIATE, 100.0, new LawFirmDto(), new ContactDetailsDto(), null))), new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto())),
                        new Client(
                                1L, "AAA", "BBB", List.of(
                                new Task(
                                        1L, "1", "1", TaskPriority.HIGH, TaskStatus.RECEIVED, null, null, null, 1.0, null,
                                        List.of(new Lawyer(1L,"1", "1", JobTitle.ASSOCIATE, 100.0, new LawFirm(), new ContactDetails()))
                                ), new Task(), new Task(), new Task()))
                )
        );
    }

    static Stream<Arguments> toDtoListSuccessArguments() {
        return Stream.of(
                Arguments.of(null, Collections.emptyList()),
                Arguments.of(
                        List.of(new Client(
                                        1L, "AAA", "BBB", List.of(
                                        new Task(), new Task(), new Task(), new Task())),
                                new Client(
                                        2L, "DDD", "EEE", List.of(
                                        new Task(), new Task(), new Task(), new Task()))),
                        List.of(new ClientDto(
                                        1L, "AAA", "BBB", List.of(
                                        new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto())),
                                new ClientDto(
                                        2L, "DDD", "EEE", List.of(
                                        new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto())))
                )
        );
    }

    static Stream<Arguments> toEntityListSuccessArguments() {
        return Stream.of(
                Arguments.of(null, Collections.emptyList()),
                Arguments.of(
                        List.of(new ClientDto(
                                        1L, "AAA", "BBB", List.of(
                                        new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto())),
                                new ClientDto(
                                        2L, "DDD", "EEE", List.of(
                                        new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto()))),
                        List.of(new Client(
                                1L, "AAA", "BBB", List.of(
                                new Task(), new Task(), new Task(), new Task())),
                                new Client(
                                        2L, "DDD", "EEE", List.of(
                                        new Task(), new Task(), new Task(), new Task())))
                )
        );
    }
}