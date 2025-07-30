
package com.employee.EmployeePortal.service;

import com.employee.EmployeePortal.dto.AttendanceDTO;
import com.employee.EmployeePortal.dto.AttendanceDaySummaryDTO;
import com.employee.EmployeePortal.dto.AttendanceSummaryDTO;
import com.employee.EmployeePortal.entity.Attendance;
import com.employee.EmployeePortal.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRepository repository;

    private AttendanceDTO toDTO(Attendance entity) {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setAttendanceId(entity.getAttendanceId());
        dto.setEmployeeId(entity.getEmployeeId());
        dto.setAttendanceDate(entity.getAttendanceDate());
        dto.setStatus(entity.getStatus());
        dto.setCheckInTime(entity.getCheckInTime());
        dto.setCheckOutTime(entity.getCheckOutTime());
        dto.setRemarks(entity.getRemarks());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setUpdatedDate(entity.getUpdatedDate());
        return dto;
    }

    private Attendance toEntity(AttendanceDTO dto) {
        Attendance entity = new Attendance();
        entity.setAttendanceId(dto.getAttendanceId());
        entity.setEmployeeId(dto.getEmployeeId());
        entity.setAttendanceDate(dto.getAttendanceDate());
        entity.setStatus(dto.getStatus());
        entity.setCheckInTime(dto.getCheckInTime());
        entity.setCheckOutTime(dto.getCheckOutTime());
        entity.setRemarks(dto.getRemarks());
        entity.setCreatedDate(dto.getCreatedDate());
        entity.setUpdatedDate(dto.getUpdatedDate());
        return entity;
    }

    @Override
    public List<AttendanceDTO> getAllAttendances() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public AttendanceDTO getAttendanceById(String attendanceId) {
        return repository.findById(attendanceId).map(this::toDTO).orElse(null);
    }

    @Override
    public AttendanceDTO createAttendance(AttendanceDTO attendanceDTO) {
        Attendance entity = toEntity(attendanceDTO);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedDate(now);
        entity.setUpdatedDate(now);
        return toDTO(repository.save(entity));
    }

    @Override
    public AttendanceDTO updateAttendance(String attendanceId, AttendanceDTO attendanceDTO) {
        if (!repository.existsById(attendanceId)) return null;
        Attendance entity = toEntity(attendanceDTO);
        entity.setAttendanceId(attendanceId);
        entity.setUpdatedDate(LocalDateTime.now());
        return toDTO(repository.save(entity));
    }

    @Override
    public void deleteAttendance(String attendanceId) {
        repository.deleteById(attendanceId);
    }

    @Override
    public AttendanceSummaryDTO getMonthlySummary(String employeeId) {
        LocalDate now = LocalDate.now();
        LocalDate start = now.withDayOfMonth(1);
        List<Attendance> records = repository.findByEmployeeIdAndAttendanceDateBetween(employeeId, start, now);

        int present = 0, absent = 0, late = 0;
        for (Attendance att : records) {
            if ("Present".equalsIgnoreCase(att.getStatus())) {
                present++;
                if (att.getCheckInTime() != null && att.getCheckInTime().isAfter(LocalTime.of(9, 15))) {
                    late++;
                }
            } else if ("Absent".equalsIgnoreCase(att.getStatus())) {
                absent++;
            }
        }

        AttendanceSummaryDTO summary = new AttendanceSummaryDTO();
        summary.setPresentDays(present);
        summary.setAbsentDays(absent);
        summary.setLateArrivals(late);
        return summary;
    }

    @Override
    public AttendanceDTO checkIn(String employeeId) {
        LocalDate today = LocalDate.now();
        Optional<Attendance> existing = repository.findByEmployeeIdAndAttendanceDate(employeeId, today);

        if (existing.isPresent()) throw new RuntimeException("Already checked in today.");

        Attendance attendance = new Attendance();
        attendance.setAttendanceId("ATT-" + System.currentTimeMillis());
        attendance.setEmployeeId(employeeId);
        attendance.setAttendanceDate(today);
        attendance.setCheckInTime(LocalTime.now());
        attendance.setStatus("Present");
        attendance.setCreatedDate(LocalDateTime.now());
        attendance.setUpdatedDate(LocalDateTime.now());

        return toDTO(repository.save(attendance));
    }

    @Override
    public AttendanceDTO checkOut(String employeeId) {
        LocalDate today = LocalDate.now();
        Optional<Attendance> existing = repository.findByEmployeeIdAndAttendanceDate(employeeId, today);

        if (existing.isEmpty()) throw new RuntimeException("No check-in found today.");

        Attendance attendance = existing.get();
        if (attendance.getCheckOutTime() != null) throw new RuntimeException("Already checked out.");

        attendance.setCheckOutTime(LocalTime.now());
        attendance.setUpdatedDate(LocalDateTime.now());

        return toDTO(repository.save(attendance));
    }

    @Override
    public List<AttendanceDaySummaryDTO> getMonthlyAttendances(String employeeId) {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);

        List<Attendance> records = repository.findByEmployeeIdAndAttendanceDateBetween(employeeId, startOfMonth, now);

        return records.stream().map(att -> {
            AttendanceDaySummaryDTO dto = new AttendanceDaySummaryDTO();
            dto.setDate(att.getAttendanceDate());
            dto.setCheckInTime(att.getCheckInTime());
            dto.setCheckOutTime(att.getCheckOutTime());
            dto.setStatus(att.getStatus());

            if (att.getCheckInTime() != null && att.getCheckOutTime() != null) {
                long hours = java.time.Duration.between(att.getCheckInTime(), att.getCheckOutTime()).toHours();
                long minutes = java.time.Duration.between(att.getCheckInTime(), att.getCheckOutTime()).toMinutes() % 60;
                dto.setTotalHoursWorked(hours + "h " + minutes + "m");
            } else {
                dto.setTotalHoursWorked("N/A");
            }

            dto.setLate(att.getCheckInTime() != null && att.getCheckInTime().isAfter(LocalTime.of(9, 15)));

            return dto;
        }).collect(Collectors.toList());
    }

}