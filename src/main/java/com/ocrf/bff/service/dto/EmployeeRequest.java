package com.ocrf.bff.service.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeRequest {

    private Long orgeh;

    private LocalDate begDate;

    private LocalDate endDate;

}

