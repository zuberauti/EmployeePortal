package com.employee.EmployeePortal.service;

import com.employee.EmployeePortal.dto.EmployeeDTO;

import java.util.List;

public interface EmployeeService {
    List<EmployeeDTO> getAllEmployees();
    EmployeeDTO getEmployeeById(String id);
    EmployeeDTO createEmployee(EmployeeDTO dto);
    EmployeeDTO updateEmployee(String id, EmployeeDTO dto);
    void deleteEmployee(String id);
}
