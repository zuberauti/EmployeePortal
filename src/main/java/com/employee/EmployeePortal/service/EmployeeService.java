package com.employee.EmployeePortal.service;

import com.employee.EmployeePortal.dto.EmployeeDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EmployeeService {
    List<EmployeeDTO> getAllEmployees();
    EmployeeDTO getEmployeeById(String id);
    EmployeeDTO createEmployee(EmployeeDTO dto);
    EmployeeDTO updateEmployee(String id, EmployeeDTO dto);
    void deleteEmployee(String id);
    void  updateProfilePicture(String employeeId, MultipartFile file) throws IOException;
    byte[] getProfilePicture(String employeeId);
    List<EmployeeDTO> filterEmployees(String department, String position, String status, String employmentType, String managerId, String name);

}
