package com.mogilan.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogilan.db.impl.ConnectionPoolImpl;
import com.mogilan.exception.handler.impl.ServletExceptionHandlerImpl;
import com.mogilan.repository.impl.*;
import com.mogilan.repository.mapper.impl.*;
import com.mogilan.service.impl.*;
import com.mogilan.servlet.mapper.SimpleTaskMapper;
import com.mogilan.servlet.mapper.impl.*;

import java.util.HashMap;

public class ApplicationContext {

    private final HashMap<String, Object> beans;

    public ApplicationContext() {
        beans = new HashMap<>();
        initAppContext(beans);
    }

    private void initAppContext(HashMap<String, Object> beans) {

        var objectMapper = new ObjectMapper();
        beans.put("objectMapper", objectMapper);

        var exceptionHandler = new ServletExceptionHandlerImpl(objectMapper);
        beans.put("servletExceptionHandler", exceptionHandler);

        var connectionPool = new ConnectionPoolImpl();
        beans.put("connectionPool", connectionPool);

        var contactDetailsResultSetMapper = new ContactDetailsResultSetMapperImpl();
        beans.put("contactDetailsResultSetMapper", contactDetailsResultSetMapper);
        var contactDetailsDao = new ContactDetailsDaoImpl(connectionPool, contactDetailsResultSetMapper);
        beans.put("contactDetailsDao", contactDetailsDao);

        var taskResultSetMapper = new TaskResultSetMapperImpl();
        beans.put("taskResultSetMapper", taskResultSetMapper);

        var taskDao = new TaskDaoImpl(connectionPool, taskResultSetMapper);
        beans.put("taskDao", taskDao);


        var lawyerResultSetMapper = new LawyerResultSetMapperImpl(contactDetailsDao, taskDao);
        beans.put("lawyerResultSetMapper", lawyerResultSetMapper);
        var lawyerDao = new LawyerDaoImpl(connectionPool, lawyerResultSetMapper);
        beans.put("lawyerDao", lawyerDao);

        var clientResultSetMapper = new ClientResultSetMapperImpl(taskDao);
        beans.put("clientResultSetMapper", clientResultSetMapper);
        var clientDao = new ClientDaoImpl(connectionPool, clientResultSetMapper);
        beans.put("clientDao", clientDao);

        var lawFirmResultSetMapper = new LawFirmResultSetMapperImpl(lawyerDao);
        beans.put("lawFirmResultSetMapper", lawFirmResultSetMapper);
        var lawFirmDao = new LawFirmDaoImpl(connectionPool, lawFirmResultSetMapper);
        beans.put("lawFirmDao", lawFirmDao);

        var simpleTaskMapper = SimpleTaskMapper.INSTANCE;
        var simpleLawyerMapper = new SimpleLawyerMapperImpl();

        var contactDetailsMapper = new ContactDetailsMapperImpl();
        beans.put("contactDetailsMapper", contactDetailsMapper);
        var clientMapper = new ClientMapperImpl(contactDetailsMapper, simpleTaskMapper, simpleLawyerMapper);
        beans.put("clientMapper", clientMapper);
        var taskMapper = new TaskMapperImpl(contactDetailsMapper, simpleTaskMapper, simpleLawyerMapper);
        beans.put("taskMapper", taskMapper);
        var lawyerMapper = new LawyerMapperImpl(contactDetailsMapper, simpleTaskMapper, simpleLawyerMapper);
        beans.put("lawyerMapper", lawyerMapper);
        var lawFirmMapper = new LawFirmMapperImpl(lawyerMapper, simpleLawyerMapper);
        beans.put("lawFirmMapper", lawFirmMapper);

        var contactDetailsService = new ContactDetailsServiceImpl(contactDetailsDao, contactDetailsMapper);
        beans.put("contactDetailsService", contactDetailsService);
        var lawyerService = new LawyerServiceImpl(lawyerDao, contactDetailsService, lawyerMapper);
        beans.put("lawyerService", lawyerService);
        var taskService = new TaskServiceImpl(taskDao, taskMapper, lawyerService);
        beans.put("taskService", taskService);
        var clientService = new ClientServiceImpl(clientDao, clientMapper, taskService, simpleTaskMapper, taskMapper);
        beans.put("clientService", clientService);
        var lawFirmService = new LawFirmServiceImpl(lawFirmDao, lawFirmMapper, lawyerService, simpleLawyerMapper, lawyerMapper);
        beans.put("lawFirmService", lawFirmService);
    }

    public Object getDependency(String name) {
        return beans.get(name);
    }

}
