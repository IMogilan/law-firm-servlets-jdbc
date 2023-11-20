package com.mogilan.servlet.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private TaskPriority priority;
    private TaskStatus status;
    private LocalDate receiptDate;
    private LocalDate dueDate;
    private LocalDate completionDate;
    private double hoursSpentOnTask;
    @JsonIgnore
    private ClientDto client;
    @JsonManagedReference("tasks")
    private List<LawyerDto> lawyers;

    public TaskDto() {
    }

    public TaskDto(String title, String description, TaskPriority priority, TaskStatus status, LocalDate receiptDate,
                   LocalDate dueDate, LocalDate completionDate, double hoursSpentOnTask, ClientDto client,
                   List<LawyerDto> lawyers) {
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

    public TaskDto(Long id, String title, String description, TaskPriority priority, TaskStatus status, LocalDate receiptDate, LocalDate dueDate, LocalDate completionDate, double hoursSpentOnTask, ClientDto client, List<LawyerDto> lawyers) {
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

    @JsonIgnore
    public ClientDto getClient() {
        return client;
    }

    @JsonIgnore
    public void setClient(ClientDto client) {
        this.client = client;
    }

    @JsonManagedReference("tasks")
    public List<LawyerDto> getLawyers() {
        return lawyers;
    }

    @JsonManagedReference("tasks")
    public void setLawyers(List<LawyerDto> lawyers) {
        this.lawyers = lawyers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskDto taskDto = (TaskDto) o;
        return Double.compare(taskDto.getHoursSpentOnTask(), getHoursSpentOnTask()) == 0 && Objects.equals(getId(), taskDto.getId()) && Objects.equals(getTitle(), taskDto.getTitle()) && Objects.equals(getDescription(), taskDto.getDescription()) && getPriority() == taskDto.getPriority() && getStatus() == taskDto.getStatus() && Objects.equals(getReceiptDate(), taskDto.getReceiptDate()) && Objects.equals(getDueDate(), taskDto.getDueDate()) && Objects.equals(getCompletionDate(), taskDto.getCompletionDate()) && Objects.equals(getClient(), taskDto.getClient()) && Objects.equals(getLawyers(), taskDto.getLawyers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getDescription(), getPriority(), getStatus(), getReceiptDate(), getDueDate(), getCompletionDate(), getHoursSpentOnTask(), getClient(), getLawyers());
    }
}
