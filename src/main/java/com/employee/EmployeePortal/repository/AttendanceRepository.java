
package com.employee.EmployeePortal.repository;

import com.employee.EmployeePortal.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, String> {
    List<Attendance> findByEmployeeIdAndAttendanceDateBetween(String employeeId, LocalDate startDate, LocalDate endDate);

    Optional<Attendance> findByEmployeeIdAndAttendanceDate(String employeeId, LocalDate date);
    List<Attendance> findByEmployeeId(String employeeId);

    long countByAttendanceDateBetween(LocalDate start, LocalDate end);

    long countByStatusAndAttendanceDateBetween(String status, LocalDate start, LocalDate end);
}