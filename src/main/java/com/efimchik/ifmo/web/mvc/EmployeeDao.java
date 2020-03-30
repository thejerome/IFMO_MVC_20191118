package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Employee;

import java.util.List;

enum FilterType{
    none,
    byName,
    byManagerId,
    byDepartmentId,
}

public interface EmployeeDao {
    Employee getEmployee(String Id, boolean full_chain);
    List<Employee> getEmployeesFilteredList(FilterType filterType, String filter, Integer page, Integer size, String sort);
}