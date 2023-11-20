package com.mogilan.model;

import com.mogilan.servlet.dto.TaskPriority;
import com.mogilan.servlet.dto.TaskStatus;

import java.time.LocalDate;
import java.util.List;

public class Task {
    private Long id;
    private String title;
    private String description;
    private TaskPriority priority;
    private TaskStatus status;
    private LocalDate receiptDate;
    private LocalDate dueDate;
    private LocalDate completionDate;
    private double hoursSpentOnTask;
    private Client client;
    private List<Lawyer> lawyers;

    public Task() {
    }

    public Task(String title, String description, TaskPriority priority, TaskStatus status, LocalDate receiptDate, LocalDate dueDate, Client client) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.receiptDate = receiptDate;
        this.dueDate = dueDate;
        this.client = client;
    }

    public Task(Long id, String title, String description, TaskPriority priority, TaskStatus status, LocalDate receiptDate, LocalDate dueDate, LocalDate completionDate, double hoursSpentOnTask, Client client, List<Lawyer> lawyers) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.receiptDate = receiptDate;
        this.dueDate = dueDate;
        this.completionDate = completionDate;
        this.hoursSpentOnTask = hoursSpentOnTask;
        this.client = client;
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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Lawyer> getLawyers() {
        return lawyers;
    }

    public void setLawyers(List<Lawyer> lawyers) {
        this.lawyers = lawyers;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", priority=" + priority +
                ", status=" + status +
                ", receiptDate=" + receiptDate +
                ", dueDate=" + dueDate +
                ", completionDate=" + completionDate +
                ", hoursSpentOnTask=" + hoursSpentOnTask +
                ", client=" + client +
                '}';
    }
}
