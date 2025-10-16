//package com.employee.EmployeePortal.entity;
//
//import com.employee.EmployeePortal.enums.LeaveStatus;
//import jakarta.persistence.*;
//import lombok.Data;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//@Data
//@Entity
//@Table(name = "leave_requests")
//public class LeaveRequest {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "employee_id", referencedColumnName = "employeeId", nullable = false)
//    private Employee employee;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "leave_type_id", nullable = false)
//    private LeaveType leaveType;
//
//    @Column(name = "start_date", nullable = false)
//    private LocalDate startDate;
//
//    @Column(name = "end_date", nullable = false)
//    private LocalDate endDate;
//
//    @Column(name = "duration_days", nullable = false)
//    private Integer durationDays;
//
//    private String reason;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private LeaveStatus status = LeaveStatus.PENDING;
//
//    @Column(name = "request_date")
//    private LocalDateTime requestDate;
//
//    @Column(name = "processed_date")
//    private LocalDateTime processedDate;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "processed_by_id")
//    private Employee processedBy;
//
//    @Column(name = "rejection_reason")
//    private String rejectionReason;
//
//    @Column(name = "created_at", updatable = false)
//    private LocalDateTime createdAt;
//
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//
//    @PrePersist
//    protected void onCreate() {
//        requestDate = LocalDateTime.now();
//        createdAt = LocalDateTime.now();
//        updatedAt = LocalDateTime.now();
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = LocalDateTime.now();
//    }
//}
package com.employee.EmployeePortal.entity;

import com.employee.EmployeePortal.enums.LeaveStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "leave_requests")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "employeeId", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveStatus status = LeaveStatus.PENDING;

    @Column(name = "request_date")
    private LocalDateTime requestDate;

    @Column(name = "processed_date")
    private LocalDateTime processedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by_id")
    private Employee processedBy;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        requestDate = LocalDateTime.now();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}