package com.employee.EmployeePortal.repository;

import com.employee.EmployeePortal.entity.PayslipItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PayslipItemRepository extends JpaRepository<PayslipItem, Long> {
    List<PayslipItem> findByPayslipId(Long payslipId);
    void deleteByPayslipId(Long payslipId);
}
