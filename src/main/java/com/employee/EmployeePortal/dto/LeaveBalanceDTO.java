package com.employee.EmployeePortal.dto;

import lombok.Data;

@Data
public class LeaveBalanceDTO {
    private String leaveType;
    private Integer totalDays;
    private Integer usedDays;
    private Integer remainingDays;
    private Integer year;
}