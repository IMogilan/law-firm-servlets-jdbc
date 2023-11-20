package com.mogilan.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogilan.exception.handler.ServletExceptionHandler;
import com.mogilan.exception.handler.impl.ServletExceptionHandlerImpl;
import com.mogilan.service.ContactDetailsService;
import com.mogilan.service.impl.ContactDetailsServiceImpl;
import com.mogilan.servlet.dto.ContactDetailsDto;
import com.mogilan.util.ServletsUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/api/contact-details/*")
public class ContactDetailsServlet extends HttpServlet {

    private final ContactDetailsService contactDetailsService = ContactDetailsServiceImpl.getInstance();
    private final ServletExceptionHandler exceptionHandler = ServletExceptionHandlerImpl.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String UPDATED_MESSAGE = "UPDATED";
    private static final String DELETED_MESSAGE = "DELETED";

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
            contactDetailsService.deleteById(id);
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
        try (var writer = resp.getWriter()) {
            objectMapper.writeValue(writer, dto);
        }
    }
}
