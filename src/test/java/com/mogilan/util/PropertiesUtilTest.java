package com.mogilan.util;

import com.mogilan.db.impl.ConnectionPoolImpl;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class PropertiesUtilTest {

    @ParameterizedTest
    @MethodSource("getSuccessArguments")
    void getSuccess(String key, String expectedResult) {
        var actualResult = PropertiesUtil.get(key);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    static Stream<Arguments> getSuccessArguments(){
        return Stream.of(
                Arguments.of(ConnectionPoolImpl.URL_KEY, "jdbc:postgresql://localhost:5432/law_firm"),
                Arguments.of(ConnectionPoolImpl.USER_KEY, "postgres"),
                Arguments.of(ConnectionPoolImpl.PASSWORD_KEY, "postgres"),
                Arguments.of(ConnectionPoolImpl.DRIVER_KEY, "org.postgresql.Driver"),
                Arguments.of(ConnectionPoolImpl.POOL_SIZE_KEY, "5")
        );
    }
}