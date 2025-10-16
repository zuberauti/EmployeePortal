package com.employee.EmployeePortal.repository;

import com.employee.EmployeePortal.entity.Employee;
import com.employee.EmployeePortal.enums.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, String>, JpaSpecificationExecutor<Employee> {

    List<Employee> findByStatus(EmployeeStatus status);

    // Add these missing methods
    Optional<Employee> findByEmployeeId(String employeeId);

    @Query("SELECT e FROM Employee e WHERE e.employeeId = :employeeId AND e.status = 'ACTIVE'")
    Optional<Employee> findActiveByEmployeeId(@Param("employeeId") String employeeId);

    List<Employee> findByHireDateIsNotNull();
}