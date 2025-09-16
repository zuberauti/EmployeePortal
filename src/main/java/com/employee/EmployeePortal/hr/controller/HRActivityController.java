package com.employee.EmployeePortal.hr.controller;

import com.employee.EmployeePortal.hr.entity.HRActivity;
import com.employee.EmployeePortal.hr.service.HRActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hr/activities")
public class HRActivityController {

    public final HRActivityService service;

    public HRActivityController(HRActivityService service){
        this.service = service;
    }

    @GetMapping("/recentActivities")
    public ResponseEntity<List<HRActivity>> getRecentActivities(){
        return ResponseEntity.ok(service.getRecentActivities());
    }

}
