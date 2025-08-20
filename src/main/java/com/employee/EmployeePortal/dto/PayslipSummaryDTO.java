
package com.employee.EmployeePortal.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PayslipSummaryDTO {
    private Long id;
    private String payslipNumber;
    private LocalDate payPeriodStart;
    private LocalDate payPeriodEnd;
    private LocalDate payDate;
    private String status;
    private BigDecimal netPay;
}
