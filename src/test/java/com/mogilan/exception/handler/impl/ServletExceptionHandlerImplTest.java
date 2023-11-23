package com.mogilan.exception.handler.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogilan.db.impl.ConnectionPoolImpl;
import com.mogilan.exception.DaoException;
import com.mogilan.exception.EntityNotFoundException;
import com.mogilan.exception.PathVariableException;
import com.mogilan.util.ServletsUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServletExceptionHandlerImplTest {

    @Mock
    ObjectMapper objectMapper;
    @Mock
    HttpServletResponse resp;
    @Mock
    PrintWriter printWriter;
    @Captor
    ArgumentCaptor<String> stringCaptor;
    @Captor
    ArgumentCaptor<Integer> integerCaptor;
    ServletExceptionHandlerImpl servletExceptionHandler;

    @BeforeEach
    void setUp() {
        servletExceptionHandler = new ServletExceptionHandlerImpl(objectMapper);
    }

    @ParameterizedTest
    @MethodSource("handleExceptionSuccessArguments")
    void handleExceptionSuccess(Exception exception, int statusCode, String statusMessage) throws IOException {
        doReturn(printWriter).when(resp).getWriter();

        servletExceptionHandler.handleException(resp, exception);

        verify(resp, times(1)).setContentType(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo("application/json");

        verify(resp, times(1)).setCharacterEncoding(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo(StandardCharsets.UTF_8.name());

        verify(resp, times(1)).setStatus(integerCaptor.capture());
        assertThat(integerCaptor.getValue()).isEqualTo(statusCode);

        verify(resp, times(1)).getWriter();

        verify(objectMapper, times(1)).writeValue(any(PrintWriter.class), stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo(statusMessage);
    }

    static Stream<Arguments> handleExceptionSuccessArguments() {
        return Stream.of(
                Arguments.of(new DaoException(new RuntimeException()), HttpServletResponse.SC_BAD_REQUEST, ServletsUtil.BAD_REQUEST_MESSAGE),
                Arguments.of(new PathVariableException("MESSAGE"), HttpServletResponse.SC_BAD_REQUEST, "MESSAGE"),
                Arguments.of(new IllegalArgumentException(), HttpServletResponse.SC_BAD_REQUEST, ServletsUtil.BAD_REQUEST_MESSAGE),
                Arguments.of(new EntityNotFoundException("MESSAGE"), HttpServletResponse.SC_NOT_FOUND, ServletsUtil.NOT_FOUND_MESSAGE),
                Arguments.of(new Exception(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ServletsUtil.INTERNAL_SERVER_ERROR)
        );
    }

}