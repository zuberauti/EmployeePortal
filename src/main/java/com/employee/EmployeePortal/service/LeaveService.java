//package com.employee.EmployeePortal.service;
//
//import com.employee.EmployeePortal.dto.*;
//
//import java.util.List;
//
//public interface LeaveService {
//    List<LeaveTypeDTO> getAllActiveLeaveTypes();
//    LeaveRequestResponseDTO submitLeaveRequest(LeaveRequestDTO requestDTO);
//    List<LeaveRequestResponseDTO> getEmployeeLeaveRequests(String employeeId);
//    List<LeaveBalanceDTO> getEmployeeLeaveBalances(String employeeId);
//    LeaveRequestResponseDTO approveLeaveRequest(Long requestId, String approvedByEmployeeId);
//    LeaveRequestResponseDTO rejectLeaveRequest(Long requestId, String rejectedByEmployeeId, String reason);
//    LeaveRequestResponseDTO cancelLeaveRequest(Long requestId, String employeeId);
//    void initializeEmployeeLeaveBalance(String employeeId);
//    List<LeaveRequestResponseDTO> getPendingLeaveRequests();
//}


package com.employee.EmployeePortal.service;

import com.employee.EmployeePortal.dto.*;
import java.util.List;
import java.util.Map;

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
    void creditMonthlyLeaves();
    Map<String, Object> getMonthlyAllocationStatus(String employeeId);
    LeaveAllocationDTO getAllocationDetails(String employeeId, Integer year);
}
