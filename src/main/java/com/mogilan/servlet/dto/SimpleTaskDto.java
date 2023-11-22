package com.mogilan.servlet.dto;

import java.time.LocalDate;
import java.util.List;

public class SimpleTaskDto {
    private Long id;
    private String title;
    private String description;
    private TaskPriority priority;
    private TaskStatus status;
    private LocalDate receiptDate;
    private LocalDate dueDate;
    private LocalDate completionDate;
    private double hoursSpentOnTask;
    private List<LawyerDto> lawyers;

    public SimpleTaskDto() {
    }

    public SimpleTaskDto(Long id, String title, String description, TaskPriority priority, TaskStatus status, LocalDate receiptDate, LocalDate dueDate, LocalDate completionDate, double hoursSpentOnTask, List<LawyerDto> lawyers) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.receiptDate = receiptDate;
        this.dueDate = dueDate;
        this.completionDate = completionDate;
        this.hoursSpentOnTask = hoursSpentOnTask;
        this.lawyers = lawyers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDate getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(LocalDate receiptDate) {
        this.receiptDate = receiptDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public double getHoursSpentOnTask() {
        return hoursSpentOnTask;
    }

    public void setHoursSpentOnTask(double hoursSpentOnTask) {
        this.hoursSpentOnTask = hoursSpentOnTask;
    }

    public List<LawyerDto> getLawyers() {
        return lawyers;
    }

    public void setLawyers(List<LawyerDto> lawyers) {
        this.lawyers = lawyers;
    }
}
