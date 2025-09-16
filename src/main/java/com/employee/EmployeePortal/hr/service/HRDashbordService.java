package com.employee.EmployeePortal.hr.service;

import com.employee.EmployeePortal.enums.LeaveStatus;
import com.employee.EmployeePortal.hr.dto.HrDashbordResponse;
import com.employee.EmployeePortal.repository.AttendanceRepository;
import com.employee.EmployeePortal.repository.EmployeeRepository;
import com.employee.EmployeePortal.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class HRDashbordService {

    private  final EmployeeRepository employeeRepository;
    private  final LeaveRequestRepository leaveRequestRepository;
    private  final AttendanceRepository attendanceRepository;

    public HrDashbordResponse getDashbordData(){
        long totalEmployees = employeeRepository.count();

        long pendingLeaves = leaveRequestRepository.countByStatus(LeaveStatus.PENDING);

        double attendanceRate = calculateAttendanceRate();

        // For now we are assigning static data later we can fetch HR tasks .

        long openTasks = 5;

        return new HrDashbordResponse(
                totalEmployees,
                pendingLeaves,
                attendanceRate,
                openTasks
        );

    }

    private  double calculateAttendanceRate(){

        YearMonth currentMonth = YearMonth.now();
        LocalDate start = currentMonth.atDay(1);
        LocalDate end = currentMonth.atEndOfMonth();

        long totalRecord = attendanceRepository.countByAttendanceDateBetween(start,end);
        long presentRecord = attendanceRepository.countByStatusAndAttendanceDateBetween("PRESENT",start,end);

        if(totalRecord == 0){
            return 0.0;
        }

        return (presentRecord * 100.0) / totalRecord;
    }

}
