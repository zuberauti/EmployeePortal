package com.employee.EmployeePortal.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AttendanceDaySummaryDTO {
    private LocalDate date;
    private String status;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private String totalHoursWorked;
    private boolean late;
}
