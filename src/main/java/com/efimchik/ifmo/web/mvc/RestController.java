package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Employee;

import com.efimchik.ifmo.web.mvc.service.Paging;
import com.efimchik.ifmo.web.mvc.service.ServiceFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;

@org.springframework.web.bind.annotation.RestController
public class RestController {
    @GetMapping("/employees")
    private List<Employee> getAllEmployees(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10000") Integer size, @RequestParam(defaultValue = "") String sort) {
        return ServiceFactory.employeeService().getAll(new Paging(page, size), sort);
    }

    @GetMapping("/employees/{employee_id}")
    private Employee getEmployeebyID(@PathVariable String employee_id, @RequestParam(required = false) boolean full_chain) {
        return ServiceFactory.employeeService().getWithId(employee_id, full_chain);
    }

    @GetMapping("/employees/by_manager/{managerId}")
    private List<Employee> getAllEmployeesByManager(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10000") Integer size, @RequestParam(defaultValue = "") String sort, @PathVariable String managerId) {
        return ServiceFactory.employeeService().getbyManager(managerId, new Paging(page, size), sort);
    }

    @GetMapping("/employees/by_department/{department}")
    private List<Employee> getAllEmployeesByDepartment(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10000") Integer size, @RequestParam(defaultValue = "") String sort, @PathVariable String department) {
        return ServiceFactory.employeeService().getbyDepartment(department, new Paging(page, size), sort);
    }
}
