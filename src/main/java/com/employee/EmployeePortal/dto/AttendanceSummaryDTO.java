package com.employee.EmployeePortal.dto;

import lombok.Data;

@Data
public class AttendanceSummaryDTO {
    private int presentDays;
    private int absentDays;
    private int lateArrivals;
}
