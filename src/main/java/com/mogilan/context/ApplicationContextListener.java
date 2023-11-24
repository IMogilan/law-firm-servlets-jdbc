package com.mogilan.context;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class ApplicationContextListener implements ServletContextListener {
    ApplicationContext context;

    public ApplicationContextListener() {
        context = new ApplicationContext();
    }

    public ApplicationContextListener(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        var servletContext = sce.getServletContext();
        servletContext.setAttribute("applicationContext", context);
    }
}
