package com.employee.EmployeePortal.controller;

import com.employee.EmployeePortal.dto.EmployeeDTO;
import com.employee.EmployeePortal.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService service;

    @GetMapping
    public List<EmployeeDTO> getAll() {
        return service.getAllEmployees();
    }

    @GetMapping("/{id}")
    public EmployeeDTO getOne(@PathVariable String id) {
        return service.getEmployeeById(id);
    }

    @PostMapping
    public EmployeeDTO create(@RequestBody EmployeeDTO dto) {
        return service.createEmployee(dto);
    }

    @PutMapping("/{id}")
    public EmployeeDTO update(@PathVariable String id, @RequestBody EmployeeDTO dto) {
        return service.updateEmployee(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.deleteEmployee(id);
    }
}
