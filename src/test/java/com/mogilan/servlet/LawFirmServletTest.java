package com.mogilan.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mogilan.context.ApplicationContext;
import com.mogilan.exception.handler.ServletExceptionHandler;
import com.mogilan.service.LawFirmService;
import com.mogilan.util.ServletsUtil;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LawFirmServletTest {
    @Mock
    LawFirmService lawFirmService;
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
    LawFirmServlet lawFirmServlet;

    @BeforeEach
    void beforeEach(){
        lawFirmServlet = new LawFirmServlet();
    }

    @Test
    void init() throws ServletException {
        doReturn(servletContext).when(config).getServletContext();
        doReturn(applicationContext).when(servletContext).getAttribute(ServletsUtil.APPLICATION_CONTEXT_KEY);
        doReturn(objectMapper).when(applicationContext).getDependency(ServletsUtil.OBJECT_MAPPER_KEY);

        lawFirmServlet.init(config);

        verify(config, times(1)).getServletContext();
        verify(servletContext, times(1)).getAttribute(ServletsUtil.APPLICATION_CONTEXT_KEY);
        verify(applicationContext, times(1)).getDependency(ServletsUtil.OBJECT_MAPPER_KEY);
        verify(applicationContext, times(1)).getDependency(ServletsUtil.SERVLET_EXCEPTION_HANDLER_KEY);
        verify(applicationContext, times(1)).getDependency(ServletsUtil.LAW_FIRM_SERVICE_KEY);

        verify(objectMapper, times(1)).registerModule(any());
        verify(objectMapper, times(1)).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Test
    void doGet() {
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
}