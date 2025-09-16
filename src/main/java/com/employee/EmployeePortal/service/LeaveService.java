package com.employee.EmployeePortal.service;

import com.employee.EmployeePortal.dto.*;

import java.util.List;

public interface LeaveService {
    List<LeaveTypeDTO> getAllActiveLeaveTypes();
    LeaveRequestResponseDTO submitLeaveRequest(LeaveRequestDTO requestDTO);
    List<LeaveRequestResponseDTO> getEmployeeLeaveRequests(String employeeId);
    List<LeaveBalanceDTO> getEmployeeLeaveBalances(String employeeId);
    LeaveRequestResponseDTO approveLeaveRequest(Long requestId, String approvedByEmployeeId);
    LeaveRequestResponseDTO rejectLeaveRequest(Long requestId, String rejectedByEmployeeId, String reason);
    LeaveRequestResponseDTO cancelLeaveRequest(Long requestId, String employeeId);
    void initializeEmployeeLeaveBalance(String employeeId);
    List<LeaveRequestResponseDTO> getPendingLeaveRequests();
}