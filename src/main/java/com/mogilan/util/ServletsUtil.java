package com.mogilan.util;

import com.mogilan.exception.PathVariableException;

import java.util.regex.Pattern;

public final class ServletsUtil {

    public static final String UPDATED_MESSAGE = "UPDATED";
    public static final String DELETED_MESSAGE = "DELETED";
    public static final String BAD_REQUEST_MESSAGE = "BAD_REQUEST";
    public static final String NOT_FOUND_MESSAGE = "NOT_FOUND";
    public static final String PATH_VARIABLE_IS_NOT_CORRECT_MESSAGE = "Path variable is not correct. Please check URI";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

    public static final String APPLICATION_CONTEXT_KEY = "applicationContext";
    public static final String OBJECT_MAPPER_KEY = "objectMapper";
    public static final String SERVLET_EXCEPTION_HANDLER_KEY = "servletExceptionHandler";
    public static final String CONTACT_DETAILS_SERVICE_KEY = "contactDetailsService";
    public static final String CLIENT_SERVICE_KEY = "clientService";
    public static final String LAW_FIRM_SERVICE_KEY = "lawFirmService";
    public static final String LAWYER_SERVICE_KEY = "lawyerService";
    public static final String TASK_SERVICE_KEY = "taskService";

    private ServletsUtil() {
    }

    public static Long getPathVariable(String pathInfo) throws PathVariableException {
        if (pathInfo == null || pathInfo.equals("/")) {
            throw new PathVariableException(PATH_VARIABLE_IS_NOT_CORRECT_MESSAGE);
        }
        var regEx = "\\/\\d+";
        var matches = Pattern.matches(regEx, pathInfo);
        if (!matches) {
            throw new PathVariableException(PATH_VARIABLE_IS_NOT_CORRECT_MESSAGE);
        }
        return Long.parseLong(pathInfo.substring(1));
    }


}
