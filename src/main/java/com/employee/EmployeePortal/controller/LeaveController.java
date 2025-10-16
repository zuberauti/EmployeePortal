package com.employee.EmployeePortal.controller;

import com.employee.EmployeePortal.dto.*;
import com.employee.EmployeePortal.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    private final LeaveService leaveService;

    @Autowired
    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @GetMapping("/types")
    public ResponseEntity<List<LeaveTypeDTO>> getLeaveTypes() {
        return ResponseEntity.ok(leaveService.getAllActiveLeaveTypes());
    }

    @PostMapping("/requests")
    public ResponseEntity<LeaveRequestResponseDTO> submitLeaveRequest(
            @RequestBody LeaveRequestDTO requestDTO,
            @RequestHeader("X-Employee-ID") String employeeId) {
        requestDTO.setEmployeeId(employeeId);
        return ResponseEntity.ok(leaveService.submitLeaveRequest(requestDTO));
    }

    @GetMapping("/requests")
    public ResponseEntity<List<LeaveRequestResponseDTO>> getEmployeeLeaveRequests(
            @RequestHeader("X-Employee-ID") String employeeId) {
        return ResponseEntity.ok(leaveService.getEmployeeLeaveRequests(employeeId));
    }

    @GetMapping("/balances")
    public ResponseEntity<List<LeaveBalanceDTO>> getEmployeeLeaveBalances(
            @RequestHeader("X-Employee-ID") String employeeId) {
        return ResponseEntity.ok(leaveService.getEmployeeLeaveBalances(employeeId));
    }

    @PostMapping("/requests/{requestId}/approve")
    public ResponseEntity<LeaveRequestResponseDTO> approveLeaveRequest(
            @PathVariable Long requestId,
            @RequestHeader("X-Employee-ID") String employeeId) {
        return ResponseEntity.ok(leaveService.approveLeaveRequest(requestId, employeeId));
    }

    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<LeaveRequestResponseDTO> rejectLeaveRequest(
            @PathVariable Long requestId,
            @RequestHeader("X-Employee-ID") String employeeId,
            @RequestParam String reason) {
        return ResponseEntity.ok(leaveService.rejectLeaveRequest(requestId, employeeId, reason));
    }

    @PostMapping("/requests/{requestId}/cancel")
    public ResponseEntity<LeaveRequestResponseDTO> cancelLeaveRequest(
            @PathVariable Long requestId,
            @RequestHeader("X-Employee-ID") String employeeId) {
        return ResponseEntity.ok(leaveService.cancelLeaveRequest(requestId, employeeId));
    }

    @PostMapping("/initialize")
    public ResponseEntity<String> initializeEmployeeLeaveBalance(
            @RequestHeader("X-Employee-ID") String employeeId) {
        leaveService.initializeEmployeeLeaveBalance(employeeId);
        return ResponseEntity.ok("Leave balance initialized for employee: " + employeeId);
    }

    @PostMapping("/credit-monthly-leaves")
    public ResponseEntity<String> creditMonthlyLeaves() {
        leaveService.creditMonthlyLeaves();
        return ResponseEntity.ok("Monthly leaves credited successfully");
    }

    @GetMapping("/allocation")
    public ResponseEntity<LeaveAllocationDTO> getAllocationDetails(
            @RequestHeader("X-Employee-ID") String employeeId,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(leaveService.getAllocationDetails(employeeId, year));
    }
}
