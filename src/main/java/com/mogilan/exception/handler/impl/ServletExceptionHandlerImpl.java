package com.mogilan.exception.handler.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogilan.exception.DaoException;
import com.mogilan.exception.EntityNotFoundException;
import com.mogilan.exception.PathVariableException;
import com.mogilan.exception.handler.ServletExceptionHandler;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class ServletExceptionHandlerImpl implements ServletExceptionHandler {

    private static final ServletExceptionHandler INSTANCE = new ServletExceptionHandlerImpl();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private ServletExceptionHandlerImpl() {
    }

    @Override
    public void handleException(HttpServletResponse resp, Exception e) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        var errorStatusCode = getErrorStatusCode(e);
        resp.setStatus(errorStatusCode);
        var errorStatusMessage = getErrorStatusMessage(e);
        try (var writer = resp.getWriter()) {
            objectMapper.writeValue(writer, errorStatusMessage);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static ServletExceptionHandler getInstance(){
        return INSTANCE;
    }

    public int getErrorStatusCode(Exception e) {
        if (e instanceof DaoException) {
            return HttpServletResponse.SC_BAD_REQUEST;
        } else if (e instanceof IllegalArgumentException) {
            return HttpServletResponse.SC_BAD_REQUEST;
        } else if (e instanceof EntityNotFoundException) {
            return HttpServletResponse.SC_NOT_FOUND;
        } else {
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
    }

    public String getErrorStatusMessage(Exception e) {
        if (e instanceof DaoException) {
            return "BAD_REQUEST";
        } else if (e instanceof PathVariableException) {
            return e.getMessage();
        } else if (e instanceof IllegalArgumentException) {
            return "BAD_REQUEST";
        } else if (e instanceof EntityNotFoundException) {
            return "NOT_FOUND";
        } else {
            return "INTERNAL_SERVER_ERROR";
        }
    }
}
