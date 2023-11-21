package com.mogilan.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogilan.db.impl.ConnectionPoolImpl;
import com.mogilan.exception.handler.impl.ServletExceptionHandlerImpl;
import com.mogilan.repository.impl.LawyerDaoImpl;
import com.mogilan.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class ApplicationContextTest {

    @Test
    void initAppContextSuccess() throws NoSuchFieldException, IllegalAccessException {
        var applicationContext = new ApplicationContext();
        assertThat(applicationContext).isNotNull();
        HashMap beans;
        var beansField = applicationContext.getClass().getDeclaredField("beans");
        try {
            beansField.setAccessible(true);
            beans = (HashMap) beansField.get(applicationContext);
            assertThat(beans).isNotNull();
            assertThat(beans).isNotEmpty();
        } finally {
            beansField.setAccessible(false);
        }
    }

    @ParameterizedTest
    @MethodSource("getDependencySuccessArguments")
    void getDependencySuccess(String key, Class expectedClass) {
        var applicationContext = new ApplicationContext();
        var actualResult = applicationContext.getDependency(key);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(expectedClass);
    }

    static Stream<Arguments> getDependencySuccessArguments(){
        return Stream.of(
                Arguments.of("objectMapper", ObjectMapper.class),
                Arguments.of("servletExceptionHandler", ServletExceptionHandlerImpl.class),
                Arguments.of("connectionPool", ConnectionPoolImpl.class),
                Arguments.of("lawyerDao", LawyerDaoImpl.class),
                Arguments.of("taskService", TaskServiceImpl.class)
        );
    }
}