package com.employee.EmployeePortal.repository;

import com.employee.EmployeePortal.entity.LeaveRequest;
import com.employee.EmployeePortal.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.employeeId = :employeeId ORDER BY lr.startDate DESC")
    List<LeaveRequest> findByEmployeeIdOrderByStartDateDesc(@Param("employeeId") String employeeId);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.employeeId = :employeeId AND lr.status = :status")
    List<LeaveRequest> findByEmployeeIdAndStatus(
            @Param("employeeId") String employeeId,
            @Param("status") LeaveStatus status);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.employeeId = :employeeId AND lr.startDate BETWEEN :start AND :end")
    List<LeaveRequest> findByEmployeeIdAndStartDateBetween(
            @Param("employeeId") String employeeId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT CASE WHEN COUNT(lr) > 0 THEN true ELSE false END " +
            "FROM LeaveRequest lr " +
            "WHERE lr.employee.employeeId = :employeeId " +
            "AND lr.leaveType.id = :leaveTypeId " +
            "AND lr.status <> :status " +
            "AND ((lr.startDate BETWEEN :startDate AND :endDate) " +
            "OR (lr.endDate BETWEEN :startDate AND :endDate) " +
            "OR (lr.startDate <= :startDate AND lr.endDate >= :endDate))")
    boolean hasOverlappingLeaveRequest(
            @Param("employeeId") String employeeId,
            @Param("leaveTypeId") Long leaveTypeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") LeaveStatus status);

    long countByStatus(LeaveStatus status);
    List<LeaveRequest> findByStatusOrderByRequestDateDesc(LeaveStatus status);
}