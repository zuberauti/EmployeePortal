package com.employee.EmployeePortal.hr.controller;

import com.employee.EmployeePortal.dto.LeaveRequestResponseDTO;
import com.employee.EmployeePortal.hr.dto.HrDashbordResponse;
import com.employee.EmployeePortal.hr.service.HRDashbordService;
import com.employee.EmployeePortal.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hr/dashboard")
@RequiredArgsConstructor
public class HRDashbordController {
    private final HRDashbordService hrDashbordService;
    private final LeaveService leaveService;

    @GetMapping
    public ResponseEntity<HrDashbordResponse> getDashbord(){
        HrDashbordResponse response = hrDashbordService.getDashbordData();
        return  ResponseEntity.ok(response);
    }

    @GetMapping("/pending-leave-requests")
    public List<LeaveRequestResponseDTO> getPendingLeaveRequests(){
        return leaveService.getPendingLeaveRequests();
    }
}
