package com.mogilan.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class LawFirm {
    private Long id;
    private String name;
    private LocalDate companyStartDay;
    private List<Lawyer> lawyers;

    public LawFirm() {
    }

    public LawFirm(String name, LocalDate companyStartDay) {
        this.name = name;
        this.companyStartDay = companyStartDay;
    }

    public LawFirm(Long id, String name, LocalDate companyStartDay) {
        this.id = id;
        this.name = name;
        this.companyStartDay = companyStartDay;
    }

    public LawFirm(String name, LocalDate companyStartDay, List<Lawyer> lawyers) {
        this.name = name;
        this.companyStartDay = companyStartDay;
        this.lawyers = lawyers;
    }

    public LawFirm(Long id, String name, LocalDate companyStartDay, List<Lawyer> lawyers) {
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

    public List<Lawyer> getLawyers() {
        return lawyers;
    }

    public void setLawyers(List<Lawyer> lawyers) {
        this.lawyers = lawyers;
    }

    @Override
    public String toString() {
        return "LawFirm{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", companyStartDay=" + companyStartDay +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LawFirm lawFirm = (LawFirm) o;
        return Objects.equals(getId(), lawFirm.getId()) && Objects.equals(getName(), lawFirm.getName()) && Objects.equals(getCompanyStartDay(), lawFirm.getCompanyStartDay());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getCompanyStartDay());
    }
}
