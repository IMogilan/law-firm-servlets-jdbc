package com.mogilan.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mogilan.context.ApplicationContext;
import com.mogilan.exception.PathVariableException;
import com.mogilan.exception.handler.ServletExceptionHandler;
import com.mogilan.exception.handler.impl.ServletExceptionHandlerImpl;
import com.mogilan.service.LawFirmService;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/api/lawyers/*")
public class LawyerServlet extends HttpServlet {

    private LawyerService lawyerService;
    private TaskService taskService;
    private ServletExceptionHandler exceptionHandler;
    private ObjectMapper objectMapper;
    private static final String REGEX_FOR_SUB_RESOURCES_1 = "\\/(\\d+)\\/tasks\\/";
    private static final String REGEX_FOR_SUB_RESOURCES_2 = "\\/(\\d+)\\/tasks\\/(\\d*)";

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
            if (pathInfo != null &&
                    (Pattern.matches(REGEX_FOR_SUB_RESOURCES_1, pathInfo) ||
                            Pattern.matches(REGEX_FOR_SUB_RESOURCES_2, pathInfo))) {
                processDoGetForSubResources(req, resp);
            } else if (pathInfo == null || pathInfo.equals("/")) {
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
            var pathInfo = req.getPathInfo();
            if (pathInfo != null &&
                    (Pattern.matches(REGEX_FOR_SUB_RESOURCES_1, pathInfo) ||
                            Pattern.matches(REGEX_FOR_SUB_RESOURCES_2, pathInfo))) {
                processDoPostForSubResources(req, resp);
            } else if (pathInfo != null && !pathInfo.equals("/")) {
                throw new PathVariableException("Method PUT doesn't support path variable");
            } else {
                var lawyerDto = readRequestJson(req, LawyerDto.class);
                var createdLawyerDto = lawyerService.create(lawyerDto);
                writeJsonResponse(resp, HttpServletResponse.SC_CREATED, createdLawyerDto);
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
                var lawyerDto = readRequestJson(req, LawyerDto.class);
                lawyerService.update(id, lawyerDto);
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
                lawyerService.deleteById(id);
                writeJsonResponse(resp, HttpServletResponse.SC_OK, ServletsUtil.DELETED_MESSAGE);
            }
        } catch (Exception e) {
            exceptionHandler.handleException(resp, e);
        }
    }

    private void processDoGetForSubResources(HttpServletRequest req, HttpServletResponse resp) throws IOException, PathVariableException {
        var ids = getResourcesIds(req);
        var lawyerId = ids.resourceId();
        var taskId = ids.subResourceId();
        if (lawyerId != null && taskId == null) {
            var taskDtoList = taskService.readAllByLawyerId(lawyerId);
            if (!taskDtoList.isEmpty()) {
                writeJsonResponse(resp, HttpServletResponse.SC_OK, taskDtoList);
            } else {
                writeJsonResponse(resp, HttpServletResponse.SC_NO_CONTENT, taskDtoList);
            }
        } else if (lawyerId != null) {
            if (taskService.isLawyerResponsibleForTask(taskId, lawyerId)) {
                var taskDto = taskService.readById(taskId);
                writeJsonResponse(resp, HttpServletResponse.SC_OK, taskDto);
            } else {
                writeJsonResponse(resp, HttpServletResponse.SC_NOT_FOUND, ServletsUtil.NOT_FOUND_MESSAGE);
            }
        } else {
            throw new PathVariableException(ServletsUtil.PATH_VARIABLE_IS_NOT_CORRECT_MESSAGE);
        }
    }

    private void processDoPostForSubResources(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var ids = getResourcesIds(req);
        var lawyerId = ids.resourceId();
        var taskId = ids.subResourceId();
        if (lawyerId != null && taskId == null) {
            var taskDto = readRequestJson(req, TaskDto.class);
            var createdTaskDto = taskService.create(taskDto);
            writeJsonResponse(resp, HttpServletResponse.SC_CREATED, createdTaskDto);
        } else {
            throw new PathVariableException(ServletsUtil.PATH_VARIABLE_IS_NOT_CORRECT_MESSAGE);
        }
    }

    private void processDoPutForSubResources(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var ids = getResourcesIds(req);
        var lawyerId = ids.resourceId();
        var taskId = ids.subResourceId();
        if (taskId != null && lawyerId != null) {
            if (taskService.isLawyerResponsibleForTask(taskId, lawyerId)) {
                var taskDto = readRequestJson(req, TaskDto.class);
                taskService.update(lawyerId, taskDto);
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
        var lawyerId = ids.resourceId();
        var taskId = ids.subResourceId();
        if (taskId != null && lawyerId != null) {
            if (taskService.isLawyerResponsibleForTask(taskId, lawyerId)) {
                taskService.deleteById(taskId);
                writeJsonResponse(resp, HttpServletResponse.SC_OK, ServletsUtil.DELETED_MESSAGE);
            } else {
                writeJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, ServletsUtil.BAD_REQUEST_MESSAGE);
            }
        } else {
            throw new PathVariableException(ServletsUtil.PATH_VARIABLE_IS_NOT_CORRECT_MESSAGE);
        }
    }

    private void registerDependencies(ServletConfig config) {
        var applicationContext = (ApplicationContext) config.getServletContext().getAttribute(ServletsUtil.APPLICATION_CONTEXT_KEY);
        this.objectMapper = (ObjectMapper) applicationContext.getDependency(ServletsUtil.OBJECT_MAPPER_KEY);
        this.exceptionHandler = (ServletExceptionHandler) applicationContext.getDependency(ServletsUtil.SERVLET_EXCEPTION_HANDLER_KEY);

        this.lawyerService = (LawyerService) applicationContext.getDependency(ServletsUtil.LAWYER_SERVICE_KEY);
        this.taskService = (TaskService) applicationContext.getDependency(ServletsUtil.TASK_SERVICE_KEY);
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
