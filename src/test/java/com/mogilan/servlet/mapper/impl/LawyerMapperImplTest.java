package com.mogilan.servlet.mapper.impl;

import com.mogilan.model.*;
import com.mogilan.servlet.dto.*;
import com.mogilan.servlet.mapper.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class LawyerMapperImplTest {
    ContactDetailsMapper contactDetailsMapper = new ContactDetailsMapperImpl();
    SimpleTaskMapper simpleTaskMapper = SimpleTaskMapper.INSTANCE;
    SimpleLawyerMapper simpleLawyerMapper = new SimpleLawyerMapperImpl();

    LawyerMapper lawyerMapper = new LawyerMapperImpl(contactDetailsMapper, simpleTaskMapper, simpleLawyerMapper);

    @ParameterizedTest
    @MethodSource("toDtoSuccessArguments")
    void toDtoSuccess(Lawyer given, LawyerDto expectedResult) {
        var actualResult = lawyerMapper.toDto(given);
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
    void toEntitySuccess(LawyerDto given, Lawyer expectedResult) {
        var actualResult = lawyerMapper.toEntity(given);
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
    void toDtoList(List<Lawyer> given, List<LawyerDto> expectedResult) {
        var actualResult = lawyerMapper.toDtoList(given);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @MethodSource("toEntityListSuccessArguments")
    void toEntityList(List<LawyerDto> given, List<Lawyer> expectedResult) {
        var actualResult = lawyerMapper.toEntityList(given);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    static Stream<Arguments> toDtoSuccessArguments() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(
                        new Lawyer(1L, "1", "1", JobTitle.ASSOCIATE, 100.0,
                                new LawFirm(1L, "AAA", null, null),
                                new ContactDetails(1L, "1", "777", "777", "777", "test@mail.com"),
                                List.of(new Task(), new Task(), new Task())),
                        new LawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0,
                                new LawFirmDto(1L, "AAA", null, null),
                                new ContactDetailsDto(1L, "1", "777", "777", "777", "test@mail.com"),
                                List.of(new TaskDto(), new TaskDto(), new TaskDto()))
                ),
                Arguments.of(
                        new Lawyer(2L, "2", "1", JobTitle.PARTNER, 200.0,
                                new LawFirm(1L, "BBB", null, null),
                                null,
                                null),
                        new LawyerDto(2L, "2", "1", JobTitle.PARTNER, 200.0,
                                new LawFirmDto(1L, "BBB", null, null),
                                null,
                                null)
                )
        );
    }

    static Stream<Arguments> toEntitySuccessArguments() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(
                        new LawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0,
                                new LawFirmDto(1L, "AAA", null, null),
                                new ContactDetailsDto(1L, "1", "777", "777", "777", "test@mail.com"),
                                List.of(new TaskDto(), new TaskDto(), new TaskDto())),
                        new Lawyer(1L, "1", "1", JobTitle.ASSOCIATE, 100.0,
                                new LawFirm(1L, "AAA", null, null),
                                new ContactDetails(1L, "1", "777", "777", "777", "test@mail.com"),
                                List.of(new Task(), new Task(), new Task()))
                ),
                Arguments.of(
                        new LawyerDto(2L, "2", "1", JobTitle.PARTNER, 200.0,
                                new LawFirmDto(1L, "BBB", null, null),
                                null,
                                null),
                        new Lawyer(2L, "2", "1", JobTitle.PARTNER, 200.0,
                                new LawFirm(1L, "BBB", null, null),
                                null,
                                null)
                )
        );
    }

    static Stream<Arguments> toDtoListSuccessArguments() {
        return Stream.of(
                Arguments.of(null, Collections.emptyList()),
                Arguments.of(
                        List.of(
                                new Lawyer(1L, "1", "1", JobTitle.ASSOCIATE, 100.0,
                                        new LawFirm(1L, "AAA", null, null),
                                        new ContactDetails(1L, "1", "777", "777", "777", "test@mail.com"),
                                        List.of(new Task(), new Task(), new Task())),
                                new Lawyer(2L, "2", "1", JobTitle.PARTNER, 200.0,
                                        new LawFirm(1L, "BBB", null, null),
                                        null,
                                        null)
                        ),
                        List.of(new LawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0,
                                        new LawFirmDto(1L, "AAA", null, null),
                                        new ContactDetailsDto(1L, "1", "777", "777", "777", "test@mail.com"),
                                        List.of(new TaskDto(), new TaskDto(), new TaskDto())),
                                new LawyerDto(2L, "2", "1", JobTitle.PARTNER, 200.0,
                                        new LawFirmDto(1L, "BBB", null, null),
                                        null,
                                        null)
                        )
                )
        );
    }

    static Stream<Arguments> toEntityListSuccessArguments() {
        return Stream.of(
                Arguments.of(null, Collections.emptyList()),
                Arguments.of(
                        List.of(new LawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0,
                                        new LawFirmDto(1L, "AAA", null, null),
                                        new ContactDetailsDto(1L, "1", "777", "777", "777", "test@mail.com"),
                                        List.of(new TaskDto(), new TaskDto(), new TaskDto())),
                                new LawyerDto(2L, "2", "1", JobTitle.PARTNER, 200.0,
                                        new LawFirmDto(1L, "BBB", null, null),
                                        null,
                                        null)
                        ),
                        List.of(
                                new Lawyer(1L, "1", "1", JobTitle.ASSOCIATE, 100.0,
                                        new LawFirm(1L, "AAA", null, null),
                                        new ContactDetails(1L, "1", "777", "777", "777", "test@mail.com"),
                                        List.of(new Task(), new Task(), new Task())),
                                new Lawyer(2L, "2", "1", JobTitle.PARTNER, 200.0,
                                        new LawFirm(1L, "BBB", null, null),
                                        null,
                                        null)
                        )
                )
        );
    }
}