package com.mogilan.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mogilan.exception.handler.ServletExceptionHandler;
import com.mogilan.exception.handler.impl.ServletExceptionHandlerImpl;
import com.mogilan.service.LawyerService;
import com.mogilan.service.impl.LawyerServiceImpl;
import com.mogilan.servlet.dto.LawyerDto;
import com.mogilan.util.ServletsUtil;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/api/lawyers/*")
public class LawyerServlet extends HttpServlet {

    private final LawyerService lawyerService = LawyerServiceImpl.getInstance();
    private final ServletExceptionHandler exceptionHandler = ServletExceptionHandlerImpl.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String UPDATED_MESSAGE = "UPDATED";
    private static final String DELETED_MESSAGE = "DELETED";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            var pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                var lawyerDtoList = lawyerService.readAll();
                if (!lawyerDtoList.isEmpty()) {
                    writeJsonResponse(resp, HttpServletResponse.SC_OK, lawyerDtoList);
                } else {
                    writeJsonResponse(resp, HttpServletResponse.SC_NO_CONTENT, lawyerDtoList);
                }
            } else {
                var id = ServletsUtil.getPathVariable(pathInfo);
                var lawyerDto = lawyerService.readById(id);
                writeJsonResponse(resp, HttpServletResponse.SC_OK, lawyerDto);
            }
        } catch (Exception e) {
            exceptionHandler.handleException(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            var lawyerDto = readRequestJson(req, LawyerDto.class);
            var createdLawyerDto = lawyerService.create(lawyerDto);
            writeJsonResponse(resp, HttpServletResponse.SC_CREATED, createdLawyerDto);
        } catch (Exception e) {
            exceptionHandler.handleException(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        try {
            var pathInfo = req.getPathInfo();
            var id = ServletsUtil.getPathVariable(pathInfo);
            var lawyerDto = readRequestJson(req, LawyerDto.class);
            lawyerService.update(id, lawyerDto);
            writeJsonResponse(resp, HttpServletResponse.SC_OK, UPDATED_MESSAGE);
        } catch (Exception e) {
            exceptionHandler.handleException(resp, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        try {
            var pathInfo = req.getPathInfo();
            var id = ServletsUtil.getPathVariable(pathInfo);
            lawyerService.deleteById(id);
            writeJsonResponse(resp, HttpServletResponse.SC_OK, DELETED_MESSAGE);
        } catch (Exception e) {
            exceptionHandler.handleException(resp, e);
        }
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
