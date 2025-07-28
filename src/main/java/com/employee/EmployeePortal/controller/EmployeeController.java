package com.employee.EmployeePortal.controller;

import com.employee.EmployeePortal.dto.EmployeeDTO;
import com.employee.EmployeePortal.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService service;
    @Autowired
    private EmployeeService employeeService;

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

    @GetMapping
    public List<EmployeeDTO> getAll() {
        return service.getAllEmployees();
    }

    @GetMapping("/{id}")
    public EmployeeDTO getOne(@PathVariable String id) {
        return service.getEmployeeById(id);
    }

    @PostMapping("/{id}/upload-profile-image")
    public ResponseEntity<String>  uploadProfilePicture(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) throws IOException {
            try {
                service.updateProfilePicture(id, file);
                return ResponseEntity.ok("Profile Picture Uploaded Successfully");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Profile Picture Upload Failed");
            }
    }

    @GetMapping("/{id}/profile-picture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable String id) {
        byte[] image = service.getProfilePicture(id);
        if(image != null) {
            return ResponseEntity
                    .ok()
                    .header("Content-Type", "image/jpeg")
                    .body(image);
        }else {
            return ResponseEntity.notFound().build();
        }
    }
}
