package com.mogilan.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mogilan.context.ApplicationContext;
import com.mogilan.exception.PathVariableException;
import com.mogilan.exception.handler.ServletExceptionHandler;
import com.mogilan.service.ContactDetailsService;
import com.mogilan.servlet.dto.ContactDetailsDto;
import com.mogilan.util.ServletsUtil;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/api/contact-details/*")
public class ContactDetailsServlet extends HttpServlet {

    private ContactDetailsService contactDetailsService;
    private ServletExceptionHandler exceptionHandler;
    private ObjectMapper objectMapper;
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        registerDependencies(config);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public ContactDetailsServlet() {
    }

    public ContactDetailsServlet(ContactDetailsService contactDetailsService, ServletExceptionHandler exceptionHandler, ObjectMapper objectMapper) {
        this.contactDetailsService = contactDetailsService;
        this.exceptionHandler = exceptionHandler;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            var pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                var contactDetailsDtoList = contactDetailsService.readAll();
                if (!contactDetailsDtoList.isEmpty()) {
                    writeJsonResponse(resp, HttpServletResponse.SC_OK, contactDetailsDtoList);
                } else {
                    writeJsonResponse(resp, HttpServletResponse.SC_NO_CONTENT, contactDetailsDtoList);
                }
            } else {
                var id = ServletsUtil.getPathVariable(pathInfo);
                var contactDetailsDto = contactDetailsService.readById(id);
                writeJsonResponse(resp, HttpServletResponse.SC_OK, contactDetailsDto);
            }
        } catch (Exception e) {
            exceptionHandler.handleException(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            var pathInfo = req.getPathInfo();
            if (pathInfo != null && !pathInfo.equals("/")) {
                throw new PathVariableException("Method PUT doesn't support path variable");
            }
            var contactDetailsDto = readRequestJson(req, ContactDetailsDto.class);
            var createdContactDetailsDto = contactDetailsService.create(contactDetailsDto);
            writeJsonResponse(resp, HttpServletResponse.SC_CREATED, createdContactDetailsDto);
        } catch (Exception e) {
            exceptionHandler.handleException(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        try {
            var pathInfo = req.getPathInfo();
            var id = ServletsUtil.getPathVariable(pathInfo);
            var contactDetailsDto = readRequestJson(req, ContactDetailsDto.class);
            contactDetailsService.update(id, contactDetailsDto);
            writeJsonResponse(resp, HttpServletResponse.SC_OK, ServletsUtil.UPDATED_MESSAGE);
        } catch (Exception e) {
            exceptionHandler.handleException(resp, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        try {
            var pathInfo = req.getPathInfo();
            var id = ServletsUtil.getPathVariable(pathInfo);
            contactDetailsService.deleteById(id);
            writeJsonResponse(resp, HttpServletResponse.SC_OK, ServletsUtil.DELETED_MESSAGE);
        } catch (Exception e) {
            exceptionHandler.handleException(resp, e);
        }
    }

    private void registerDependencies(ServletConfig config) {
        var applicationContext = (ApplicationContext) config.getServletContext().getAttribute(ServletsUtil.APPLICATION_CONTEXT_KEY);
        this.objectMapper = (ObjectMapper) applicationContext.getDependency(ServletsUtil.OBJECT_MAPPER_KEY);
        this.exceptionHandler = (ServletExceptionHandler) applicationContext.getDependency(ServletsUtil.SERVLET_EXCEPTION_HANDLER_KEY);

        this.contactDetailsService = (ContactDetailsService) applicationContext.getDependency(ServletsUtil.CONTACT_DETAILS_SERVICE_KEY);
    }

    private <T> T readRequestJson(HttpServletRequest req, Class<T> valueType) throws IOException {
        try (var reader = req.getReader()) {
            return objectMapper.readValue(reader, valueType);
        }
    }

    private void writeJsonResponse(HttpServletResponse resp, int statusCode, Object dto) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setStatus(statusCode);
        try (var writer = resp.getWriter()) {
            objectMapper.writeValue(writer, dto);
        }
    }
}
