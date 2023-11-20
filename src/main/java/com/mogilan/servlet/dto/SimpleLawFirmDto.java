package com.mogilan.servlet.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDate;
import java.util.List;

public class SimpleLawFirmDto {
    private Long id;
    private String name;
    private LocalDate companyStartDay;
    @JsonManagedReference
    private List<LawyerDto> lawyers;
}
