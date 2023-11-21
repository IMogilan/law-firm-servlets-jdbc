package com.mogilan.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mogilan.exception.PathVariableException;
import com.mogilan.exception.handler.ServletExceptionHandler;
import com.mogilan.exception.handler.impl.ServletExceptionHandlerImpl;
import com.mogilan.service.LawyerService;
import com.mogilan.service.TaskService;
import com.mogilan.service.impl.LawyerServiceImpl;
import com.mogilan.service.impl.TaskServiceImpl;
import com.mogilan.servlet.dto.LawyerDto;
import com.mogilan.servlet.dto.TaskDto;
import com.mogilan.util.ServletsUtil;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/api/tasks/*")
public class TaskServlet extends HttpServlet {
    private final TaskService taskService = TaskServiceImpl.getInstance();
    private final LawyerService lawyerService = LawyerServiceImpl.getInstance();
    private final ServletExceptionHandler exceptionHandler = ServletExceptionHandlerImpl.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String REGEX_FOR_SUB_RESOURCES_1 = "\\/(\\d+)\\/lawyers\\/";
    private static final String REGEX_FOR_SUB_RESOURCES_2 = "\\/(\\d+)\\/lawyers\\/(\\d*)";

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
            if (pathInfo != null &&
                    (Pattern.matches(REGEX_FOR_SUB_RESOURCES_1, pathInfo) ||
                            Pattern.matches(REGEX_FOR_SUB_RESOURCES_2, pathInfo))) {
                processDoGetForSubResources(req, resp);
            } else if (pathInfo == null || pathInfo.equals("/")) {
                var taskDtoList = taskService.readAll();
                if (!taskDtoList.isEmpty()) {
                    writeJsonResponse(resp, HttpServletResponse.SC_OK, taskDtoList);
                } else {
                    writeJsonResponse(resp, HttpServletResponse.SC_NO_CONTENT, taskDtoList);
                }
            } else {
                var id = ServletsUtil.getPathVariable(pathInfo);
                var taskDto = taskService.readById(id);
                writeJsonResponse(resp, HttpServletResponse.SC_OK, taskDto);
            }
        } catch (Exception e) {
            exceptionHandler.handleException(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            var pathInfo = req.getPathInfo();
            if (pathInfo != null &&
                    (Pattern.matches(REGEX_FOR_SUB_RESOURCES_1, pathInfo) ||
                            Pattern.matches(REGEX_FOR_SUB_RESOURCES_2, pathInfo))) {
                processDoPostForSubResources(req, resp);
            } else if (pathInfo != null && !pathInfo.equals("/")) {
                throw new PathVariableException("Method PUT doesn't support path variable");
            } else {
                var taskDto = readRequestJson(req, TaskDto.class);
                var createdTaskDto = taskService.create(taskDto);
                writeJsonResponse(resp, HttpServletResponse.SC_CREATED, createdTaskDto);
            }
        } catch (Exception e) {
            exceptionHandler.handleException(resp, e);
        }
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        try {
            var pathInfo = req.getPathInfo();
            if (pathInfo != null &&
                    (Pattern.matches(REGEX_FOR_SUB_RESOURCES_1, pathInfo) ||
                            Pattern.matches(REGEX_FOR_SUB_RESOURCES_2, pathInfo))) {
                processDoPutForSubResources(req, resp);
            } else {
                var id = ServletsUtil.getPathVariable(pathInfo);
                var taskDto = readRequestJson(req, TaskDto.class);
                taskService.update(id, taskDto);
                writeJsonResponse(resp, HttpServletResponse.SC_OK, ServletsUtil.UPDATED_MESSAGE);
            }
        } catch (Exception e) {
            exceptionHandler.handleException(resp, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        try {
            var pathInfo = req.getPathInfo();
            if (pathInfo != null &&
                    (Pattern.matches(REGEX_FOR_SUB_RESOURCES_1, pathInfo) ||
                            Pattern.matches(REGEX_FOR_SUB_RESOURCES_2, pathInfo))) {
                processDoDeleteForSubResources(req, resp);
            } else {
                var id = ServletsUtil.getPathVariable(pathInfo);
                taskService.deleteById(id);
                writeJsonResponse(resp, HttpServletResponse.SC_OK, ServletsUtil.DELETED_MESSAGE);
            }
        } catch (Exception e) {
            exceptionHandler.handleException(resp, e);
        }
    }

    private void processDoGetForSubResources(HttpServletRequest req, HttpServletResponse resp) throws IOException, PathVariableException {
        var ids = getResourcesIds(req);
        var taskId = ids.resourceId;
        var lawyerId = ids.subResourceId;
        if (taskId != null && lawyerId == null) {
            var lawyerDtoList = lawyerService.readAllByTaskId(taskId);
            if (!lawyerDtoList.isEmpty()) {
                writeJsonResponse(resp, HttpServletResponse.SC_OK, lawyerDtoList);
            } else {
                writeJsonResponse(resp, HttpServletResponse.SC_NO_CONTENT, lawyerDtoList);
            }
        } else if (taskId != null) {
            if (taskService.isLawyerResponsibleForTask(taskId, lawyerId)) {
                var lawyerDto = lawyerService.readById(lawyerId);
                writeJsonResponse(resp, HttpServletResponse.SC_OK, lawyerDto);
            } else {
                writeJsonResponse(resp, HttpServletResponse.SC_NOT_FOUND, ServletsUtil.NOT_FOUND_MESSAGE);
            }
        } else {
            throw new PathVariableException(ServletsUtil.PATH_VARIABLE_IS_NOT_CORRECT_MESSAGE);
        }
    }

    private void processDoPostForSubResources(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var ids = getResourcesIds(req);
        var taskId = ids.resourceId;
        var lawyerId = ids.subResourceId;
        if (taskId != null && lawyerId == null) {
            var lawyerDto = readRequestJson(req, LawyerDto.class);
            var createdLawyer = lawyerService.create(lawyerDto);
            writeJsonResponse(resp, HttpServletResponse.SC_CREATED, createdLawyer);
        } else {
            throw new PathVariableException(ServletsUtil.PATH_VARIABLE_IS_NOT_CORRECT_MESSAGE);
        }
    }

    private void processDoPutForSubResources(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var ids = getResourcesIds(req);
        var taskId = ids.resourceId;
        var lawyerId = ids.subResourceId;
        if (taskId != null && lawyerId != null) {
            if (taskService.isLawyerResponsibleForTask(taskId, lawyerId)) {
                var lawyerDto = readRequestJson(req, LawyerDto.class);
                lawyerService.update(lawyerId, lawyerDto);
                writeJsonResponse(resp, HttpServletResponse.SC_OK, ServletsUtil.UPDATED_MESSAGE);
            } else {
                writeJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ServletsUtil.BAD_REQUEST_MESSAGE);
            }
        } else {
            throw new PathVariableException(ServletsUtil.PATH_VARIABLE_IS_NOT_CORRECT_MESSAGE);
        }
    }

    private void processDoDeleteForSubResources(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var ids = getResourcesIds(req);
        var taskId = ids.resourceId;
        var lawyerId = ids.subResourceId;
        if (taskId != null && lawyerId != null) {
            if (taskService.isLawyerResponsibleForTask(taskId, lawyerId)) {
                lawyerService.deleteById(lawyerId);
                writeJsonResponse(resp, HttpServletResponse.SC_OK, ServletsUtil.DELETED_MESSAGE);
            } else {
                writeJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ServletsUtil.BAD_REQUEST_MESSAGE);
            }
        } else {
            throw new PathVariableException(ServletsUtil.PATH_VARIABLE_IS_NOT_CORRECT_MESSAGE);
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

    private Ids getResourcesIds(HttpServletRequest req) throws PathVariableException {
        var pathInfo = req.getPathInfo();
        Pattern pattern = Pattern.compile(REGEX_FOR_SUB_RESOURCES_2);
        Matcher matcher = pattern.matcher(pathInfo);
        Long resourceId = null;
        Long subResourceId = null;
        if (matcher.find()) {
            var group1 = matcher.group(1);
            if (!group1.isBlank()) {
                resourceId = Long.parseLong(group1);
            }
            var group2 = matcher.group(2);
            if (!group2.isBlank()) {
                subResourceId = Long.parseLong(group2);
            }
        } else {
            throw new PathVariableException(ServletsUtil.PATH_VARIABLE_IS_NOT_CORRECT_MESSAGE);
        }
        return new Ids(resourceId, subResourceId);
    }

    record Ids(Long resourceId, Long subResourceId) {
    }
}
