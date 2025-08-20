package com.employee.EmployeePortal.service;

import com.employee.EmployeePortal.dto.PayslipDTO;
import com.employee.EmployeePortal.dto.PayslipSummaryDTO;
import java.time.LocalDate;
import java.util.List;

public interface PayslipService {
    PayslipDTO generatePayslip(String employeeId, LocalDate payPeriodStart, LocalDate payPeriodEnd);
    PayslipDTO getPayslipById(Long payslipId);
    List<PayslipSummaryDTO> getEmployeePayslips(String employeeId);
    byte[] generatePayslipPdf(Long payslipId);
    void sendPayslipByEmail(Long payslipId, String recipientEmail);
    PayslipDTO updatePayslip(Long payslipId, PayslipDTO payslipDTO);
    void deletePayslip(Long payslipId);
}
