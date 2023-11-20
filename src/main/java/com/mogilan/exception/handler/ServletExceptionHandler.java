package com.mogilan.exception.handler;

import jakarta.servlet.http.HttpServletResponse;

public interface ServletExceptionHandler {

    void handleException(HttpServletResponse resp, Exception e);
}
