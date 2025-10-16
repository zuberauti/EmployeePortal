package com.employee.EmployeePortal.dto;

import lombok.Data;

@Data
public class LeaveTypeDTO {

    private Long id;
    private String name;
    private String description;
    private boolean paid;
    private boolean requiresApproval;
    private int maxDaysPerYear;
    private boolean active;

}
