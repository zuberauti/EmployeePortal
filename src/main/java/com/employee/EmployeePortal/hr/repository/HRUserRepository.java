package com.employee.EmployeePortal.hr.repository;

import com.employee.EmployeePortal.hr.entity.HRUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HRUserRepository extends JpaRepository<HRUser, Long> {
    Optional<HRUser> findByUsernameAndPassword(String username, String password);
}
