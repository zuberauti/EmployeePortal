package com.employee.EmployeePortal.hr.repository;

import com.employee.EmployeePortal.hr.entity.HRActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HRActivityRepository extends JpaRepository<HRActivity, Long> {

    // Get latest N activities sorted by date
    List<HRActivity> findTop10ByOrderByActivityDateDesc();
}
