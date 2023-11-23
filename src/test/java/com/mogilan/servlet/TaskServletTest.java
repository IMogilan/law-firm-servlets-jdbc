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
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServletTest {
    @Mock
    TaskService taskService;
    @Mock
    LawyerService lawyerService;
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
    ArgumentCaptor<List<TaskDto>> listArgumentCaptor;
    @Captor
    ArgumentCaptor<TaskDto> dtoArgumentCaptor;
    TaskServlet taskServlet;

    @BeforeEach
    void beforeEach() {
        taskServlet = new TaskServlet(taskService, lawyerService, exceptionHandler, objectMapper);
    }

    @Test
    void init() throws ServletException {
        doReturn(servletContext).when(config).getServletContext();
        doReturn(applicationContext).when(servletContext).getAttribute(ServletsUtil.APPLICATION_CONTEXT_KEY);
        doReturn(objectMapper).when(applicationContext).getDependency(ServletsUtil.OBJECT_MAPPER_KEY);

        taskServlet.init(config);

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
        doReturn(dtoList).when(taskService).readAll();

        doReturn(printWriter).when(resp).getWriter();

        taskServlet.doGet(req, resp);

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
        doReturn(dtoList).when(taskService).readAll();

        doReturn(printWriter).when(resp).getWriter();

        taskServlet.doGet(req, resp);

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
        doReturn(dto).when(taskService).readById(id);

        doReturn(printWriter).when(resp).getWriter();

        taskServlet.doGet(req, resp);

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

        doThrow(new EntityNotFoundException("Any message")).when(taskService).readById(id);

        taskServlet.doGet(req, resp);

        verify(exceptionHandler).handleException(any(), any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1/", "/1/", "//1"})
    void doGetRedirectToExceptionHandlerIfPathIncorrect(String pathInfo) throws IOException {
        doReturn(pathInfo).when(req).getPathInfo();

        taskServlet.doGet(req, resp);

        verify(exceptionHandler).handleException(any(), any());
    }

    @Test
    void doPostSuccess() throws IOException {
        String pathInfo = "/";
        doReturn(pathInfo).when(req).getPathInfo();

        doReturn(reader).when(req).getReader();

        var dto = getDto();
        doReturn(dto).when(objectMapper).readValue(reader, TaskDto.class);
        var dtoWithId = getDtoWithId();
        doReturn(dtoWithId).when(taskService).create(dto);

        doReturn(printWriter).when(resp).getWriter();

        taskServlet.doPost(req, resp);

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

        taskServlet.doPost(req, resp);

        verify(exceptionHandler).handleException(any(), any());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 10L, 100L})
    void doPutSuccessWhenDtoPresent(Long id) throws IOException {
        String pathInfo = "/" + id;
        doReturn(pathInfo).when(req).getPathInfo();

        doReturn(reader).when(req).getReader();

        var dto = getDto();
        doReturn(dto).when(objectMapper).readValue(reader, TaskDto.class);
        doNothing().when(taskService).update(id, dto);
        doReturn(printWriter).when(resp).getWriter();

        taskServlet.doPut(req, resp);

        verify(resp, times(1)).setContentType(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo("application/json");

        verify(resp, times(1)).setCharacterEncoding(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo(StandardCharsets.UTF_8.name());

        verify(resp, times(1)).setStatus(integerCaptor.capture());
        assertThat(integerCaptor.getValue()).isEqualTo(HttpServletResponse.SC_OK);

        verify(taskService, times(1)).update(longCaptor.capture(), dtoArgumentCaptor.capture());
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
        doReturn(dto).when(objectMapper).readValue(reader, TaskDto.class);
        doThrow(new EntityNotFoundException("Any Message")).when(taskService).update(id, dto);

        taskServlet.doPut(req, resp);
        verify(taskService, times(1)).update(longCaptor.capture(), dtoArgumentCaptor.capture());
        assertThat(longCaptor.getValue()).isEqualTo(id);
        assertThat(dtoArgumentCaptor.getValue()).isEqualTo(dto);

        verify(exceptionHandler).handleException(any(), any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/","1/", "/1/", "//1"})
    void doPutRedirectToExceptionHandlerIfPathIncorrect(String pathInfo) throws IOException {
        doReturn(pathInfo).when(req).getPathInfo();

        taskServlet.doPut(req, resp);

        verify(exceptionHandler).handleException(any(), any());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 10L, 100L})
    void doDeleteSuccessWhenDtoPresent(Long id) throws IOException {
        String pathInfo = "/" + id;
        doReturn(pathInfo).when(req).getPathInfo();

        doNothing().when(taskService).deleteById(id);

        doReturn(printWriter).when(resp).getWriter();

        taskServlet.doDelete(req, resp);

        verify(resp, times(1)).setContentType(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo("application/json");

        verify(resp, times(1)).setCharacterEncoding(stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo(StandardCharsets.UTF_8.name());

        verify(resp, times(1)).setStatus(integerCaptor.capture());
        assertThat(integerCaptor.getValue()).isEqualTo(HttpServletResponse.SC_OK);

        verify(taskService, times(1)).deleteById(id);

        verify(resp, times(1)).getWriter();
        verify(objectMapper, times(1)).writeValue(any(PrintWriter.class), stringCaptor.capture());
        assertThat(stringCaptor.getValue()).isEqualTo(ServletsUtil.DELETED_MESSAGE);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 10L, 100L})
    void doDeleteRedirectToExceptionHandlerWhenDtoIsNotPresent(Long id) throws IOException {
        String pathInfo = "/" + id;
        doReturn(pathInfo).when(req).getPathInfo();

        doThrow(new EntityNotFoundException("Any Message")).when(taskService).deleteById(id);

        taskServlet.doDelete(req, resp);

        verify(exceptionHandler).handleException(any(), any());
    }
    @ParameterizedTest
    @ValueSource(strings = {"/","1/", "/1/", "//1"})
    void doDeleteRedirectToExceptionHandlerIfPathIncorrect(String pathInfo) throws IOException {
        doReturn(pathInfo).when(req).getPathInfo();

        taskServlet.doDelete(req, resp);

        verify(exceptionHandler).handleException(any(), any());
    }

    private TaskDto getDto() {
        return new TaskDto("Make draft", "ASP", TaskPriority.HIGH, TaskStatus.ACCEPTED,
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
        );
    }

    private TaskDto getDtoWithId() {
        return new TaskDto(1L, "Make draft", "ASP", TaskPriority.HIGH, TaskStatus.ACCEPTED,
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
        );
    }

    private List<TaskDto> getDtoList() {
        return List.of(getDtoWithId(), getDtoWithId(), getDtoWithId(), getDtoWithId(), getDtoWithId());
    }
}