
package com.employee.EmployeePortal.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PayslipItemDTO {
    private Long id;
    // you had both itemName and name — keep both for compatibility, map to name
    private String itemName;
    private String name;
    private String description;
    private BigDecimal amount;
    private boolean taxable;
}
