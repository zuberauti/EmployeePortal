package com.employee.EmployeePortal.service;

import com.employee.EmployeePortal.dto.EmployeeDTO;
import com.employee.EmployeePortal.entity.Employee;
import com.employee.EmployeePortal.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository repository;

    private EmployeeDTO convertToDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setEmail(employee.getEmail());
        dto.setPhone(employee.getPhone());
        dto.setDateOfBirth(employee.getDateOfBirth());
        dto.setGender(employee.getGender());
        dto.setNationality(employee.getNationality());
        dto.setAddress(employee.getAddress());
        dto.setEmergencyContactName(employee.getEmergencyContactName());
        dto.setEmergencyContactPhone(employee.getEmergencyContactPhone());
        dto.setEmergencyContactRelationship(employee.getEmergencyContactRelationship());
        dto.setHireDate(employee.getHireDate());
        dto.setPosition(employee.getPosition());
        dto.setDepartment(employee.getDepartment());
        dto.setManagerId(employee.getManagerId());
        dto.setEmploymentType(employee.getEmploymentType());
        dto.setStatus(employee.getStatus());
        dto.setAvatarInitials(employee.getAvatarInitials());
        dto.setCreatedAt(employee.getCreatedAt());
        dto.setUpdatedAt(employee.getUpdatedAt());
        return dto;
    }

    private Employee convertToEntity(EmployeeDTO dto) {
        Employee employee = new Employee();
        employee.setEmployeeId(dto.getEmployeeId());
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setDateOfBirth(dto.getDateOfBirth());
        employee.setGender(dto.getGender());
        employee.setNationality(dto.getNationality());
        employee.setAddress(dto.getAddress());
        employee.setEmergencyContactName(dto.getEmergencyContactName());
        employee.setEmergencyContactPhone(dto.getEmergencyContactPhone());
        employee.setEmergencyContactRelationship(dto.getEmergencyContactRelationship());
        employee.setHireDate(dto.getHireDate());
        employee.setPosition(dto.getPosition());
        employee.setDepartment(dto.getDepartment());
        employee.setManagerId(dto.getManagerId());
        employee.setEmploymentType(dto.getEmploymentType());
        employee.setStatus(dto.getStatus());
        employee.setAvatarInitials(dto.getAvatarInitials());
        employee.setCreatedAt(dto.getCreatedAt());
        employee.setUpdatedAt(dto.getUpdatedAt());
        return employee;
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        return repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDTO getEmployeeById(String id) {
        Optional<Employee> employee = repository.findById(id);
        return employee.map(this::convertToDTO).orElse(null);
    }

    @Override
    public EmployeeDTO createEmployee(EmployeeDTO dto) {
        Employee employee = convertToEntity(dto);
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        return convertToDTO(repository.save(employee));
    }

    @Override
    public EmployeeDTO updateEmployee(String id, EmployeeDTO dto) {
        if (!repository.existsById(id)) return null;
        Employee employee = convertToEntity(dto);
        employee.setEmployeeId(id);
        employee.setUpdatedAt(LocalDateTime.now());
        return convertToDTO(repository.save(employee));
    }

    @Override
    public void deleteEmployee(String id) {
        repository.deleteById(id);
    }

    @Override
    public void updateProfilePicture(String employeeId, MultipartFile file) throws IOException{
        Optional<Employee> optional = repository.findById(employeeId);
        if(optional.isPresent()){
            Employee employee = optional.get();
            if(file != null && !file.isEmpty()){
                employee.setProfilePicture(file.getBytes());
                employee.setUpdatedAt(LocalDateTime.now());
                repository.save(employee);
            }
        } else {
            throw new RuntimeException("Employee Not Found With Id : " + employeeId);
        }
    }

    @Override
    public byte[] getProfilePicture(String employeeId) {
        Optional<Employee> optional = repository.findById(employeeId);
        return optional.map(Employee::getProfilePicture).orElse(null);
    }
}
