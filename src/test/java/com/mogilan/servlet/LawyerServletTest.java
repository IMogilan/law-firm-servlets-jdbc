package com.mogilan.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mogilan.context.ApplicationContext;
import com.mogilan.exception.EntityNotFoundException;
import com.mogilan.exception.handler.ServletExceptionHandler;
import com.mogilan.service.LawyerService;
import com.mogilan.service.TaskService;
import com.mogilan.servlet.dto.*;
import com.mogilan.util.ServletsUtil;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LawyerServletTest {
    @Mock
    LawyerService lawyerService;
    @Mock
    TaskService taskService;
    @Mock
    ServletExceptionHandler exceptionHandler;
    @Mock
    ObjectMapper objectMapper;
    @Mock
    ServletConfig config;
    @Mock
    ServletContext servletContext;
    @Mock
    ApplicationContext applicationContext;

    @Mock
    HttpServletRequest req;
    @Mock
    HttpServletResponse resp;
    @Mock
    PrintWriter printWriter;
    @Mock
    BufferedReader reader;
    @Captor
    ArgumentCaptor<String> stringCaptor;
    @Captor
    ArgumentCaptor<Integer> integerCaptor;
    @Captor
    ArgumentCaptor<Long> longCaptor;
    @Captor
    ArgumentCaptor<List<LawyerDto>> listArgumentCaptor;
    @Captor
    ArgumentCaptor<LawyerDto> dtoArgumentCaptor;
    LawyerServlet lawyerServlet;

    @BeforeEach
    void beforeEach(){
        lawyerServlet = new LawyerServlet(lawyerService, taskService, exceptionHandler, objectMapper);
    }

    @Test
    void init() throws ServletException {
        doReturn(servletContext).when(config).getServletContext();
        doReturn(applicationContext).when(servletContext).getAttribute(ServletsUtil.APPLICATION_CONTEXT_KEY);
        doReturn(objectMapper).when(applicationContext).getDependency(ServletsUtil.OBJECT_MAPPER_KEY);

        lawyerServlet.init(config);

        verify(config, times(1)).getServletContext();
        verify(servletContext, times(1)).getAttribute(ServletsUtil.APPLICATION_CONTEXT_KEY);
        verify(applicationContext, times(1)).getDependency(ServletsUtil.OBJECT_MAPPER_KEY);
        verify(applicationContext, times(1)).getDependency(ServletsUtil.SERVLET_EXCEPTION_HANDLER_KEY);
        verify(applicationContext, times(1)).getDependency(ServletsUtil.LAWYER_SERVICE_KEY);
        verify(applicationContext, times(1)).getDependency(ServletsUtil.TASK_SERVICE_KEY);

        verify(objectMapper, times(1)).registerModule(any());
        verify(objectMapper, times(1)).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Test
    void doGetSuccessWhenDtoListNotEmpty() throws IOException {
        String pathInfo = "/";
        doReturn(pathInfo).when(req).getPathInfo();

        var dtoList = getDtoList();
        assertThat(dtoList).isNotEmpty();
        doReturn(dtoList).when(lawyerService).readAll();

        doReturn(printWriter).when(resp).getWriter();

        lawyerServlet.doGet(req, resp);

        verify(resp, times(1)).setContentType(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo("application/json");

        verify(resp, times(1)).setCharacterEncoding(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo(StandardCharsets.UTF_8.name());

        verify(resp, times(1)).setStatus(integerCaptor.capture());
        assertThat(integerCaptor.getValue()).isEqualTo(HttpServletResponse.SC_OK);

        verify(resp, times(1)).getWriter();
        verify(objectMapper, times(1)).writeValue(any(PrintWriter.class), listArgumentCaptor.capture());
        assertThat(listArgumentCaptor.getValue()).isEqualTo(dtoList);
    }

    @Test
    void doGetWhenDtoListIsEmpty() throws IOException {
        String pathInfo = "/";
        doReturn(pathInfo).when(req).getPathInfo();

        var dtoList = Collections.emptyList();
        assertThat(dtoList).isEmpty();
        doReturn(dtoList).when(lawyerService).readAll();

        doReturn(printWriter).when(resp).getWriter();

        lawyerServlet.doGet(req, resp);

        verify(resp, times(1)).setContentType(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo("application/json");

        verify(resp, times(1)).setCharacterEncoding(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo(StandardCharsets.UTF_8.name());

        verify(resp, times(1)).setStatus(integerCaptor.capture());
        assertThat(integerCaptor.getValue()).isEqualTo(HttpServletResponse.SC_NO_CONTENT);

        verify(resp, times(1)).getWriter();
        verify(objectMapper, times(1)).writeValue(any(PrintWriter.class), listArgumentCaptor.capture());
        assertThat(listArgumentCaptor.getValue()).isEqualTo(dtoList);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 10L, 100L})
    void doGetSuccessWhenDtoPresent(Long id) throws IOException {
        String pathInfo = "/" + id;
        doReturn(pathInfo).when(req).getPathInfo();

        var dto = getDto();
        doReturn(dto).when(lawyerService).readById(id);

        doReturn(printWriter).when(resp).getWriter();

        lawyerServlet.doGet(req, resp);

        verify(resp, times(1)).setContentType(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo("application/json");

        verify(resp, times(1)).setCharacterEncoding(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo(StandardCharsets.UTF_8.name());

        verify(resp, times(1)).setStatus(integerCaptor.capture());
        assertThat(integerCaptor.getValue()).isEqualTo(HttpServletResponse.SC_OK);

        verify(resp, times(1)).getWriter();
        verify(objectMapper, times(1)).writeValue(any(PrintWriter.class), dtoArgumentCaptor.capture());
        assertThat(dtoArgumentCaptor.getValue()).isEqualTo(dto);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 10L, 100L})
    void doGetRedirectToExceptionHandlerWhenDtoIsNotPresent(Long id) throws IOException {
        String pathInfo = "/" + id;
        doReturn(pathInfo).when(req).getPathInfo();

        doThrow(new EntityNotFoundException("Any message")).when(lawyerService).readById(id);

        lawyerServlet.doGet(req, resp);

        verify(exceptionHandler).handleException(any(), any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1/", "/1/", "//1"})
    void doGetRedirectToExceptionHandlerIfPathIncorrect(String pathInfo) throws IOException {
        doReturn(pathInfo).when(req).getPathInfo();

        lawyerServlet.doGet(req, resp);

        verify(exceptionHandler).handleException(any(), any());
    }

    @Test
    void doPostSuccess() throws IOException {
        String pathInfo = "/";
        doReturn(pathInfo).when(req).getPathInfo();

        doReturn(reader).when(req).getReader();

        var dto = getDto();
        doReturn(dto).when(objectMapper).readValue(reader, LawyerDto.class);
        var dtoWithId = getDtoWithId();
        doReturn(dtoWithId).when(lawyerService).create(dto);

        doReturn(printWriter).when(resp).getWriter();

        lawyerServlet.doPost(req, resp);

        verify(resp, times(1)).setContentType(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo("application/json");

        verify(resp, times(1)).setCharacterEncoding(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo(StandardCharsets.UTF_8.name());

        verify(resp, times(1)).setStatus(integerCaptor.capture());
        assertThat(integerCaptor.getValue()).isEqualTo(HttpServletResponse.SC_CREATED);

        verify(resp, times(1)).getWriter();
        verify(objectMapper, times(1)).writeValue(any(PrintWriter.class), dtoArgumentCaptor.capture());
        assertThat(dtoArgumentCaptor.getValue()).isEqualTo(dtoWithId);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1/", "/1/", "//1"})
    void doPostRedirectToExceptionHandlerIfPathIncorrect(String pathInfo) throws IOException {
        doReturn(pathInfo).when(req).getPathInfo();

        lawyerServlet.doPost(req, resp);

        verify(exceptionHandler).handleException(any(), any());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 10L, 100L})
    void doPutSuccessWhenDtoPresent(Long id) throws IOException {
        String pathInfo = "/" + id;
        doReturn(pathInfo).when(req).getPathInfo();

        doReturn(reader).when(req).getReader();

        var dto = getDto();
        doReturn(dto).when(objectMapper).readValue(reader, LawyerDto.class);
        doNothing().when(lawyerService).update(id, dto);
        doReturn(printWriter).when(resp).getWriter();

        lawyerServlet.doPut(req, resp);

        verify(resp, times(1)).setContentType(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo("application/json");

        verify(resp, times(1)).setCharacterEncoding(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo(StandardCharsets.UTF_8.name());

        verify(resp, times(1)).setStatus(integerCaptor.capture());
        assertThat(integerCaptor.getValue()).isEqualTo(HttpServletResponse.SC_OK);

        verify(lawyerService, times(1)).update(longCaptor.capture(), dtoArgumentCaptor.capture());
        assertThat(longCaptor.getValue()).isEqualTo(id);
        assertThat(dtoArgumentCaptor.getValue()).isEqualTo(dto);

        verify(resp, times(1)).getWriter();
        verify(objectMapper, times(1)).writeValue(any(PrintWriter.class), stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo(ServletsUtil.UPDATED_MESSAGE);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 10L, 100L})
    void doPutRedirectToExceptionHandlerWhenDtoIsNotPresent(Long id) throws IOException {
        String pathInfo = "/" + id;
        doReturn(pathInfo).when(req).getPathInfo();

        doReturn(reader).when(req).getReader();

        var dto = getDto();
        doReturn(dto).when(objectMapper).readValue(reader, LawyerDto.class);
        doThrow(new EntityNotFoundException("Any Message")).when(lawyerService).update(id, dto);

        lawyerServlet.doPut(req, resp);
        verify(lawyerService, times(1)).update(longCaptor.capture(), dtoArgumentCaptor.capture());
        assertThat(longCaptor.getValue()).isEqualTo(id);
        assertThat(dtoArgumentCaptor.getValue()).isEqualTo(dto);

        verify(exceptionHandler).handleException(any(), any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/","1/", "/1/", "//1"})
    void doPutRedirectToExceptionHandlerIfPathIncorrect(String pathInfo) throws IOException {
        doReturn(pathInfo).when(req).getPathInfo();

        lawyerServlet.doPut(req, resp);

        verify(exceptionHandler).handleException(any(), any());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 10L, 100L})
    void doDeleteSuccessWhenDtoPresent(Long id) throws IOException {
        String pathInfo = "/" + id;
        doReturn(pathInfo).when(req).getPathInfo();

        doNothing().when(lawyerService).deleteById(id);

        doReturn(printWriter).when(resp).getWriter();

        lawyerServlet.doDelete(req, resp);

        verify(resp, times(1)).setContentType(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo("application/json");

        verify(resp, times(1)).setCharacterEncoding(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo(StandardCharsets.UTF_8.name());

        verify(resp, times(1)).setStatus(integerCaptor.capture());
        assertThat(integerCaptor.getValue()).isEqualTo(HttpServletResponse.SC_OK);

        verify(lawyerService, times(1)).deleteById(id);

        verify(resp, times(1)).getWriter();
        verify(objectMapper, times(1)).writeValue(any(PrintWriter.class), stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo(ServletsUtil.DELETED_MESSAGE);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 10L, 100L})
    void doDeleteRedirectToExceptionHandlerWhenDtoIsNotPresent(Long id) throws IOException {
        String pathInfo = "/" + id;
        doReturn(pathInfo).when(req).getPathInfo();

        doThrow(new EntityNotFoundException("Any Message")).when(lawyerService).deleteById(id);

        lawyerServlet.doDelete(req, resp);

        verify(exceptionHandler).handleException(any(), any());
    }
    @ParameterizedTest
    @ValueSource(strings = {"/","1/", "/1/", "//1"})
    void doDeleteRedirectToExceptionHandlerIfPathIncorrect(String pathInfo) throws IOException {
        doReturn(pathInfo).when(req).getPathInfo();

        lawyerServlet.doDelete(req, resp);

        verify(exceptionHandler).handleException(any(), any());
    }

    private LawyerDto getDto() {
        return new LawyerDto("1", "1", JobTitle.ASSOCIATE, 100.0,
                new LawFirmDto(1L, "AAA", null, null),
                new ContactDetailsDto(1L, "1", "777", "777", "777", "test@mail.com"),
                List.of(new TaskDto(), new TaskDto(), new TaskDto()));
    }

    private LawyerDto getDtoWithId() {
        return new LawyerDto(1L, "1", "1", JobTitle.ASSOCIATE, 100.0,
                new LawFirmDto(1L, "AAA", null, null),
                new ContactDetailsDto(1L, "1", "777", "777", "777", "test@mail.com"),
                List.of(new TaskDto(), new TaskDto(), new TaskDto()));
    }

    private List<LawyerDto> getDtoList() {
        return List.of(getDtoWithId(), getDtoWithId(), getDtoWithId(), getDtoWithId(), getDtoWithId());
    }
}