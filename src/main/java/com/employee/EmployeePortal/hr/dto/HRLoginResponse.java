package com.employee.EmployeePortal.hr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HRLoginResponse {
    private boolean success;
    private String message;
    private  String fullName;
}
