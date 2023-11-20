package com.mogilan.servlet.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDate;
import java.util.List;

public class LawFirmDto {
    private Long id;
    private String name;
    private LocalDate companyStartDay;
    @JsonManagedReference
    private List<LawyerDto> lawyers;

    public LawFirmDto() {
    }

    public LawFirmDto(String name, LocalDate companyStartDay, List<LawyerDto> lawyers) {
        this.name = name;
        this.companyStartDay = companyStartDay;
        this.lawyers = lawyers;
    }

    public LawFirmDto(Long id, String name, LocalDate companyStartDay, List<LawyerDto> lawyers) {
        this.id = id;
        this.name = name;
        this.companyStartDay = companyStartDay;
        this.lawyers = lawyers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getCompanyStartDay() {
        return companyStartDay;
    }

    public void setCompanyStartDay(LocalDate companyStartDay) {
        this.companyStartDay = companyStartDay;
    }

    public List<LawyerDto> getLawyers() {
        return lawyers;
    }

    public void setLawyers(List<LawyerDto> lawyers) {
        this.lawyers = lawyers;
    }

    @Override
    public String toString() {
        return "LawFirmDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", companyStartDay=" + companyStartDay +
                '}';
    }
}
