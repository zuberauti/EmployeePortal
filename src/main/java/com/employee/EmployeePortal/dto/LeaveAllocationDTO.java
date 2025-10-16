package com.employee.EmployeePortal.dto;

import lombok.Data;
import java.util.Map;

@Data
public class LeaveAllocationDTO {
    private int year;
    private int totalAnnualLeaves;
    private int usedAnnualLeaves;
    private int remainingAnnualLeaves;
    private Map<String, Integer> monthlyAllocation;
    private String allocationStatus;

    public LeaveAllocationDTO(int year, int totalAnnualLeaves, int usedAnnualLeaves, int remainingAnnualLeaves,
                              Map<String, Integer> monthlyAllocation, String allocationStatus) {
        this.year = year;
        this.totalAnnualLeaves = totalAnnualLeaves;
        this.usedAnnualLeaves = usedAnnualLeaves;
        this.remainingAnnualLeaves = remainingAnnualLeaves;
        this.monthlyAllocation = monthlyAllocation;
        this.allocationStatus = allocationStatus;
    }
}