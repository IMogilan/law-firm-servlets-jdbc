package com.mogilan.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.descriptor.JspConfigDescriptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ApplicationContextListenerTest {

    @Test
    void contextInitializedSuccess() {
        var servletContextMock = Mockito.mock(ServletContext.class);

        ApplicationContextListener listener = new ApplicationContextListener();
        ServletContextEvent event = new ServletContextEvent(servletContextMock);
        listener.contextInitialized(event);

        var stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        var applicationContextArgumentCaptor = ArgumentCaptor.forClass(ApplicationContext.class);
        Mockito.verify(servletContextMock).setAttribute(stringArgumentCaptor.capture(), applicationContextArgumentCaptor.capture());
        var key = stringArgumentCaptor.getValue();
        assertThat(key).isNotNull();
        assertThat(key).isEqualTo("applicationContext");
        var applicationContext = applicationContextArgumentCaptor.getValue();
        assertThat(applicationContext).isNotNull();

        var anyBean = applicationContext.getDependency("objectMapper");
        assertThat(anyBean).isNotNull();
        assertThat(anyBean).isInstanceOf(ObjectMapper.class);
    }
}