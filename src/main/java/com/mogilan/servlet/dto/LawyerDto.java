package com.mogilan.servlet.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class LawyerDto {
    private Long id;
    private String firstName;
    private String lastName;
    private JobTitle jobTitle;
    private double hourlyRate;
    //    @JsonBackReference("lawyers"
    @JsonBackReference
    private LawFirmDto lawFirm;
    private ContactDetailsDto contacts;
    @JsonIgnore
    private List<TaskDto> tasks;

    public LawyerDto(String firstName, String lastName, JobTitle jobTitle,
                     double hourlyRate, LawFirmDto lawFirm, ContactDetailsDto contacts, List<TaskDto> tasks) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.jobTitle = jobTitle;
        this.hourlyRate = hourlyRate;
        this.lawFirm = lawFirm;
        this.contacts = contacts;
        this.tasks = tasks;
    }

    public LawyerDto() {
    }

    public LawyerDto(Long id, String firstName, String lastName, JobTitle jobTitle, double hourlyRate,
                     LawFirmDto lawFirm, ContactDetailsDto contacts, List<TaskDto> tasks) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.jobTitle = jobTitle;
        this.hourlyRate = hourlyRate;
        this.lawFirm = lawFirm;
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

    @JsonIgnore
    public LawFirmDto getLawFirm() {
        return lawFirm;
    }

    @JsonIgnore
    public void setLawFirm(LawFirmDto lawFirm) {
        this.lawFirm = lawFirm;
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
    public String toString() {
        return "LawyerDto{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", jobTitle=" + jobTitle +
                ", hourlyRate=" + hourlyRate +
                ", contacts=" + contacts +
                '}';
    }
}
