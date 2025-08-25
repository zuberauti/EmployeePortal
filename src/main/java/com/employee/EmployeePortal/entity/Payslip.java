
package com.employee.EmployeePortal.entity;

import com.employee.EmployeePortal.enums.PayslipStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "payslips")
public class Payslip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payslip_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "employeeId", nullable = false)
    private Employee employee;

    private String payslipNumber;
    private LocalDate payPeriodStart;
    private LocalDate payPeriodEnd;
    private LocalDate payDate;

    @Enumerated(EnumType.STRING)
    private PayslipStatus status; // PAID, PENDING, PROCESSING, CANCELLED

    // Earnings
    private BigDecimal basicSalary;
    private BigDecimal housingAllowance;
    private BigDecimal transportAllowance;
    private BigDecimal mealAllowance;
    private BigDecimal overtimePay;
    private BigDecimal bonus;
    private BigDecimal otherAllowances;

    // Deductions
    private BigDecimal taxDeduction;
    private BigDecimal socialSecurity;
    private BigDecimal healthInsurance;
    private BigDecimal pensionContribution;
    private BigDecimal otherDeductions;

    // Totals
    private BigDecimal grossEarnings;
    private BigDecimal totalDeductions;
    private BigDecimal netPay;

    @OneToMany(mappedBy = "payslip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PayslipItem> items;

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
