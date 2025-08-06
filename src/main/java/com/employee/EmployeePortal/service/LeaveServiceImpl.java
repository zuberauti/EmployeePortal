package com.employee.EmployeePortal.service;

import com.employee.EmployeePortal.dto.*;
import com.employee.EmployeePortal.entity.*;
import com.employee.EmployeePortal.enums.LeaveStatus;
import com.employee.EmployeePortal.exception.LeaveRequestConflictException;
import com.employee.EmployeePortal.exception.InsufficientLeaveBalanceException;
import com.employee.EmployeePortal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final EmployeeLeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public LeaveServiceImpl(LeaveRequestRepository leaveRequestRepository,
                            LeaveTypeRepository leaveTypeRepository,
                            EmployeeLeaveBalanceRepository leaveBalanceRepository,
                            EmployeeRepository employeeRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<LeaveTypeDTO> getAllActiveLeaveTypes() {
        return leaveTypeRepository.findByIsActiveTrue().stream()
                .map(this::convertToLeaveTypeDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LeaveRequestResponseDTO submitLeaveRequest(LeaveRequestDTO requestDTO) {
        // Validate employee exists
        Employee employee = employeeRepository.findById(requestDTO.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Validate leave type exists
        LeaveType leaveType = leaveTypeRepository.findById(requestDTO.getLeaveTypeId())
                .orElseThrow(() -> new RuntimeException("Leave type not found"));

        // Validate dates
        if (requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        // Calculate duration
        int duration = (int) ChronoUnit.DAYS.between(requestDTO.getStartDate(), requestDTO.getEndDate()) + 1;

        // Check for overlapping leave requests
        if (leaveRequestRepository.hasOverlappingLeaveRequest(
                requestDTO.getEmployeeId(),
                requestDTO.getLeaveTypeId(),
                requestDTO.getStartDate(),
                requestDTO.getEndDate(),
                LeaveStatus.REJECTED)) {
            throw new LeaveRequestConflictException("You already have a pending or approved leave request for this period");
        }

        // For paid leave types, check balance
        if (leaveType.isPaid()) {
            checkLeaveBalance(requestDTO.getEmployeeId(), requestDTO.getLeaveTypeId(), duration);
        }

        // Create and save leave request
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStartDate(requestDTO.getStartDate());
        leaveRequest.setEndDate(requestDTO.getEndDate());
        leaveRequest.setDurationDays(duration);
        leaveRequest.setReason(requestDTO.getReason());
        leaveRequest.setStatus(LeaveStatus.PENDING);

        leaveRequest = leaveRequestRepository.save(leaveRequest);

        return convertToLeaveRequestResponseDTO(leaveRequest);
    }

    @Override
    public List<LeaveRequestResponseDTO> getEmployeeLeaveRequests(String employeeId) {
        return leaveRequestRepository.findByEmployeeIdOrderByStartDateDesc(employeeId).stream()
                .map(this::convertToLeaveRequestResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveBalanceDTO> getEmployeeLeaveBalances(String employeeId) {
        int currentYear = LocalDate.now().getYear();
        return leaveBalanceRepository.findByEmployeeIdAndYear(employeeId, currentYear).stream()
                .map(this::convertToLeaveBalanceDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LeaveRequestResponseDTO approveLeaveRequest(Long requestId, String approvedByEmployeeId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        Employee approvedBy = employeeRepository.findById(approvedByEmployeeId)
                .orElseThrow(() -> new RuntimeException("Approver not found"));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Only pending leave requests can be approved");
        }

        // For paid leave types, deduct from balance
        if (leaveRequest.getLeaveType().isPaid()) {
            updateLeaveBalance(leaveRequest, false);
        }

        leaveRequest.setStatus(LeaveStatus.APPROVED);
        leaveRequest.setProcessedBy(approvedBy);
        leaveRequest.setProcessedDate(LocalDateTime.now());

        leaveRequest = leaveRequestRepository.save(leaveRequest);

        return convertToLeaveRequestResponseDTO(leaveRequest);
    }

    @Override
    @Transactional
    public LeaveRequestResponseDTO rejectLeaveRequest(Long requestId, String rejectedByEmployeeId, String reason) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        Employee rejectedBy = employeeRepository.findById(rejectedByEmployeeId)
                .orElseThrow(() -> new RuntimeException("Rejector not found"));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Only pending leave requests can be rejected");
        }

        leaveRequest.setStatus(LeaveStatus.REJECTED);
        leaveRequest.setProcessedBy(rejectedBy);
        leaveRequest.setProcessedDate(LocalDateTime.now());
        leaveRequest.setRejectionReason(reason);

        leaveRequest = leaveRequestRepository.save(leaveRequest);

        return convertToLeaveRequestResponseDTO(leaveRequest);
    }

    @Override
    @Transactional
    public void initializeEmployeeLeaveBalance(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        int currentYear = LocalDate.now().getYear();
        List<LeaveType> activeLeaveType = leaveTypeRepository.findByIsActiveTrue();

        for (LeaveType leaveType : activeLeaveType) {
            // Skip if balance already exists
            if (leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(
                    employeeId, leaveType.getId(), currentYear).isPresent()) {
                continue;
            }

            // Create new balance record
            EmployeeLeaveBalance balance = new EmployeeLeaveBalance();
            balance.setEmployee(employee);
            balance.setLeaveType(leaveType);
            balance.setYear(currentYear);
            balance.setTotalDays(leaveType.getMaxDaysPerYear() != null ?
                    leaveType.getMaxDaysPerYear() : 0);
            balance.setUsedDays(0);
            balance.setRemainingDays(balance.getTotalDays());

            leaveBalanceRepository.save(balance);
        }
    }

    @Override
    @Transactional
    public LeaveRequestResponseDTO cancelLeaveRequest(Long requestId, String employeeId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        if (!leaveRequest.getEmployee().getEmployeeId().equals(employeeId)) {
            throw new RuntimeException("You can only cancel your own leave requests");
        }

        if (leaveRequest.getStatus() != LeaveStatus.PENDING && leaveRequest.getStatus() != LeaveStatus.APPROVED) {
            throw new RuntimeException("Only pending or approved leave requests can be cancelled");
        }

        // If approved and paid, return the days to balance
        if (leaveRequest.getStatus() == LeaveStatus.APPROVED && leaveRequest.getLeaveType().isPaid()) {
            updateLeaveBalance(leaveRequest, true);
        }

        leaveRequest.setStatus(LeaveStatus.CANCELLED);
        leaveRequest.setProcessedDate(LocalDateTime.now());

        leaveRequest = leaveRequestRepository.save(leaveRequest);

        return convertToLeaveRequestResponseDTO(leaveRequest);
    }

    private void checkLeaveBalance(String employeeId, Long leaveTypeId, int requestedDays) {
        int currentYear = LocalDate.now().getYear();

        Optional<EmployeeLeaveBalance> balanceOpt = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, currentYear);

        if (balanceOpt.isEmpty()) {
            // Initialize balance if not found
            initializeEmployeeLeaveBalance(employeeId);
            balanceOpt = leaveBalanceRepository
                    .findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, currentYear);
        }

        EmployeeLeaveBalance balance = balanceOpt.orElseThrow(() ->
                new RuntimeException("Failed to initialize leave balance"));

        if (balance.getRemainingDays() < requestedDays) {
            throw new InsufficientLeaveBalanceException(
                    String.format("Insufficient leave balance. Available: %d, Requested: %d",
                            balance.getRemainingDays(), requestedDays));
        }
    }

    private void updateLeaveBalance(LeaveRequest leaveRequest, boolean isCancellation) {
        int currentYear = LocalDate.now().getYear();
        EmployeeLeaveBalance balance = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndYear(
                        leaveRequest.getEmployee().getEmployeeId(),
                        leaveRequest.getLeaveType().getId(),
                        currentYear)
                .orElseThrow(() -> new RuntimeException("Leave balance not found"));

        if (isCancellation) {
            balance.setUsedDays(balance.getUsedDays() - leaveRequest.getDurationDays());
            balance.setRemainingDays(balance.getRemainingDays() + leaveRequest.getDurationDays());
        } else {
            balance.setUsedDays(balance.getUsedDays() + leaveRequest.getDurationDays());
            balance.setRemainingDays(balance.getRemainingDays() - leaveRequest.getDurationDays());
        }

        leaveBalanceRepository.save(balance);
    }

    private LeaveTypeDTO convertToLeaveTypeDTO(LeaveType leaveType) {
        LeaveTypeDTO dto = new LeaveTypeDTO();
        dto.setId(leaveType.getId());
        dto.setName(leaveType.getName());
        dto.setDescription(leaveType.getDescription());
        dto.setPaid(leaveType.isPaid());
        dto.setRequiresApproval(leaveType.isRequiresApproval());
        dto.setMaxDaysPerYear(leaveType.getMaxDaysPerYear());
        dto.setActive(leaveType.isActive());
        return dto;
    }

    private LeaveRequestResponseDTO convertToLeaveRequestResponseDTO(LeaveRequest leaveRequest) {
        LeaveRequestResponseDTO dto = new LeaveRequestResponseDTO();
        dto.setId(leaveRequest.getId());
        dto.setEmployeeId(leaveRequest.getEmployee().getEmployeeId());
        dto.setEmployeeName(leaveRequest.getEmployee().getFirstName() + " " + leaveRequest.getEmployee().getLastName());
        dto.setLeaveType(leaveRequest.getLeaveType().getName());
        dto.setStartDate(leaveRequest.getStartDate());
        dto.setEndDate(leaveRequest.getEndDate());
        dto.setDurationDays(leaveRequest.getDurationDays());
        dto.setReason(leaveRequest.getReason());
        dto.setStatus(leaveRequest.getStatus().name());
        dto.setRequestDate(leaveRequest.getRequestDate());
        dto.setProcessedDate(leaveRequest.getProcessedDate());
        dto.setRejectionReason(leaveRequest.getRejectionReason());

        if (leaveRequest.getProcessedBy() != null) {
            dto.setProcessedByName(leaveRequest.getProcessedBy().getFirstName() + " " +
                    leaveRequest.getProcessedBy().getLastName());
        }

        return dto;
    }

    private LeaveBalanceDTO convertToLeaveBalanceDTO(EmployeeLeaveBalance balance) {
        LeaveBalanceDTO dto = new LeaveBalanceDTO();
        dto.setLeaveType(balance.getLeaveType().getName());
        dto.setTotalDays(balance.getTotalDays());
        dto.setUsedDays(balance.getUsedDays());
        dto.setRemainingDays(balance.getRemainingDays());
        dto.setYear(balance.getYear());
        return dto;
    }
}