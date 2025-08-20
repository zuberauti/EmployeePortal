//package com.employee.EmployeePortal.dto;
//
//import lombok.Data;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//
//@Data
//public class PayslipDTO {
//    private Long id;
//    private String employeeId;
//    private String employeeName;
//    private String payslipNumber;
//    private LocalDate payPeriodStart;
//    private LocalDate payPeriodEnd;
//    private LocalDate payDate;
//    private String status;
//
//    // Earnings
//    private BigDecimal basicSalary;
//    private BigDecimal housingAllowance;
//    private BigDecimal transportAllowance;
//    private BigDecimal mealAllowance;
//    private BigDecimal overtimePay;
//    private BigDecimal bonus;
//    private BigDecimal otherAllowances;
//
//    // Deductions
//    private BigDecimal taxDeduction;
//    private BigDecimal socialSecurity;
//    private BigDecimal healthInsurance;
//    private BigDecimal pensionContribution;
//    private BigDecimal otherDeductions;
//
//    // Totals
//    private BigDecimal grossEarnings;
//    private BigDecimal totalDeductions;
//    private BigDecimal netPay;
//
//    private List<PayslipItemDTO> items;
//}
package com.employee.EmployeePortal.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class PayslipDTO {
    private Long id;
    private String employeeId;
    private String employeeName;
    private String payslipNumber;
    private LocalDate payPeriodStart;
    private LocalDate payPeriodEnd;
    private LocalDate payDate;
    private String status;

    // Earnings
    private BigDecimal basicSalary;
    private BigDecimal housingAllowance;
    private BigDecimal transportAllowance;
    private BigDecimal mealAllowance;
    private BigDecimal overtimePay;
    private BigDecimal bonus;
    private BigDecimal otherAllowances;

    // Deductions
    private BigDecimal taxDeduction;
    private BigDecimal socialSecurity;
    private BigDecimal healthInsurance;
    private BigDecimal pensionContribution;
    private BigDecimal otherDeductions;

    // Totals
    private BigDecimal grossEarnings;
    private BigDecimal totalDeductions;
    private BigDecimal netPay;

    private List<PayslipItemDTO> items;
}
