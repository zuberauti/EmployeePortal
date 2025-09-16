package com.employee.EmployeePortal.hr.service;

import com.employee.EmployeePortal.hr.entity.HRActivity;
import com.employee.EmployeePortal.hr.repository.HRActivityRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class HRActivityService {

    private final HRActivityRepository repository;

    public HRActivityService(HRActivityRepository repository){
        this.repository = repository;
    }

    public List<HRActivity> getRecentActivities(){
        return repository.findTop10ByOrderByActivityDateDesc();
    }

    public void logActivity(String activityType, String description, String employeeName, String status) {
        HRActivity activity = new HRActivity();
        activity.setActivityType(activityType);
        activity.setDescription(description);
        activity.setEmployeeName(employeeName);
        activity.setStatus(status);
        activity.setActivityDate(LocalDateTime.now());

        repository.save(activity);
    }
}
