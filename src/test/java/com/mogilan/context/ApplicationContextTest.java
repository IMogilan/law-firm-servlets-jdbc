package com.mogilan.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogilan.db.impl.ConnectionPoolImpl;
import com.mogilan.exception.handler.impl.ServletExceptionHandlerImpl;
import com.mogilan.repository.impl.LawyerDaoImpl;
import com.mogilan.service.impl.TaskServiceImpl;
import com.mogilan.util.PropertiesUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class ApplicationContextTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @Test
    void initAppContextSuccess() throws NoSuchFieldException, IllegalAccessException {
        var poolSize = Integer.parseInt(PropertiesUtil.get("db.pool.size"));
        var connectionPool = new ConnectionPoolImpl(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword(),
                poolSize);
        var applicationContext = new ApplicationContext(connectionPool);
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
        var poolSize = Integer.parseInt(PropertiesUtil.get("db.pool.size"));
        var connectionPool = new ConnectionPoolImpl(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword(),
                poolSize);
        var applicationContext = new ApplicationContext(connectionPool);
        var actualResult = applicationContext.getDependency(key);
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isInstanceOf(expectedClass);
    }

    static Stream<Arguments> getDependencySuccessArguments(){
        return Stream.of(
                Arguments.of("objectMapper", ObjectMapper.class),
                Arguments.of("servletExceptionHandler", ServletExceptionHandlerImpl.class),
                Arguments.of("lawyerDao", LawyerDaoImpl.class),
                Arguments.of("taskService", TaskServiceImpl.class)
        );
    }
}