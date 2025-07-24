package com.employee.EmployeePortal.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EmployeeDTO {
    private String employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private String nationality;
    private String address;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;
    private LocalDate hireDate;
    private String position;
    private String department;
    private String managerId;
    private String employmentType;
    private String status;
    private String avatarInitials;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
