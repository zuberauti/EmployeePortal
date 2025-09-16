package com.employee.EmployeePortal.hr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HrDashbordResponse {
    private long totalEmployees;
    private long pendingLeaves;
    private double attendanceRate; // add in percentage
    private long openTasks;
}
