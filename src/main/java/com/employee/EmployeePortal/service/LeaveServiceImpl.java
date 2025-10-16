package com.employee.EmployeePortal.service;

import com.employee.EmployeePortal.dto.*;
import com.employee.EmployeePortal.entity.*;
import com.employee.EmployeePortal.enums.LeaveStatus;
import com.employee.EmployeePortal.enums.EmployeeStatus;
import com.employee.EmployeePortal.exception.LeaveRequestConflictException;
import com.employee.EmployeePortal.exception.InsufficientLeaveBalanceException;
import com.employee.EmployeePortal.hr.service.HRActivityService;
import com.employee.EmployeePortal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final EmployeeLeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository employeeRepository;
    private final HRActivityService hrActivityService;

    @Autowired
    public LeaveServiceImpl(LeaveRequestRepository leaveRequestRepository,
                            LeaveTypeRepository leaveTypeRepository,
                            EmployeeLeaveBalanceRepository leaveBalanceRepository,
                            EmployeeRepository employeeRepository,
                            HRActivityService hrActivityService) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.employeeRepository = employeeRepository;
        this.hrActivityService = hrActivityService;
    }

    // Monthly allocation configuration
    private final Map<Month, Integer> MONTHLY_ALLOCATION = createMonthlyAllocationMap();

    private Map<Month, Integer> createMonthlyAllocationMap() {
        Map<Month, Integer> allocation = new HashMap<>();
        allocation.put(Month.JANUARY, 1);
        allocation.put(Month.FEBRUARY, 1);
        allocation.put(Month.MARCH, 1);
        allocation.put(Month.APRIL, 2);
        allocation.put(Month.MAY, 1);
        allocation.put(Month.JUNE, 1);
        allocation.put(Month.JULY, 1);
        allocation.put(Month.AUGUST, 2);
        allocation.put(Month.SEPTEMBER, 1);
        allocation.put(Month.OCTOBER, 1);
        allocation.put(Month.NOVEMBER, 1);
        allocation.put(Month.DECEMBER, 2);
        return Collections.unmodifiableMap(allocation);
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
        Employee employee = employeeRepository.findByEmployeeId(requestDTO.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LeaveType leaveType = leaveTypeRepository.findById(requestDTO.getLeaveTypeId())
                .orElseThrow(() -> new RuntimeException("Leave type not found"));

        if (requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        int duration = (int) ChronoUnit.DAYS.between(requestDTO.getStartDate(), requestDTO.getEndDate()) + 1;

        if (leaveRequestRepository.hasOverlappingLeaveRequest(
                requestDTO.getEmployeeId(),
                requestDTO.getLeaveTypeId(),
                requestDTO.getStartDate(),
                requestDTO.getEndDate(),
                LeaveStatus.REJECTED)) {
            throw new LeaveRequestConflictException("You already have a pending or approved leave request for this period");
        }

        if (leaveType.isPaid()) {
            // Use monthly logic for balance check
            checkLeaveBalanceWithMonthlyLogic(requestDTO.getEmployeeId(), requestDTO.getLeaveTypeId(),
                    duration, requestDTO.getStartDate());
        }

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStartDate(requestDTO.getStartDate());
        leaveRequest.setEndDate(requestDTO.getEndDate());
        leaveRequest.setDurationDays(duration);
        leaveRequest.setReason(requestDTO.getReason());
        leaveRequest.setStatus(LeaveStatus.PENDING);

        leaveRequest = leaveRequestRepository.save(leaveRequest);

        hrActivityService.logActivity(
                "Leave Request",
                "Leave request submitted for " + requestDTO.getStartDate() + " to " + requestDTO.getEndDate(),
                employee.getFirstName() + " " + employee.getLastName(),
                leaveRequest.getStatus().name()
        );

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

        Employee approvedBy = employeeRepository.findByEmployeeId(approvedByEmployeeId)
                .orElseThrow(() -> new RuntimeException("Approver not found"));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Only pending leave requests can be approved");
        }

        if (leaveRequest.getLeaveType().isPaid()) {
            updateLeaveBalanceWithMonthlyLogic(leaveRequest, false);
        }

        leaveRequest.setStatus(LeaveStatus.APPROVED);
        leaveRequest.setProcessedBy(approvedBy);
        leaveRequest.setProcessedDate(LocalDateTime.now());

        leaveRequest = leaveRequestRepository.save(leaveRequest);

        hrActivityService.logActivity(
                "Leave Approved",
                "Leave approved for " + leaveRequest.getStartDate() + " to " + leaveRequest.getEndDate(),
                leaveRequest.getEmployee().getFirstName() + " " + leaveRequest.getEmployee().getLastName(),
                leaveRequest.getStatus().name()
        );

        return convertToLeaveRequestResponseDTO(leaveRequest);
    }

    @Override
    @Transactional
    public LeaveRequestResponseDTO rejectLeaveRequest(Long requestId, String rejectedByEmployeeId, String reason) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        Employee rejectedBy = employeeRepository.findByEmployeeId(rejectedByEmployeeId)
                .orElseThrow(() -> new RuntimeException("Rejector not found"));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Only pending leave requests can be rejected");
        }

        leaveRequest.setStatus(LeaveStatus.REJECTED);
        leaveRequest.setProcessedBy(rejectedBy);
        leaveRequest.setProcessedDate(LocalDateTime.now());
        leaveRequest.setRejectionReason(reason);

        leaveRequest = leaveRequestRepository.save(leaveRequest);

        hrActivityService.logActivity(
                "Leave Rejected",
                "Leave rejected for " + leaveRequest.getStartDate() + " to " + leaveRequest.getEndDate() +
                        ". Reason: " + reason,
                leaveRequest.getEmployee().getFirstName() + " " + leaveRequest.getEmployee().getLastName(),
                leaveRequest.getStatus().name()
        );

        return convertToLeaveRequestResponseDTO(leaveRequest);
    }

    @Override
    @Transactional
    public void initializeEmployeeLeaveBalance(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        int currentYear = LocalDate.now().getYear();
        List<LeaveType> activeLeaveTypes = leaveTypeRepository.findByIsActiveTrue();

        for (LeaveType leaveType : activeLeaveTypes) {
            // Skip if balance already exists for this year
            if (leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(
                    employeeId, leaveType.getId(), currentYear).isPresent()) {
                continue;
            }

            // FIXED: Calculate leaves based on leave type
            int totalLeaves;
            if ("Annual Leave".equalsIgnoreCase(leaveType.getName())) {
                // For Annual Leave: use monthly allocation logic
                totalLeaves = calculateEligibleLeaves(employee.getHireDate(), currentYear);
            } else {
                // For other leave types: use their maxDaysPerYear
                totalLeaves = (leaveType.getMaxDaysPerYear() != null) ?
                        leaveType.getMaxDaysPerYear() : 0;
            }

            // Ensure it doesn't exceed leave type's max days per year
            if (leaveType.getMaxDaysPerYear() != null) {
                totalLeaves = Math.min(totalLeaves, leaveType.getMaxDaysPerYear());
            }

            EmployeeLeaveBalance balance = new EmployeeLeaveBalance();
            balance.setEmployee(employee);
            balance.setLeaveType(leaveType);
            balance.setYear(currentYear);
            balance.setTotalDays(totalLeaves);
            balance.setUsedDays(0);
            balance.setRemainingDays(totalLeaves);

            leaveBalanceRepository.save(balance);
        }
    }

    /**
     * Calculates eligible leaves based on monthly allocation and joining date.
     * Only employees who joined on or before the 10th of a month are eligible for that month's allocation.
     */
    private int calculateEligibleLeaves(LocalDate joiningDate, int year) {
        if (joiningDate == null) return 0;

        int totalLeaves = 0;
        int joiningYear = joiningDate.getYear();

        // If employee joined in previous years, they get full allocation (15 days)
        if (joiningYear < year) {
            return 15; // Hardcoded total since we know it's always 15
        }

        // For current year: calculate pro-rated leaves based on joining month
        for (Map.Entry<Month, Integer> entry : MONTHLY_ALLOCATION.entrySet()) {
            Month month = entry.getKey();
            int leaves = entry.getValue();

            if (isEligibleForMonthAllocation(joiningDate, month, year)) {
                totalLeaves += leaves;
            }
        }

        return totalLeaves;
    }

    /**
     * Check if employee is eligible for a specific month's allocation
     * Employee must have joined on or before 10th of that month
     */
    private boolean isEligibleForMonthAllocation(LocalDate joiningDate, Month month, int year) {
        LocalDate monthStart = LocalDate.of(year, month, 1);
        LocalDate eligibilityCutoff = LocalDate.of(year, month, 10);

        // If joining date is after the month, not eligible
        if (joiningDate.isAfter(monthStart.withDayOfMonth(monthStart.lengthOfMonth()))) {
            return false;
        }

        // For the joining month: must join on or before 10th to be eligible
        if (joiningDate.getMonth() == month && joiningDate.getYear() == year) {
            return joiningDate.isBefore(eligibilityCutoff) || joiningDate.equals(eligibilityCutoff);
        }

        // For months after joining month, always eligible
        return joiningDate.isBefore(monthStart) || joiningDate.equals(monthStart);
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

        if (leaveRequest.getStatus() == LeaveStatus.APPROVED && leaveRequest.getLeaveType().isPaid()) {
            updateLeaveBalanceWithMonthlyLogic(leaveRequest, true);
        }

        leaveRequest.setStatus(LeaveStatus.CANCELLED);
        leaveRequest.setProcessedDate(LocalDateTime.now());

        leaveRequest = leaveRequestRepository.save(leaveRequest);

        hrActivityService.logActivity(
                "Leave Cancelled",
                "Leave cancelled for " + leaveRequest.getStartDate() + " to " + leaveRequest.getEndDate(),
                leaveRequest.getEmployee().getFirstName() + " " + leaveRequest.getEmployee().getLastName(),
                leaveRequest.getStatus().name()
        );

        return convertToLeaveRequestResponseDTO(leaveRequest);
    }

    @Override
    public List<LeaveRequestResponseDTO> getPendingLeaveRequests() {
        return leaveRequestRepository.findByStatusOrderByRequestDateDesc(LeaveStatus.PENDING)
                .stream()
                .map(this::convertToLeaveRequestResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Monthly allocation job - runs on 15th of each month
     * Credits leaves to employees who joined before 10th
     */
    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 15 * ?") // Run on 15th of every month at midnight
    public void creditMonthlyLeaves() {
        LocalDate today = LocalDate.now();
        Month currentMonth = today.getMonth();
        int currentYear = today.getYear();

        // Get leaves to allocate for current month
        Integer leavesToAllocate = MONTHLY_ALLOCATION.get(currentMonth);
        if (leavesToAllocate == null || leavesToAllocate == 0) {
            return;
        }

        // Get Annual Leave type
        Optional<LeaveType> annualLeaveTypeOpt = leaveTypeRepository.findByIsActiveTrue().stream()
                .filter(lt -> "Annual Leave".equalsIgnoreCase(lt.getName()))
                .findFirst();

        if (annualLeaveTypeOpt.isEmpty()) {
            System.out.println("Annual Leave type not found. Skipping monthly allocation.");
            return;
        }

        LeaveType annualLeaveType = annualLeaveTypeOpt.get();
        List<Employee> activeEmployees = employeeRepository.findByStatus(EmployeeStatus.ACTIVE);

        for (Employee employee : activeEmployees) {
            LocalDate joiningDate = employee.getHireDate();

            // Check if employee is eligible for this month's allocation (joined before/on 10th)
            if (isEligibleForMonthAllocation(joiningDate, currentMonth, currentYear)) {
                // Credit leaves ONLY to Annual Leave
                allocateAnnualLeavesForEmployee(employee, currentYear, leavesToAllocate, currentMonth, annualLeaveType);
            }
        }

        System.out.println("Monthly Annual leaves allocated on 15th for " + currentMonth + " " + currentYear);
    }


     //Allocate leaves ONLY for Annual Leave type

    private void allocateAnnualLeavesForEmployee(Employee employee, int year, int leavesToAdd, Month month, LeaveType annualLeaveType) {
        Optional<EmployeeLeaveBalance> balanceOpt = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndYear(employee.getEmployeeId(), annualLeaveType.getId(), year);

        EmployeeLeaveBalance balance;
        if (balanceOpt.isPresent()) {
            balance = balanceOpt.get();
            // Update existing balance
            balance.setTotalDays(balance.getTotalDays() + leavesToAdd);
            balance.setRemainingDays(balance.getRemainingDays() + leavesToAdd);
        } else {
            // Create new balance if doesnt exist
            balance = new EmployeeLeaveBalance();
            balance.setEmployee(employee);
            balance.setLeaveType(annualLeaveType);
            balance.setYear(year);
            balance.setTotalDays(leavesToAdd);
            balance.setUsedDays(0);
            balance.setRemainingDays(leavesToAdd);
        }

        leaveBalanceRepository.save(balance);

        // Log the allocation
        hrActivityService.logActivity(
                "Annual Leave Allocation",
                "Monthly Annual leave allocated: " + leavesToAdd + " days for " + month,
                employee.getFirstName() + " " + employee.getLastName(),
                "ALLOCATED"
        );
    }

    //  Check balance with monthly deduction logic
    // Leaves 1st-9th: deducted from current month
     // Leaves 10th onwards: deducted from next month

    private void checkLeaveBalanceWithMonthlyLogic(String employeeId, Long leaveTypeId,
                                                   int requestedDays, LocalDate startDate) {
        // FIXED: Make balanceYear effectively final
        final int balanceYear;
        if (startDate.getDayOfMonth() >= 10) {
            // Leaves after 10th: use next month's balance
            balanceYear = startDate.plusMonths(1).getYear();
        } else {
            // Leaves 1st-9th: use current month's balance
            balanceYear = startDate.getYear();
        }

        // Now balanceYear is effectively final and can be used in lambda
        Optional<EmployeeLeaveBalance> balanceOpt = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, balanceYear);

        if (balanceOpt.isEmpty()) {
            initializeEmployeeLeaveBalance(employeeId);
            balanceOpt = leaveBalanceRepository
                    .findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, balanceYear);
        }

        EmployeeLeaveBalance balance = balanceOpt.orElseThrow(() ->
                new RuntimeException("Failed to initialize leave balance"));

        if (balance.getRemainingDays() < requestedDays) {
            throw new InsufficientLeaveBalanceException(
                    String.format("Insufficient leave balance for %s. Available: %d, Requested: %d",
                            getMonthDescription(startDate), balance.getRemainingDays(), requestedDays));
        }
    }


    //  Get description for which month's balance is being used

    private String getMonthDescription(LocalDate startDate) {
        if (startDate.getDayOfMonth() <= 9) {
            return startDate.getMonth() + " current month";
        } else {
            return startDate.plusMonths(1).getMonth() + " next month";
        }
    }


    // Update leave balance with monthly logic

    private void updateLeaveBalanceWithMonthlyLogic(LeaveRequest leaveRequest, boolean isCancellation) {
        LocalDate startDate = leaveRequest.getStartDate();
        int duration = leaveRequest.getDurationDays();

        // FIXED: Make balanceYear effectively final
        final int balanceYear;
        if (startDate.getDayOfMonth() >= 10) {
            // Leaves after 10th: use next month's balance
            balanceYear = startDate.plusMonths(1).getYear();
        } else {
            // Leaves 1st-9th: use current month's balance
            balanceYear = startDate.getYear();
        }

        // FIXED: Now balanceYear is effectively final and can be used in lambda
        EmployeeLeaveBalance balance = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndYear(
                        leaveRequest.getEmployee().getEmployeeId(),
                        leaveRequest.getLeaveType().getId(),
                        balanceYear)
                .orElseThrow(() -> new RuntimeException("Leave balance not found for year: " + balanceYear));

        if (isCancellation) {
            balance.setUsedDays(balance.getUsedDays() - duration);
            balance.setRemainingDays(balance.getRemainingDays() + duration);
        } else {
            balance.setUsedDays(balance.getUsedDays() + duration);
            balance.setRemainingDays(balance.getRemainingDays() - duration);
        }

        leaveBalanceRepository.save(balance);
    }

    @Override
    public Map<String, Object> getMonthlyAllocationStatus(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LocalDate today = LocalDate.now();
        Month currentMonth = today.getMonth();
        int currentYear = today.getYear();

        Map<String, Object> status = new HashMap<>();
        status.put("currentMonth", currentMonth);
        status.put("currentYear", currentYear);

        // Check eligibility (joined before/on 10th)
        boolean isEligible = isEligibleForMonthAllocation(employee.getHireDate(), currentMonth, currentYear);
        status.put("isEligibleForAllocation", isEligible);

        if (isEligible) {
            status.put("leavesToAllocate", MONTHLY_ALLOCATION.get(currentMonth));
            status.put("allocationDate", "15th of " + currentMonth);
            status.put("joiningDate", employee.getHireDate());
        }

        return status;
    }

    // NEW: Allocation endpoint implementation
    @Override
    public LeaveAllocationDTO getAllocationDetails(String employeeId, Integer year) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Use provided year or current year if null
        int targetYear = (year != null) ? year : LocalDate.now().getYear();

        // Find Annual Leave type dynamically
        Optional<LeaveType> annualLeaveTypeOpt = leaveTypeRepository.findByIsActiveTrue().stream()
                .filter(lt -> "Annual Leave".equalsIgnoreCase(lt.getName()))
                .findFirst();

        if (annualLeaveTypeOpt.isEmpty()) {
            throw new RuntimeException("Annual Leave type not found in the system");
        }

        LeaveType annualLeaveType = annualLeaveTypeOpt.get();

        // Get only ANNUAL LEAVE balance for the employee for the target year
        Optional<EmployeeLeaveBalance> annualLeaveBalance = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, annualLeaveType.getId(), targetYear);

        if (annualLeaveBalance.isEmpty()) {
            // Initialize balance if not found
            initializeEmployeeLeaveBalance(employeeId);
            annualLeaveBalance = leaveBalanceRepository
                    .findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, annualLeaveType.getId(), targetYear);
        }

        EmployeeLeaveBalance balance = annualLeaveBalance.orElseThrow(() ->
                new RuntimeException("No annual leave balance found for employee " + employeeId + " in year " + targetYear));

        int totalAnnualLeaves = balance.getTotalDays();
        int usedAnnualLeaves = balance.getUsedDays();
        int remainingAnnualLeaves = balance.getRemainingDays();

        // Calculate monthly breakdown with ordered months
        Map<String, Integer> monthlyAllocation = calculateMonthlyBreakdown(employee.getHireDate(), targetYear);

        // Determine allocation status
        String allocationStatus = getAllocationStatus(employee, targetYear, totalAnnualLeaves);

        return new LeaveAllocationDTO(
                targetYear,
                totalAnnualLeaves,
                usedAnnualLeaves,
                remainingAnnualLeaves,
                monthlyAllocation,
                allocationStatus
        );
    }


    // Calculate monthly breakdown based on joining date - ORDERED

    private Map<String, Integer> calculateMonthlyBreakdown(LocalDate joiningDate, int year) {
        Map<String, Integer> monthlyAllocation = new LinkedHashMap<>();

        // Create ordered months list
        List<Month> orderedMonths = Arrays.asList(
                Month.JANUARY, Month.FEBRUARY, Month.MARCH, Month.APRIL, Month.MAY, Month.JUNE,
                Month.JULY, Month.AUGUST, Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER, Month.DECEMBER
        );

        int joiningYear = joiningDate.getYear();

        // If employee joined in previous years, show full allocation
        if (joiningYear < year) {
            for (Month month : orderedMonths) {
                monthlyAllocation.put(month.toString(), MONTHLY_ALLOCATION.get(month));
            }
        } else {
            // For current year: show pro-rated allocation based on joining month
            for (Month month : orderedMonths) {
                int leaves = MONTHLY_ALLOCATION.get(month);

                if (isEligibleForMonthAllocation(joiningDate, month, year)) {
                    monthlyAllocation.put(month.toString(), leaves);
                } else {
                    monthlyAllocation.put(month.toString(), 0); // Show 0 for ineligible months
                }
            }
        }

        return monthlyAllocation;
    }

    //Determine allocation status
    private String getAllocationStatus(Employee employee, int year, int totalAnnualLeaves) {
        LocalDate today = LocalDate.now();

        // Check if it's a future year
        if (year > today.getYear()) {
            return "FUTURE_YEAR";
        }

        // Check if it's a past year
        if (year < today.getYear()) {
            return "PAST_YEAR";
        }

        // For current year
        int expectedTotal = 15; // Total annual leaves

        if (totalAnnualLeaves < expectedTotal) {
            return "PRO_RATED"; // Employee joined mid-year
        }

        return "FULL_YEAR"; // Full allocation
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