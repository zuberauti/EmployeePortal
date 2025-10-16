//package com.employee.EmployeePortal.repository;
//
//import com.employee.EmployeePortal.entity.LeaveType;
//import org.springframework.data.jpa.repository.JpaRepository;
//import java.util.List;
//
//public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {
//    List<LeaveType> findByIsActiveTrue();
//}


package com.employee.EmployeePortal.repository;

import com.employee.EmployeePortal.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {
    List<LeaveType> findByIsActiveTrue();
}