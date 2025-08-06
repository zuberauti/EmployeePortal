package com.employee.EmployeePortal.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LeaveRequestResponseDTO {
    private Long id;
    private String employeeId;
    private String employeeName;
    private String leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer durationDays;
    private String reason;
    private String status;
    private LocalDateTime requestDate;
    private LocalDateTime processedDate;
    private String processedByName;
    private String rejectionReason;
}