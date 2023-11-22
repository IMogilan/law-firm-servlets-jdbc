package com.mogilan.servlet.mapper.impl;

import com.mogilan.model.*;
import com.mogilan.servlet.dto.*;
import com.mogilan.servlet.mapper.ContactDetailsMapper;
import com.mogilan.servlet.mapper.SimpleLawyerMapper;
import com.mogilan.servlet.mapper.SimpleTaskMapper;
import com.mogilan.servlet.mapper.TaskMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class TaskMapperImplTest {

    ContactDetailsMapper contactDetailsMapper = new ContactDetailsMapperImpl();
    SimpleTaskMapper simpleTaskMapper = SimpleTaskMapper.INSTANCE;
    SimpleLawyerMapper simpleLawyerMapper = new SimpleLawyerMapperImpl();

    TaskMapper taskMapper = new TaskMapperImpl(contactDetailsMapper, simpleTaskMapper, simpleLawyerMapper);

    @ParameterizedTest
    @MethodSource("toDtoSuccessArguments")
    void toDtoSuccess(Task given, TaskDto expectedResult) {
        var actualResult = taskMapper.toDto(given);
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
    void toEntitySuccess(TaskDto given, Task expectedResult) {
        var actualResult = taskMapper.toEntity(given);
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
    void toDtoList(List<Task> given, List<TaskDto> expectedResult) {
        var actualResult = taskMapper.toDtoList(given);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @MethodSource("toEntityListSuccessArguments")
    void toEntityList(List<TaskDto> given, List<Task> expectedResult) {
        var actualResult = taskMapper.toEntityList(given);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    static Stream<Arguments> toDtoSuccessArguments() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(
                        new Task(1L, "Make draft", "ASP", TaskPriority.HIGH, TaskStatus.ACCEPTED,
                                LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 1), null, 5.0,
                                new Client(1L, "AAA", "BBB", List.of(new Task(), new Task(), new Task(), new Task())),
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
                        new TaskDto(1L, "Make draft", "ASP", TaskPriority.HIGH, TaskStatus.ACCEPTED,
                                LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 1), null, 5.0,
                                new ClientDto(1L, "AAA", "BBB", List.of(new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto())),
                                List.of(
                                        new LawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0,
                                                new LawFirmDto(1L, "AAA", null, null),
                                                new ContactDetailsDto(1L, "1", "777", "777", "777", "test@mail.com"),
                                                List.of(new TaskDto(), new TaskDto(), new TaskDto())),
                                        new LawyerDto(2L, "2", "1", JobTitle.PARTNER, 200.0,
                                                new LawFirmDto(1L, "BBB", null, null),
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
                        new TaskDto(1L, "Make draft", "ASP", TaskPriority.HIGH, TaskStatus.ACCEPTED,
                                LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 1), null, 5.0,
                                new ClientDto(1L, "AAA", "BBB", List.of(new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto())),
                                List.of(
                                        new LawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0,
                                                new LawFirmDto(1L, "AAA", null, null),
                                                new ContactDetailsDto(1L, "1", "777", "777", "777", "test@mail.com"),
                                                List.of(new TaskDto(), new TaskDto(), new TaskDto())),
                                        new LawyerDto(2L, "2", "1", JobTitle.PARTNER, 200.0,
                                                new LawFirmDto(1L, "BBB", null, null),
                                                null,
                                                null)
                                )
                        ),
                        new Task(1L, "Make draft", "ASP", TaskPriority.HIGH, TaskStatus.ACCEPTED,
                                LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 1), null, 5.0,
                                new Client(1L, "AAA", "BBB", List.of(new Task(), new Task(), new Task(), new Task())),
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
                        List.of(new Task(1L, "Make draft", "ASP", TaskPriority.HIGH, TaskStatus.ACCEPTED,
                                        LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 1), null, 5.0,
                                        new Client(1L, "AAA", "BBB", List.of(new Task(), new Task(), new Task(), new Task())),
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
                        List.of(
                                new TaskDto(1L, "Make draft", "ASP", TaskPriority.HIGH, TaskStatus.ACCEPTED,
                                        LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 1), null, 5.0,
                                        new ClientDto(1L, "AAA", "BBB", List.of(new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto())),
                                        List.of(
                                                new LawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0,
                                                        new LawFirmDto(1L, "AAA", null, null),
                                                        new ContactDetailsDto(1L, "1", "777", "777", "777", "test@mail.com"),
                                                        List.of(new TaskDto(), new TaskDto(), new TaskDto())),
                                                new LawyerDto(2L, "2", "1", JobTitle.PARTNER, 200.0,
                                                        new LawFirmDto(1L, "BBB", null, null),
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
                        List.of(
                                new TaskDto(1L, "Make draft", "ASP", TaskPriority.HIGH, TaskStatus.ACCEPTED,
                                        LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 1), null, 5.0,
                                        new ClientDto(1L, "AAA", "BBB", List.of(new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto(), new SimpleTaskDto())),
                                        List.of(
                                                new LawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0,
                                                        new LawFirmDto(1L, "AAA", null, null),
                                                        new ContactDetailsDto(1L, "1", "777", "777", "777", "test@mail.com"),
                                                        List.of(new TaskDto(), new TaskDto(), new TaskDto())),
                                                new LawyerDto(2L, "2", "1", JobTitle.PARTNER, 200.0,
                                                        new LawFirmDto(1L, "BBB", null, null),
                                                        null,
                                                        null)
                                        )
                                )
                        ),
                        List.of(
                                new Task(1L, "Make draft", "ASP", TaskPriority.HIGH, TaskStatus.ACCEPTED,
                                        LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 1), null, 5.0,
                                        new Client(1L, "AAA", "BBB", List.of(new Task(), new Task(), new Task(), new Task())),
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