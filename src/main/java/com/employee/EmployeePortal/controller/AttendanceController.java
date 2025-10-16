

package com.employee.EmployeePortal.controller;

import com.employee.EmployeePortal.dto.AttendanceDTO;
import com.employee.EmployeePortal.dto.AttendanceDaySummaryDTO;
import com.employee.EmployeePortal.dto.AttendanceSummaryDTO;
import com.employee.EmployeePortal.service.AttendanceService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin
public class AttendanceController {

    @Autowired
   private AttendanceService attendanceService;


    @PostMapping
    public AttendanceDTO createAttendance(@RequestBody AttendanceDTO dto) {
        return attendanceService.createAttendance(dto);
    }

    @GetMapping
    public List<AttendanceDTO> getAllAttendances() {
        return attendanceService.getAllAttendances();
    }

    @GetMapping("/{attendanceId}")
    public AttendanceDTO getAttendanceById(@PathVariable String attendanceId) {
        return attendanceService.getAttendanceById(attendanceId);
    }

    @GetMapping("/employee/{employeeId}")
    public List<AttendanceDTO> getAttendancesByEmployeeId(@PathVariable String employeeId) {
        return attendanceService.getAttendancesByEmployeeId(employeeId);
    }

    @PutMapping("/{attendanceId}")
    public AttendanceDTO updateAttendance(@PathVariable String attendanceId, @RequestBody AttendanceDTO dto) {
        return attendanceService.updateAttendance(attendanceId, dto);
    }

    @GetMapping("/summary/{employeeId}")
    public AttendanceSummaryDTO getMonthlySummary(@PathVariable String employeeId) {
        return attendanceService.getMonthlySummary(employeeId);
    }

    @GetMapping("/month/{employeeId}")
    public List<AttendanceDaySummaryDTO> getMonthlyAttendances(@PathVariable String employeeId) {
        return attendanceService.getMonthlyAttendances(employeeId);
    }

    @PostMapping("/check-in/{employeeId}")
    public AttendanceDTO checkIn(@PathVariable String employeeId) {
        return attendanceService.checkIn(employeeId);
    }

    @PostMapping("/check-out/{employeeId}")
    public AttendanceDTO checkOut(@PathVariable String employeeId) {
        return attendanceService.checkOut(employeeId);
    }


    @GetMapping("/export")
    public ResponseEntity<ByteArrayResource> exportAttendance() throws IOException {
        byte[] data = attendanceService.exportAttendanceToBytes();
        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attendance.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(data.length)
                .body(resource);
    }


}
