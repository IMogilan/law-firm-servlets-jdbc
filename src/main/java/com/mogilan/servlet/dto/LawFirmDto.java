package com.mogilan.servlet.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class LawFirmDto {
    private Long id;
    private String name;
    private LocalDate companyStartDay;
    private List<SimpleLawyerDto> lawyers;

    public LawFirmDto() {
    }

    public LawFirmDto(Long id, String name, LocalDate companyStartDay, List<SimpleLawyerDto> lawyers) {
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

    public List<SimpleLawyerDto> getLawyers() {
        return lawyers;
    }

    public void setLawyers(List<SimpleLawyerDto> lawyers) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LawFirmDto that = (LawFirmDto) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName()) && Objects.equals(getCompanyStartDay(), that.getCompanyStartDay());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getCompanyStartDay());
    }
}
