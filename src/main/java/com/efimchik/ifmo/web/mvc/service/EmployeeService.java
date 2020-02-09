package com.efimchik.ifmo.web.mvc.service;

import com.efimchik.ifmo.web.mvc.controller.PagingRequest;
import com.efimchik.ifmo.web.mvc.data.EmployeeDao;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeDao employeeDao;

    public EmployeeService(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    public List<Employee> getAllEmployees(PagingRequest request) {
        return employeeDao.findAllEmployees(request);
    }

    public Employee getById(String id, boolean fullChain) {
        return employeeDao.findById(id, true, fullChain);
    }

    public List<Employee> getByManagerId(String managerId, PagingRequest request) {
        return employeeDao.findByManagerId(managerId, request);
    }

    public List<Employee> getByDepartment(String department, PagingRequest request) {
        if (isNumeric(department)) {
            return employeeDao.findByDepartmentId(department, request);
        }
        return employeeDao.findByDepartmentName(department, request);
    }

    private  boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Long.parseLong(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
