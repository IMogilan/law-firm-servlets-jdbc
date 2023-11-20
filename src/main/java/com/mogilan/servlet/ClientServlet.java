package com.mogilan.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mogilan.exception.PathVariableException;
import com.mogilan.exception.handler.ServletExceptionHandler;
import com.mogilan.exception.handler.impl.ServletExceptionHandlerImpl;
import com.mogilan.service.ClientService;
import com.mogilan.service.impl.ClientServiceImpl;
import com.mogilan.servlet.dto.ClientDto;
import com.mogilan.util.ServletsUtil;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/api/clients/*")
public class ClientServlet extends HttpServlet {
    private final ClientService clientService = ClientServiceImpl.getInstance();
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
                var clientDtoList = clientService.readAll();
                if (!clientDtoList.isEmpty()) {
                    writeJsonResponse(resp, HttpServletResponse.SC_OK, clientDtoList);
                } else {
                    writeJsonResponse(resp, HttpServletResponse.SC_NO_CONTENT, clientDtoList);
                }
            } else {
                var id = ServletsUtil.getPathVariable(pathInfo);
                var clientDto = clientService.readById(id);
                writeJsonResponse(resp, HttpServletResponse.SC_OK, clientDto);
            }
        } catch (Exception e) {
            exceptionHandler.handleException(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            var pathInfo = req.getPathInfo();
            if (pathInfo != null || !pathInfo.equals("/")){
                throw new PathVariableException("Method PUT doesn't support path variable");
            }
            var clientDto = readRequestJson(req, ClientDto.class);
            var createdClientDto = clientService.create(clientDto);
            writeJsonResponse(resp, HttpServletResponse.SC_CREATED, createdClientDto);
        } catch (Exception e) {
            exceptionHandler.handleException(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        try {
            var pathInfo = req.getPathInfo();
            var id = ServletsUtil.getPathVariable(pathInfo);
            var clientDto = readRequestJson(req, ClientDto.class);
            clientService.update(id, clientDto);
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
            clientService.deleteById(id);
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
