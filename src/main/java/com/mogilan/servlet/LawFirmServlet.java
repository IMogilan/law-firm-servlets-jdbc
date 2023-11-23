package com.mogilan.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mogilan.context.ApplicationContext;
import com.mogilan.exception.PathVariableException;
import com.mogilan.exception.handler.ServletExceptionHandler;
import com.mogilan.service.LawFirmService;
import com.mogilan.servlet.dto.LawFirmDto;
import com.mogilan.util.ServletsUtil;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/api/law-firms/*")
public class LawFirmServlet extends HttpServlet {
    private LawFirmService lawFirmService;
    private ServletExceptionHandler exceptionHandler;
    private ObjectMapper objectMapper;

    public LawFirmServlet() {
    }

    public LawFirmServlet(LawFirmService lawFirmService, ServletExceptionHandler exceptionHandler, ObjectMapper objectMapper) {
        this.lawFirmService = lawFirmService;
        this.exceptionHandler = exceptionHandler;
        this.objectMapper = objectMapper;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        registerDependencies(config);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            var pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                var lawFirmDtoList = lawFirmService.readAll();
                if (!lawFirmDtoList.isEmpty()) {
                    writeJsonResponse(resp, HttpServletResponse.SC_OK, lawFirmDtoList);
                } else {
                    writeJsonResponse(resp, HttpServletResponse.SC_NO_CONTENT, lawFirmDtoList);
                }
            } else {
                var id = ServletsUtil.getPathVariable(pathInfo);
                var lawFirmDto = lawFirmService.readById(id);
                writeJsonResponse(resp, HttpServletResponse.SC_OK, lawFirmDto);
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
            var lawFirmDto = readRequestJson(req, LawFirmDto.class);
            var createdLawFirmDto = lawFirmService.create(lawFirmDto);
            writeJsonResponse(resp, HttpServletResponse.SC_CREATED, createdLawFirmDto);
        } catch (Exception e) {
            exceptionHandler.handleException(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        try {
            var pathInfo = req.getPathInfo();
            var id = ServletsUtil.getPathVariable(pathInfo);
            var lawFirmDto = readRequestJson(req, LawFirmDto.class);
            lawFirmService.update(id, lawFirmDto);
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
            lawFirmService.deleteById(id);
            writeJsonResponse(resp, HttpServletResponse.SC_OK, ServletsUtil.DELETED_MESSAGE);
        } catch (Exception e) {
            exceptionHandler.handleException(resp, e);
        }
    }

    private void registerDependencies(ServletConfig config) {
        var applicationContext = (ApplicationContext) config.getServletContext().getAttribute(ServletsUtil.APPLICATION_CONTEXT_KEY);
        this.objectMapper = (ObjectMapper) applicationContext.getDependency(ServletsUtil.OBJECT_MAPPER_KEY);
        this.exceptionHandler = (ServletExceptionHandler) applicationContext.getDependency(ServletsUtil.SERVLET_EXCEPTION_HANDLER_KEY);

        this.lawFirmService = (LawFirmService) applicationContext.getDependency(ServletsUtil.LAW_FIRM_SERVICE_KEY);
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
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        try (var writer = resp.getWriter()) {
            objectMapper.writeValue(writer, dto);
        }
    }
}
