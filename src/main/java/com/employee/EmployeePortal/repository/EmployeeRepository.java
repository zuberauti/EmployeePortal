package com.employee.EmployeePortal.repository;

import com.employee.EmployeePortal.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
    // Additional query methods if needed
}
