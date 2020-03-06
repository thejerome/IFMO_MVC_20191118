package com.efimchik.ifmo.web.mvc;

public class ListMapperFactory {
    public EmployeeListMapper employeeListMapper() {
        return new EmployeeListMapper();
    }

    public DepartmentListMapper departmentListMapper() {
        return new DepartmentListMapper();
    }
}
