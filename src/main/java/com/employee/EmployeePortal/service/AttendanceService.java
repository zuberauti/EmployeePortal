package com.employee.EmployeePortal.service;

import com.employee.EmployeePortal.dto.AttendanceDTO;
import com.employee.EmployeePortal.dto.AttendanceDaySummaryDTO;
import com.employee.EmployeePortal.dto.AttendanceSummaryDTO;

import java.util.List;

public interface AttendanceService {
    List<AttendanceDTO> getAllAttendances();
    AttendanceDTO getAttendanceById(String attendanceId); //employee id
    List<AttendanceDTO> getAttendancesByEmployeeId(String employeeId);//getting the specific employee all attendance
    AttendanceDTO createAttendance(AttendanceDTO attendanceDTO);
    AttendanceDTO updateAttendance(String attendanceId, AttendanceDTO attendanceDTO);
    AttendanceSummaryDTO getMonthlySummary(String employeeId);//quartely early half quarter

    AttendanceDTO checkIn(String employeeId);
    AttendanceDTO checkOut(String employeeId);

    List<AttendanceDaySummaryDTO> getMonthlyAttendances(String employeeId);
}
