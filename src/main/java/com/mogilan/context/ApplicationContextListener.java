package com.mogilan.context;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class ApplicationContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        var servletContext = sce.getServletContext();
        ApplicationContext context = new ApplicationContext();
        servletContext.setAttribute("applicationContext", context);
    }
}
