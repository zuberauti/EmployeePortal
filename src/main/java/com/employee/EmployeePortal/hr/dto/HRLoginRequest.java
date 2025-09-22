package com.employee.EmployeePortal.hr.dto;

import lombok.Data;

@Data
public class HRLoginRequest {
    private String username;
    private String password;
}
