package com.mogilan.servlet.mapper.impl;

import com.mogilan.model.ContactDetails;
import com.mogilan.servlet.dto.ContactDetailsDto;
import com.mogilan.servlet.mapper.ContactDetailsMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class ContactDetailsMapperImplTest {

    ContactDetailsMapper contactDetailsMapper;

    @BeforeEach
    void setUp() {
        contactDetailsMapper = new ContactDetailsMapperImpl();
    }

    @ParameterizedTest
    @MethodSource("toDtoSuccessArguments")
    void toDtoSuccess(ContactDetails given, ContactDetailsDto expectedResult) {
        var actualResult = contactDetailsMapper.toDto(given);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @MethodSource("toEntitySuccessArguments")
    void toEntitySuccess(ContactDetailsDto given, ContactDetails expectedResult) {
        var actualResult = contactDetailsMapper.toEntity(given);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @MethodSource("toDtoListSuccessArguments")
    void toDtoListSuccess(List<ContactDetails> given, List<ContactDetailsDto> expectedResult) {
        var actualResult = contactDetailsMapper.toDtoList(given);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @MethodSource("toEntityListSuccessArguments")
    void toEntityListSuccess(List<ContactDetailsDto> given, List<ContactDetails> expectedResult) {
        var actualResult = contactDetailsMapper.toEntityList(given);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    static Stream<Arguments> toDtoSuccessArguments() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(
                        new ContactDetails(1L, "123 Main St, Cityville", "123-456-7890", "987-654-3210", "555-123-4567", "john.doe@example.com"),
                        new ContactDetailsDto(1L, "123 Main St, Cityville", "123-456-7890", "987-654-3210", "555-123-4567", "john.doe@example.com")
                ),
                Arguments.of(
                        new ContactDetails(2L, "0123 Main St, Cityville", "0123-456-7890", "0987-654-3210", "0555-123-4567", "0john.doe@example.com"),
                        new ContactDetailsDto(2L, "0123 Main St, Cityville", "0123-456-7890", "0987-654-3210", "0555-123-4567", "0john.doe@example.com")
                )
        );
    }

    static Stream<Arguments> toEntitySuccessArguments() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(
                        new ContactDetailsDto(1L, "123 Main St, Cityville", "123-456-7890", "987-654-3210", "555-123-4567", "john.doe@example.com"),
                        new ContactDetails(1L, "123 Main St, Cityville", "123-456-7890", "987-654-3210", "555-123-4567", "john.doe@example.com")
                ),
                Arguments.of(
                        new ContactDetailsDto(2L, "0123 Main St, Cityville", "0123-456-7890", "0987-654-3210", "0555-123-4567", "0john.doe@example.com"),
                        new ContactDetails(2L, "0123 Main St, Cityville", "0123-456-7890", "0987-654-3210", "0555-123-4567", "0john.doe@example.com")
                )
        );
    }

    static Stream<Arguments> toDtoListSuccessArguments() {
        return Stream.of(
                Arguments.of(null, Collections.emptyList()),
                Arguments.of(
                        List.of(new ContactDetails(1L, "123 Main St, Cityville", "123-456-7890", "987-654-3210", "555-123-4567", "john.doe@example.com"),
                                new ContactDetails(2L, "0123 Main St, Cityville", "0123-456-7890", "0987-654-3210", "0555-123-4567", "0john.doe@example.com")),
                        List.of(new ContactDetailsDto(1L, "123 Main St, Cityville", "123-456-7890", "987-654-3210", "555-123-4567", "john.doe@example.com"),
                                new ContactDetailsDto(2L, "0123 Main St, Cityville", "0123-456-7890", "0987-654-3210", "0555-123-4567", "0john.doe@example.com"))
                        )
        );
    }

    static Stream<Arguments> toEntityListSuccessArguments() {
        return Stream.of(
                Arguments.of(null, Collections.emptyList()),
                Arguments.of(
                        List.of(new ContactDetailsDto(1L, "123 Main St, Cityville", "123-456-7890", "987-654-3210", "555-123-4567", "john.doe@example.com"),
                                new ContactDetailsDto(2L, "0123 Main St, Cityville", "0123-456-7890", "0987-654-3210", "0555-123-4567", "0john.doe@example.com")),
                        List.of(new ContactDetails(1L, "123 Main St, Cityville", "123-456-7890", "987-654-3210", "555-123-4567", "john.doe@example.com"),
                                new ContactDetails(2L, "0123 Main St, Cityville", "0123-456-7890", "0987-654-3210", "0555-123-4567", "0john.doe@example.com"))
                        )
        );
    }


}