package com.mogilan.servlet.dto;

import java.util.Objects;

public class ContactDetailsDto {
    private Long id;
    private String address;
    private String telNumber;
    private String mobNumber;
    private String faxNumber;
    private String email;

    public ContactDetailsDto() {
    }

    public ContactDetailsDto(String address, String telNumber, String mobNumber, String faxNumber, String email) {
        this.address = address;
        this.telNumber = telNumber;
        this.mobNumber = mobNumber;
        this.faxNumber = faxNumber;
        this.email = email;
    }

    public ContactDetailsDto(Long id, String address, String telNumber, String mobNumber, String faxNumber, String email) {
        this.id = id;
        this.address = address;
        this.telNumber = telNumber;
        this.mobNumber = mobNumber;
        this.faxNumber = faxNumber;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public String getMobNumber() {
        return mobNumber;
    }

    public void setMobNumber(String mobNumber) {
        this.mobNumber = mobNumber;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "ContactDetailsDto{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", telNumber='" + telNumber + '\'' +
                ", mobNumber='" + mobNumber + '\'' +
                ", faxNumber='" + faxNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactDetailsDto that = (ContactDetailsDto) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getAddress(), that.getAddress()) && Objects.equals(getTelNumber(), that.getTelNumber()) && Objects.equals(getMobNumber(), that.getMobNumber()) && Objects.equals(getFaxNumber(), that.getFaxNumber()) && Objects.equals(getEmail(), that.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAddress(), getTelNumber(), getMobNumber(), getFaxNumber(), getEmail());
    }
}
