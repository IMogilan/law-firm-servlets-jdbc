package com.mogilan.servlet.mapper.impl;

import com.mogilan.model.ContactDetails;
import com.mogilan.model.LawFirm;
import com.mogilan.model.Lawyer;
import com.mogilan.model.Task;
import com.mogilan.servlet.dto.*;
import com.mogilan.servlet.mapper.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class LawFirmMapperImplTest {
    ContactDetailsMapper contactDetailsMapper = new ContactDetailsMapperImpl();
    SimpleTaskMapper simpleTaskMapper = SimpleTaskMapper.INSTANCE;
    SimpleLawyerMapper simpleLawyerMapper = new SimpleLawyerMapperImpl();
    LawyerMapper lawyerMapper = new LawyerMapperImpl(contactDetailsMapper, simpleTaskMapper, simpleLawyerMapper);

    LawFirmMapper lawFirmMapper = new LawFirmMapperImpl(lawyerMapper, simpleLawyerMapper);

    @ParameterizedTest
    @MethodSource("toDtoSuccessArguments")
    void toDtoSuccess(LawFirm given, LawFirmDto expectedResult) {
        var actualResult = lawFirmMapper.toDto(given);
        assertThat(actualResult).isEqualTo(expectedResult);
        if (actualResult != null && given != null) {
            if (given.getLawyers() == null) {
                assertThat(actualResult.getLawyers()).isEmpty();
            } else {
                assertThat(actualResult.getLawyers().size()).isEqualTo(given.getLawyers().size());
            }
        }
    }

    @ParameterizedTest
    @MethodSource("toEntitySuccessArguments")
    void toEntitySuccess(LawFirmDto given, LawFirm expectedResult) {
        var actualResult = lawFirmMapper.toEntity(given);
        assertThat(actualResult).isEqualTo(expectedResult);
        if (actualResult != null && given != null) {
            if (given.getLawyers() == null) {
                assertThat(actualResult.getLawyers()).isEmpty();
            } else {
                assertThat(actualResult.getLawyers().size()).isEqualTo(given.getLawyers().size());
            }
        }
    }

    @ParameterizedTest
    @MethodSource("toDtoListSuccessArguments")
    void toDtoList(List<LawFirm> given, List<LawFirmDto> expectedResult) {
        var actualResult = lawFirmMapper.toDtoList(given);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @MethodSource("toEntityListSuccessArguments")
    void toEntityList(List<LawFirmDto> given, List<LawFirm> expectedResult) {
        var actualResult = lawFirmMapper.toEntityList(given);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    static Stream<Arguments> toDtoSuccessArguments() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(
                        new LawFirm(1L, "AAA", LocalDate.of(2000, 1, 1),
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
                        ),
                        new LawFirmDto(1L, "AAA", LocalDate.of(2000, 1, 1),
                                List.of(
                                        new SimpleLawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0,
                                                new ContactDetailsDto(1L, "1", "777", "777", "777", "test@mail.com"),
                                                List.of(new TaskDto(), new TaskDto(), new TaskDto())),
                                        new SimpleLawyerDto(2L, "2", "1", JobTitle.PARTNER, 200.0,
                                                null,
                                                null)
                                )
                        )
                )
        );
    }

    static Stream<Arguments> toEntitySuccessArguments() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(
                        new LawFirmDto(1L, "AAA", LocalDate.of(2000, 1, 1),
                                List.of(
                                        new SimpleLawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0,
                                                new ContactDetailsDto(1L, "1", "777", "777", "777", "test@mail.com"),
                                                List.of(new TaskDto(), new TaskDto(), new TaskDto())),
                                        new SimpleLawyerDto(2L, "2", "1", JobTitle.PARTNER, 200.0,
                                                null,
                                                null)
                                )
                        ),
                        new LawFirm(1L, "AAA", LocalDate.of(2000, 1, 1),
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
                )
        );
    }

    static Stream<Arguments> toDtoListSuccessArguments() {
        return Stream.of(
                Arguments.of(null, Collections.emptyList()),
                Arguments.of(
                        List.of(new LawFirm(1L, "AAA", LocalDate.of(2000, 1, 1),
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
                        ),
                        List.of(new LawFirmDto(1L, "AAA", LocalDate.of(2000, 1, 1),
                                        List.of(
                                                new SimpleLawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0,
                                                        new ContactDetailsDto(1L, "1", "777", "777", "777", "test@mail.com"),
                                                        List.of(new TaskDto(), new TaskDto(), new TaskDto())),
                                                new SimpleLawyerDto(2L, "2", "1", JobTitle.PARTNER, 200.0,
                                                        null,
                                                        null)
                                        )
                                )
                        )
                )
        );
    }

    static Stream<Arguments> toEntityListSuccessArguments() {
        return Stream.of(
                Arguments.of(null, Collections.emptyList()),
                Arguments.of(
                        List.of(new LawFirmDto(1L, "AAA", LocalDate.of(2000, 1, 1),
                                        List.of(
                                                new SimpleLawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0,
                                                        new ContactDetailsDto(1L, "1", "777", "777", "777", "test@mail.com"),
                                                        List.of(new TaskDto(), new TaskDto(), new TaskDto())),
                                                new SimpleLawyerDto(2L, "2", "1", JobTitle.PARTNER, 200.0,
                                                        null,
                                                        null)
                                        )
                                )
                        ),
                        List.of(new LawFirm(1L, "AAA", LocalDate.of(2000, 1, 1),
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
                        )
                )
        );
    }

}