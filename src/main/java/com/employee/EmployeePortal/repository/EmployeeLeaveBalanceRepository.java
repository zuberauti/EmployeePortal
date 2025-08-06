package com.employee.EmployeePortal.repository;

import com.employee.EmployeePortal.entity.EmployeeLeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

public interface EmployeeLeaveBalanceRepository extends JpaRepository<EmployeeLeaveBalance, Long> {

    @Query("SELECT elb FROM EmployeeLeaveBalance elb WHERE elb.employee.employeeId = :employeeId AND elb.leaveType.id = :leaveTypeId AND elb.year = :year")
    Optional<EmployeeLeaveBalance> findByEmployeeIdAndLeaveTypeIdAndYear(
            @Param("employeeId") String employeeId,
            @Param("leaveTypeId") Long leaveTypeId,
            @Param("year") int year);

    @Query("SELECT elb FROM EmployeeLeaveBalance elb WHERE elb.employee.employeeId = :employeeId AND elb.year = :year")
    List<EmployeeLeaveBalance> findByEmployeeIdAndYear(
            @Param("employeeId") String employeeId,
            @Param("year") int year);
}