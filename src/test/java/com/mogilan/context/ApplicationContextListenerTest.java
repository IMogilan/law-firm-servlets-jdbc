package com.mogilan.context;

import jakarta.servlet.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationContextListenerTest {

    @Mock
    ServletContextEvent servletContextEvent;
    @Mock
    ServletContext servletContext;

    @Mock
    ApplicationContext applicationContext;
    @Captor
    ArgumentCaptor<ApplicationContext> contextArgumentCaptor;
    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    @Test
    void contextInitializedSuccess() {
        doReturn(servletContext).when(servletContextEvent).getServletContext();

        ApplicationContextListener applicationContextListener = new ApplicationContextListener(applicationContext);

        applicationContextListener.contextInitialized(servletContextEvent);
        verify(servletContextEvent, times(1)).getServletContext();
        verify(servletContext, times(1)).setAttribute(stringArgumentCaptor.capture(), contextArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue()).isNotNull();
        assertThat(stringArgumentCaptor.getValue()).isEqualTo("applicationContext");
        assertThat(contextArgumentCaptor.getValue()).isNotNull();
        assertThat(contextArgumentCaptor.getValue()).isInstanceOf(ApplicationContext.class);
    }
}