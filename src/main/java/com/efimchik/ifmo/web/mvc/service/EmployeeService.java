package com.efimchik.ifmo.web.mvc.service;

import java.util.List;

import com.efimchik.ifmo.web.mvc.domain.Employee;

public interface EmployeeService {

    List<Employee> getAll(Paging paging, String sort);

    List<Employee> getbyDepartment(String department, Paging paging, String sort);

    List<Employee> getbyManager(String manager, Paging paging, String sort);

    Employee getWithId(String Id, boolean chain);
}
