package com.mogilan.util;

import com.mogilan.db.impl.ConnectionPoolImpl;
import com.mogilan.exception.PathVariableException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class ServletsUtilTest {

    @ParameterizedTest
    @MethodSource("getPathVariableSuccessArguments")
    void getPathVariableSuccess(String pathInfo, Long expectedResult) {
        var actualResult = ServletsUtil.getPathVariable(pathInfo);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "/","1/", "/1/", "//1", "abc/", "1/abc", "/abc/1", "/abc/1/"})
    void getPathVariableShouldThrowExceptionIfPathInfoIsNotCorrect(String pathInfo) {
        var pathVariableException = assertThrows(PathVariableException.class, () -> ServletsUtil.getPathVariable(pathInfo));
        assertThat(pathVariableException.getMessage()).isEqualTo(ServletsUtil.PATH_VARIABLE_IS_NOT_CORRECT_MESSAGE);
    }


    static Stream<Arguments> getPathVariableSuccessArguments(){
        return Stream.of(
                Arguments.of("/10", 10L),
                Arguments.of("/20", 20L),
                Arguments.of("/100", 100L),
                Arguments.of("/1000", 1000L),
                Arguments.of("/10000", 10000L)
        );
    }

    static Stream<Arguments> getPathVariableShouldThrowExceptionIfUriIsNotCorrectArguments(){
        return Stream.of(
                Arguments.of("/10", 10L),
                Arguments.of("/20", 20L),
                Arguments.of("/100", 100L),
                Arguments.of("/1000", 1000L),
                Arguments.of("/10000", 10000L)
        );
    }
}