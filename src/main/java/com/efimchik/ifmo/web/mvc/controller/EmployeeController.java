package com.efimchik.ifmo.web.mvc.controller;

import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.service.EmployeeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<Employee> getAllEmployees(PagingRequest pagingRequest) {
        return employeeService.getAllEmployees(pagingRequest);
    }

    @GetMapping("/{id}")
    public Employee getById(@PathVariable(name = "id") String id,
                            @RequestParam(name = "full_chain", required = false) boolean fullChain) {
        return employeeService.getById(id, fullChain);
    }


    @GetMapping("by_manager/{managerId}")
    public List<Employee> getAllEmployees(@PathVariable("managerId") String managerId, PagingRequest pagingRequest) {
        return employeeService.getByManagerId(managerId, pagingRequest);
    }

    @GetMapping("by_department/{department}")
    public List<Employee> getByDepartment(@PathVariable("department") String department, PagingRequest pagingRequest) {
        return employeeService.getByDepartment(department, pagingRequest);
    }

}
