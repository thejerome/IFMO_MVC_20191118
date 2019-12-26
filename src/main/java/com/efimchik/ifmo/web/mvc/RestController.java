package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.*;
import com.efimchik.ifmo.web.mvc.service.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@org.springframework.web.bind.annotation.RestController
public class RestController {
    @GetMapping("/employees")
    List<Employee> getAllEmployees(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10000") Integer size, @RequestParam(defaultValue = "") String sort) {
        switch (sort) {
            case "lastName":
                return ServiceFactory.employeeService().getAllSortByLastname(new Paging(page, size));
            case "hired":
                return ServiceFactory.employeeService().getAllSortByHireDate(new Paging(page, size));
            case "position":
                return ServiceFactory.employeeService().getAllSortByPosition(new Paging(page, size));
            case "salary":
                return ServiceFactory.employeeService().getAllSortBySalary(new Paging(page, size));
            default:
                return ServiceFactory.employeeService().getAll(new Paging(page, size));
        }
    }

    @GetMapping("/employees/{employee_id}")
    Employee getEmployeebyID(@PathVariable String employee_id, @RequestParam(required = false) boolean full_chain) {
        return ServiceFactory.employeeService().getWithId(employee_id, full_chain);
    }

    @GetMapping("/employees/by_manager/{managerId}")
    List<Employee> getAllEmployeesByManager(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10000") Integer size, @RequestParam(defaultValue = "") String sort, @PathVariable String managerId) {
        switch (sort) {
            case "lastName":
                return ServiceFactory.employeeService().getByManagerSortByLastname(managerId, new Paging(page, size));
            case "hired":
                return ServiceFactory.employeeService().getByManagerSortByHireDate(managerId, new Paging(page, size));
            case "position":
                return ServiceFactory.employeeService().getByManagerSortByPosition(managerId, new Paging(page, size));
            case "salary":
                return ServiceFactory.employeeService().getByManagerSortBySalary(managerId, new Paging(page, size));
            default:
                return ServiceFactory.employeeService().getAllbyManager(managerId, new Paging(page, size));
        }
    }

    @GetMapping("/employees/by_department/{department}")
    List<Employee> getAllEmployeesByDepartment(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10000") Integer size, @RequestParam(defaultValue = "") String sort, @PathVariable String department) {
        switch (sort) {
            case "lastName":
                return ServiceFactory.employeeService().getByDepartmentSortByLastname(department, new Paging(page, size));
            case "hired":
                return ServiceFactory.employeeService().getByDepartmentSortByHireDate(department, new Paging(page, size));
            case "position":
                return ServiceFactory.employeeService().getByDepartmentSortByPosition(department, new Paging(page, size));
            case "salary":
                return ServiceFactory.employeeService().getByDepartmentSortBySalary(department, new Paging(page, size));
            default:
                return ServiceFactory.employeeService().getAllbyDepartment(department, new Paging(page, size));
        }
    }
}
