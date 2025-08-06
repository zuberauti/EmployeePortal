package com.employee.EmployeePortal.exception;

public class LeaveRequestConflictException extends RuntimeException {
    public LeaveRequestConflictException(String message) {
        super(message);
    }
}

