package com.employee.EmployeePortal.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "employees")
public class Employee {

    @Id
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

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] profilePicture;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
