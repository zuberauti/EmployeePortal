package com.employee.EmployeePortal.service;

import com.employee.EmployeePortal.dto.AttendanceDTO;
import com.employee.EmployeePortal.dto.AttendanceDaySummaryDTO;
import com.employee.EmployeePortal.dto.AttendanceSummaryDTO;

import java.util.List;

public interface AttendanceService {
    List<AttendanceDTO> getAllAttendances();
    AttendanceDTO getAttendanceById(String attendanceId);
    AttendanceDTO createAttendance(AttendanceDTO attendanceDTO);
    AttendanceDTO updateAttendance(String attendanceId, AttendanceDTO attendanceDTO);
    void deleteAttendance(String attendanceId);
    AttendanceSummaryDTO getMonthlySummary(String employeeId);

    AttendanceDTO checkIn(String employeeId);
    AttendanceDTO checkOut(String employeeId);

    List<AttendanceDaySummaryDTO> getMonthlyAttendances(String employeeId);
}
