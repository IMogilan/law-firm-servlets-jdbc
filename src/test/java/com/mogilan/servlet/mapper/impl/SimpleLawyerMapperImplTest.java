package com.mogilan.servlet.mapper.impl;

import com.mogilan.servlet.dto.*;
import com.mogilan.servlet.mapper.SimpleLawyerMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleLawyerMapperImplTest {

    SimpleLawyerMapper simpleLawyerMapper = new SimpleLawyerMapperImpl();

    @ParameterizedTest
    @MethodSource("toSimpleLawyerDtoSuccessArguments")
    void toSimpleLawyerDtoSuccess(LawyerDto given, SimpleLawyerDto expectedResult) {
        var actualResult = simpleLawyerMapper.toSimpleLawyerDto(given);
        assertThat(actualResult).isEqualTo(expectedResult);
        if (actualResult != null && actualResult.getTasks() != null) {
            assertThat(actualResult.getTasks().size()).isEqualTo(given.getTasks().size());
        }
    }

    @ParameterizedTest
    @MethodSource("toLawyerDtoSuccessArguments")
    void toLawyerDtoSuccess(SimpleLawyerDto given, LawyerDto expectedResult) {
        var actualResult = simpleLawyerMapper.toLawyerDto(given);
        assertThat(actualResult).isEqualTo(expectedResult);
        if (actualResult != null && actualResult.getTasks() != null) {
            assertThat(actualResult.getTasks().size()).isEqualTo(given.getTasks().size());
        }
    }

    @ParameterizedTest
    @MethodSource("toSimpleLawyerDtoListSuccessArguments")
    void toSimpleLawyerDtoListSuccess(List<LawyerDto> given, List<SimpleLawyerDto> expectedResult) {
        var actualResult = simpleLawyerMapper.toSimpleLawyerDtoList(given);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @MethodSource("toLawyerDtoListSuccessArguments")
    void toLawyerDtoListSuccess(List<SimpleLawyerDto> given, List<LawyerDto> expectedResult) {
        var actualResult = simpleLawyerMapper.toLawyerDtoList(given);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    static Stream<Arguments> toSimpleLawyerDtoSuccessArguments() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(
                        new LawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0, new LawFirmDto(), new ContactDetailsDto(), List.of(new TaskDto(), new TaskDto())),
                        new SimpleLawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0, new ContactDetailsDto(), List.of(new TaskDto(), new TaskDto())
                        ),
                        Arguments.of(
                                new LawyerDto(2L, "1", "1", JobTitle.PARTNER, 200.0, null, null, null),
                                new SimpleLawyerDto(2L, "1", "1", JobTitle.PARTNER, 200.0, null, null)
                        )
                )
        );
    }

    static Stream<Arguments> toLawyerDtoSuccessArguments() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(
                        new SimpleLawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0, new ContactDetailsDto(), Collections.emptyList()),
                        new LawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0, new LawFirmDto(), new ContactDetailsDto(), Collections.emptyList())
                ),
                Arguments.of(
                        new SimpleLawyerDto(2L, "1", "1", JobTitle.PARTNER, 200.0, null, null),
                        new LawyerDto(2L, "1", "1", JobTitle.PARTNER, 200.0, null, null, null)
                )
        );
    }

    static Stream<Arguments> toSimpleLawyerDtoListSuccessArguments() {
        return Stream.of(
                Arguments.of(null, Collections.emptyList()),
                Arguments.of(
                        List.of(new LawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0, new LawFirmDto(), new ContactDetailsDto(), Collections.emptyList()),
                                new LawyerDto(2L, "1", "1", JobTitle.PARTNER, 200.0, null, null, null)),
                        List.of(new SimpleLawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0, new ContactDetailsDto(), Collections.emptyList()),
                                new SimpleLawyerDto(2L, "1", "1", JobTitle.PARTNER, 200.0, null, null))
                )
        );
    }

    static Stream<Arguments> toLawyerDtoListSuccessArguments() {
        return Stream.of(
                Arguments.of(null, Collections.emptyList()),
                Arguments.of(
                        List.of(new SimpleLawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0, new ContactDetailsDto(), Collections.emptyList()),
                                new SimpleLawyerDto(2L, "1", "1", JobTitle.PARTNER, 200.0, null, null)),
                        List.of(new LawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0, new LawFirmDto(), new ContactDetailsDto(), Collections.emptyList()),
                                new LawyerDto(2L, "1", "1", JobTitle.PARTNER, 200.0, null, null, null))
                )
        );
    }
}