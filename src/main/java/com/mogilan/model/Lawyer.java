package com.mogilan.model;

import com.mogilan.servlet.dto.JobTitle;

import java.util.List;
import java.util.Objects;

public class Lawyer {
    private Long id;
    private String firstName;
    private String lastName;
    private JobTitle jobTitle;
    private double hourlyRate;
    private LawFirm lawFirm;
    private ContactDetails contacts;
    private List<Task> tasks;

    public Lawyer() {
    }

    public Lawyer(Long id, String firstName, String lastName, JobTitle jobTitle,
                  double hourlyRate, LawFirm lawFirm, ContactDetails contacts) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.jobTitle = jobTitle;
        this.hourlyRate = hourlyRate;
        this.lawFirm = lawFirm;
        this.contacts = contacts;
    }

    public Lawyer(String firstName, String lastName, JobTitle jobTitle, double hourlyRate,
                  LawFirm lawFirm, ContactDetails contacts) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.jobTitle = jobTitle;
        this.hourlyRate = hourlyRate;
        this.lawFirm = lawFirm;
        this.contacts = contacts;
    }

    public Lawyer(Long id, String firstName, String lastName, JobTitle jobTitle, double hourlyRate,
                  LawFirm lawFirm, ContactDetails contacts, List<Task> tasks) {
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

    public ContactDetails getContacts() {
        return contacts;
    }

    public void setContacts(ContactDetails contacts) {
        this.contacts = contacts;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public LawFirm getLawFirm() {
        return lawFirm;
    }

    public void setLawFirm(LawFirm lawFirm) {
        this.lawFirm = lawFirm;
    }

    @Override
    public String toString() {
        return "Lawyer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", jobTitle=" + jobTitle +
                ", hourlyRate=" + hourlyRate +
                ", lawFirm=" + lawFirm +
                ", contacts=" + contacts +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lawyer lawyer = (Lawyer) o;
        return Double.compare(lawyer.getHourlyRate(), getHourlyRate()) == 0 && Objects.equals(getId(), lawyer.getId()) && Objects.equals(getFirstName(), lawyer.getFirstName()) && Objects.equals(getLastName(), lawyer.getLastName()) && getJobTitle() == lawyer.getJobTitle();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFirstName(), getLastName(), getJobTitle(), getHourlyRate());
    }
}
