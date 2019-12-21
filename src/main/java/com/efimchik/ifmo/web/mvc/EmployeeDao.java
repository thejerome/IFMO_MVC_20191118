package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Employee;

import java.util.List;

public interface EmployeeDao {
    Employee getById(String Id, boolean full_chain);
    Employee getById(Long Id, DaoFactory.EmployeeConverter converter);
    List<Employee> getAll(Integer page, Integer size, String sort);
    List<Employee> getByDepartmentId(String departmentId, Integer page, Integer size, String sort);
    List<Employee> getByDepartmentName(String departmentName, Integer page, Integer size, String sort);
    List<Employee> getByManagerId(String id, Integer page, Integer size, String sort);
}