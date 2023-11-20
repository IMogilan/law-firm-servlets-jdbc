package com.mogilan.model;

public class ContactDetails {
    private Long id;
    private String address;
    private String telNumber;
    private String mobNumber;
    private String faxNumber;
    private String email;

    public ContactDetails() {
    }

    public ContactDetails(String address, String telNumber, String mobNumber, String faxNumber, String email) {
        this.address = address;
        this.telNumber = telNumber;
        this.mobNumber = mobNumber;
        this.faxNumber = faxNumber;
        this.email = email;
    }

    public ContactDetails(Long id, String address, String telNumber, String mobNumber, String faxNumber, String email) {
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
        return "ContactDetails{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", telNumber='" + telNumber + '\'' +
                ", mobNumber='" + mobNumber + '\'' +
                ", faxNumber='" + faxNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
