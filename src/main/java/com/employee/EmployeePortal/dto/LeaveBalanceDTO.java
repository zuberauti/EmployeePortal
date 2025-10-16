//package com.employee.EmployeePortal.dto;
//
//import lombok.Data;
//
//@Data
//public class LeaveBalanceDTO {
//    private String leaveType;
//    private Integer totalDays;
//    private Integer usedDays;
//    private Integer remainingDays;
//    private Integer year;
//}

package com.employee.EmployeePortal.dto;

import lombok.Data;

@Data
public class LeaveBalanceDTO {

    private String leaveType;
    private int totalDays;
    private int usedDays;
    private int remainingDays;
    private int year;

    // getters & setters
}
