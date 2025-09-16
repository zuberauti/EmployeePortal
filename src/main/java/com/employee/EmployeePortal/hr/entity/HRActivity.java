package com.employee.EmployeePortal.hr.entity;


import jakarta.persistence.*;
import lombok.*;

import java.security.PrivateKey;
import java.time.LocalDateTime;

@Entity
@Table(name = "hr_activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HRActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activityId;

    @Column(nullable = false)
    private LocalDateTime activityDate;

    @Column(nullable = false,length = 100)
    private String activityType;

    @Column(nullable = false,length = 255)
    private String description;

    @Column(nullable = false,length = 100)
    private String employeeName;

    @Column(nullable = false,length = 20)
    private String status;


}
