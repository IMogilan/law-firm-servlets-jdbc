package com.mogilan.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mogilan.context.ApplicationContext;
import com.mogilan.exception.EntityNotFoundException;
import com.mogilan.exception.handler.ServletExceptionHandler;
import com.mogilan.service.ContactDetailsService;
import com.mogilan.servlet.dto.ContactDetailsDto;
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

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactDetailsServletTest {

    @Mock
    ContactDetailsService contactDetailsService;
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
    @Captor
    ArgumentCaptor<String> stringCaptor;
    @Captor
    ArgumentCaptor<Integer> integerCaptor;
    @Captor
    ArgumentCaptor<List<ContactDetailsDto>> listArgumentCaptor;
    @Captor
    ArgumentCaptor<ContactDetailsDto> dtoArgumentCaptor;
    ContactDetailsServlet contactDetailsServlet;

    @BeforeEach
    void beforeEach(){
        contactDetailsServlet = new ContactDetailsServlet(contactDetailsService, exceptionHandler, objectMapper);
    }

    @Test
    void init() throws ServletException {
        doReturn(servletContext).when(config).getServletContext();
        doReturn(applicationContext).when(servletContext).getAttribute(ServletsUtil.APPLICATION_CONTEXT_KEY);
        doReturn(objectMapper).when(applicationContext).getDependency(ServletsUtil.OBJECT_MAPPER_KEY);

        contactDetailsServlet.init(config);

        verify(config, times(1)).getServletContext();
        verify(servletContext, times(1)).getAttribute(ServletsUtil.APPLICATION_CONTEXT_KEY);
        verify(applicationContext, times(1)).getDependency(ServletsUtil.OBJECT_MAPPER_KEY);
        verify(applicationContext, times(1)).getDependency(ServletsUtil.SERVLET_EXCEPTION_HANDLER_KEY);
        verify(applicationContext, times(1)).getDependency(ServletsUtil.CONTACT_DETAILS_SERVICE_KEY);

        verify(objectMapper, times(1)).registerModule(any());
        verify(objectMapper, times(1)).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Test
    void doGetSuccessWhenDtoListNotEmpty() throws IOException {
        String pathInfo = "/";
        doReturn(pathInfo).when(req).getPathInfo();

        var dtoList = getDtoList();
        assertThat(dtoList).isNotEmpty();
        doReturn(dtoList).when(contactDetailsService).readAll();

        doReturn(printWriter).when(resp).getWriter();

        contactDetailsServlet.doGet(req, resp);

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
        doReturn(dtoList).when(contactDetailsService).readAll();

        doReturn(printWriter).when(resp).getWriter();

        contactDetailsServlet.doGet(req, resp);

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
        doReturn(dto).when(contactDetailsService).readById(id);

        doReturn(printWriter).when(resp).getWriter();

        contactDetailsServlet.doGet(req, resp);

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

        doThrow(new EntityNotFoundException("Any message")).when(contactDetailsService).readById(id);

        contactDetailsServlet.doGet(req, resp);

        verify(exceptionHandler).handleException(any(), any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1/", "/1/", "//1"})
    void doGetRedirectToExceptionHandlerIfPathIncorrect(String pathInfo) throws IOException {
        doReturn(pathInfo).when(req).getPathInfo();

        contactDetailsServlet.doGet(req, resp);

        verify(exceptionHandler).handleException(any(), any());
    }

    @Test
    void doPost() {
    }

    @Test
    void doPut() {
    }

    @Test
    void doDelete() {
    }

    private ContactDetailsDto getDto() {
        return new ContactDetailsDto("Address", "777", "888", "999", "test@email.com");
    }

    private ContactDetailsDto getDtoWithId() {
        return new ContactDetailsDto(1L, "Address", "777", "888", "999", "test@email.com");
    }

    private List<ContactDetailsDto> getDtoList() {
        return List.of(getDtoWithId(),getDtoWithId(),getDtoWithId(),getDtoWithId(),getDtoWithId());
    }

}