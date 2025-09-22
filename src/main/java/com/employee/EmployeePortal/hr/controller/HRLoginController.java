package com.employee.EmployeePortal.hr.controller;

import com.employee.EmployeePortal.hr.dto.HRLoginRequest;
import com.employee.EmployeePortal.hr.dto.HRLoginResponse;
import com.employee.EmployeePortal.hr.repository.HRUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/hr")
public class HRLoginController {

    private final HRUserRepository hrUserRepository;

    public HRLoginController(HRUserRepository hrUserRepository){
        this.hrUserRepository = hrUserRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<HRLoginResponse> login(@RequestBody HRLoginRequest request){

        return  hrUserRepository.findByUsernameAndPassword(request.getUsername(),request.getPassword())
                .map(user -> ResponseEntity.ok(new HRLoginResponse(true,"Login Successful", user.getUsername())))
                .orElseGet(() -> ResponseEntity.status(401).body(new HRLoginResponse(false, "Invalid Credentials", null)));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Since we don’t have sessions/JWT, just return success message
        return ResponseEntity.ok(Map.of(
                "message", "Logout successful"
        ));
    }
}
