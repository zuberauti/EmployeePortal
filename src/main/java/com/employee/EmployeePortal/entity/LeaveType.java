//package com.employee.EmployeePortal.entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//import java.time.LocalDateTime;
//
//@Data
//@Entity
//@Table(name = "leave_types")
//public class LeaveType {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String name;
//
//    private String description;
//
//    @Column(name = "is_paid", nullable = false)
//    private boolean isPaid;
//
//    @Column(name = "requires_approval", nullable = false)
//    private boolean requiresApproval;
//
//    @Column(name = "max_days_per_year")
//    private Integer maxDaysPerYear;
//
//    @Column(name = "is_active", nullable = false)
//    private boolean isActive = true;
//
//    @Column(name = "created_at", updatable = false)
//    private LocalDateTime createdAt;
//
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//
//    @PrePersist
//    protected void onCreate() {
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

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "leave_types")
public class LeaveType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "is_paid", nullable = false)
    private boolean isPaid;

    @Column(name = "requires_approval", nullable = false)
    private boolean requiresApproval;

    @Column(name = "max_days_per_year")
    private Integer maxDaysPerYear;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}