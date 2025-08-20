package com.employee.EmployeePortal.repository;

import com.employee.EmployeePortal.entity.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PayslipRepository extends JpaRepository<Payslip , Long> {
    @Query("SELECT p FROM Payslip p WHERE p.employee.employeeId = :employeeId ORDER BY p.payPeriodEnd DESC")
    List<Payslip> findByEmployeeIdOrderByPayPeriodEndDesc(@Param("employeeId") String employeeId);

    @Query("SELECT p FROM Payslip p WHERE p.employee.employeeId = :employeeId AND p.payPeriodStart BETWEEN :startDate AND :endDate")
    List<Payslip> findByEmployeeIdAndPayPeriodBetween(
            @Param("employeeId") String employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
