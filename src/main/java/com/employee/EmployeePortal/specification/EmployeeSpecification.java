package com.employee.EmployeePortal.specification;

import com.employee.EmployeePortal.entity.Employee;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeSpecification {

    public static Specification<Employee> hasDepartment(String department) {
        return (root, query, cb) ->
                department == null ? null : cb.equal(root.get("department"), department);
    }

    public static Specification<Employee> hasPosition(String position) {
        return (root, query, cb) ->
                position == null ? null : cb.equal(root.get("position"), position);
    }

    public static Specification<Employee> hasStatus(String status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Employee> hasEmploymentType(String employmentType) {
        return (root, query, cb) ->
                employmentType == null ? null : cb.equal(root.get("employmentType"), employmentType);
    }

    public static Specification<Employee> hasManager(String managerId) {
        return (root, query, cb) ->
                managerId == null ? null : cb.equal(root.get("managerId"), managerId);
    }

    public static Specification<Employee> hasNameLike(String name) {
        return (root, query, cb) ->
                name == null ? null : cb.like(cb.lower(root.get("firstName")), "%" + name.toLowerCase() + "%");
    }
}
