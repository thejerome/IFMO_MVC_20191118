package com.efimchik.ifmo.web.mvc.service;

import java.util.List;

import com.efimchik.ifmo.web.mvc.domain.Employee;

public interface EmployeeService {

    List<Employee> getAll(Paging paging);

    List<Employee> getAllSortByHireDate(Paging paging);

    List<Employee> getAllSortByLastname(Paging paging);

    List<Employee> getAllSortBySalary(Paging paging);

    List<Employee> getAllSortByPosition(Paging paging);

    List<Employee> getByDepartmentSortByHireDate(String department, Paging paging);

    List<Employee> getByDepartmentSortBySalary(String department, Paging paging);

    List<Employee> getByDepartmentSortByLastname(String department, Paging paging);

    List<Employee> getByDepartmentSortByPosition(String department, Paging paging);

    List<Employee> getAllbyDepartment(String department, Paging paging);

    List<Employee> getByManagerSortByLastname(String manager, Paging paging);

    List<Employee> getByManagerSortByHireDate(String manager, Paging paging);

    List<Employee> getByManagerSortBySalary(String manager, Paging paging);

    List<Employee> getByManagerSortByPosition(String manager, Paging paging);

    List<Employee> getAllbyManager(String manager, Paging paging);

    Employee getWithId(String Id, boolean chain);
}
