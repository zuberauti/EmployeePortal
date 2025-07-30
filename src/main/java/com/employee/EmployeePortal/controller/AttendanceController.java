package com.employee.EmployeePortal.controller;

import com.employee.EmployeePortal.dto.AttendanceDTO;
import com.employee.EmployeePortal.dto.AttendanceDaySummaryDTO;
import com.employee.EmployeePortal.dto.AttendanceSummaryDTO;
import com.employee.EmployeePortal.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping
    public List<AttendanceDTO> getAllAttendances() {
        return attendanceService.getAllAttendances();
    }

    @GetMapping("/{attendanceId}")
    public AttendanceDTO getAttendanceById(@PathVariable String attendanceId) {
        return attendanceService.getAttendanceById(attendanceId);
    }

    @PostMapping
    public AttendanceDTO createAttendance(@RequestBody AttendanceDTO attendanceDTO) {
        return attendanceService.createAttendance(attendanceDTO);
    }

    @PutMapping("/{attendanceId}")
    public AttendanceDTO updateAttendance(@PathVariable String attendanceId, @RequestBody AttendanceDTO attendanceDTO) {
        return attendanceService.updateAttendance(attendanceId, attendanceDTO);
    }

    @DeleteMapping("/{attendanceId}")
    public void deleteAttendance(@PathVariable String attendanceId) {
        attendanceService.deleteAttendance(attendanceId);
    }

    @GetMapping("/summary/{employeeId}")
    public AttendanceSummaryDTO getMonthlySummary(@PathVariable String employeeId) {
        return attendanceService.getMonthlySummary(employeeId);
    }


    @PostMapping("/check-in")
    public AttendanceDTO checkIn(@RequestParam String employeeId) {
        return attendanceService.checkIn(employeeId);
    }

    @PostMapping("/check-out")
    public AttendanceDTO checkOut(@RequestParam String employeeId) {
        return attendanceService.checkOut(employeeId);
    }

    @GetMapping("/monthlyAttendance/{employeeId}")
    public List<AttendanceDaySummaryDTO> getMonthlyAttendances(@PathVariable String employeeId) {
        return attendanceService.getMonthlyAttendances(employeeId);
    }
}