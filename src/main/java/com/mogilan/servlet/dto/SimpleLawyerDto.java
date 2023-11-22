package com.mogilan.servlet.dto;

import java.util.List;
import java.util.Objects;

public class SimpleLawyerDto {

    private Long id;
    private String firstName;
    private String lastName;
    private JobTitle jobTitle;
    private double hourlyRate;
    private ContactDetailsDto contacts;
    private List<TaskDto> tasks;

    public SimpleLawyerDto() {
    }

    public SimpleLawyerDto(Long id, String firstName, String lastName, JobTitle jobTitle, double hourlyRate, ContactDetailsDto contacts, List<TaskDto> tasks) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.jobTitle = jobTitle;
        this.hourlyRate = hourlyRate;
        this.contacts = contacts;
        this.tasks = tasks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public JobTitle getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(JobTitle jobTitle) {
        this.jobTitle = jobTitle;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public ContactDetailsDto getContacts() {
        return contacts;
    }

    public void setContacts(ContactDetailsDto contacts) {
        this.contacts = contacts;
    }

    public List<TaskDto> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDto> tasks) {
        this.tasks = tasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleLawyerDto that = (SimpleLawyerDto) o;
        return Double.compare(that.getHourlyRate(), getHourlyRate()) == 0 && Objects.equals(getId(), that.getId()) && Objects.equals(getFirstName(), that.getFirstName()) && Objects.equals(getLastName(), that.getLastName()) && getJobTitle() == that.getJobTitle();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFirstName(), getLastName(), getJobTitle(), getHourlyRate());
    }
}
