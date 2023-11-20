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

    private ServletsUtil() {
    }

    public static Long getPathVariable(String pathInfo) throws PathVariableException {
        if (pathInfo == null || pathInfo.equals("/")) {
            throw new PathVariableException("Path variable is not correct. Please check URI");
        }
        var regEx = "\\/\\d+";
        var matches = Pattern.matches(regEx, pathInfo);
        if (!matches) {
            throw new PathVariableException("Path variable is not correct. Please check URI");
        }
        return Long.parseLong(pathInfo.substring(1));
    }


}
