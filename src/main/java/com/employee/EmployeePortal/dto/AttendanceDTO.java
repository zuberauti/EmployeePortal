package com.employee.EmployeePortal.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class AttendanceDTO {
    private String attendanceId;
    private String employeeId;
    private LocalDate attendanceDate;
    private String status;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private String remarks;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
