package com.mogilan.util;

import com.mogilan.exception.DaoException;
import com.mogilan.exception.EntityNotFoundException;
import com.mogilan.exception.PathVariableException;
import jakarta.servlet.http.HttpServletResponse;

import java.util.regex.Pattern;

public final class ServletsUtil {

    private ServletsUtil() {
    }

    public static Long getPathVariable(String pathInfo) throws PathVariableException {
        if (pathInfo == null || pathInfo.equals("/")) {
            throw new PathVariableException("Path variable is not correct. Please check URI");
        }
        var regEx = "\\/(\\d)+";
        var matches = Pattern.matches(regEx, pathInfo);
        if (!matches) {
            throw new PathVariableException("Path variable is not correct. Please check URI");
        }
        return Long.parseLong(pathInfo.substring(1));
    }



}
