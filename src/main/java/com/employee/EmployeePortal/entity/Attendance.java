package com.employee.EmployeePortal.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "attendance")
public class Attendance {

    @Id
    private String attendanceId;

    private String employeeId;

    private LocalDate attendanceDate;

    private String status;

    private LocalTime checkInTime;

    private LocalTime checkOutTime;

    private String remarks;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
