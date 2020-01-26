package com.efimchik.ifmo.web.mvc.controller;

import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping(value = "/employees")
@ResponseBody
public class Task2Controller{

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping(value = {"", "/"})
    public List<Employee> getAll(@RequestParam(required = false) Integer page,
                         @RequestParam(required = false) Integer size,
                         @RequestParam(required = false) String sort) {
        List<Employee> all = employeeRepository.getAll();
        return formData(all, page, size, sort);
    }

    @GetMapping("/{id:[0-9]+}")
    public Employee getById(@PathVariable Long id,
                          @RequestParam(required = false) Boolean full_chain) {
        boolean fc = full_chain != null ? full_chain : false;
        return employeeRepository.getById(id, fc);
    }

    @GetMapping("/by_manager/{managerId:[0-9]+}")
    public List<Employee> getByManager(@PathVariable Long managerId,
                               @RequestParam(required = false) Integer page,
                               @RequestParam(required = false) Integer size,
                               @RequestParam(required = false) String sort) {
        List<Employee> employees = employeeRepository.getByManager(managerId);
        return formData(employees, page, size, sort);
    }

    @GetMapping("/by_department/{departmentId:[0-9]+}")
    public List<Employee> getByDepartmentId(@PathVariable Long departmentId,
                                    @RequestParam(required = false) Integer page,
                                    @RequestParam(required = false) Integer size,
                                    @RequestParam(required = false) String sort) {
        List<Employee> employees = employeeRepository.getByDepartment(departmentId);
        return formData(employees, page, size, sort);
    }

    @GetMapping("/by_department/{departmentName:[A-Z|a-z]+}")
    public List<Employee> getByDepartmentName(@PathVariable String departmentName,
                                      @RequestParam(required = false) Integer page,
                                      @RequestParam(required = false) Integer size,
                                      @RequestParam(required = false) String sort) {
        List<Employee> byDepartment = employeeRepository.getByDepartment(departmentName);
        return formData(byDepartment, page, size, sort);
    }

    private List<Employee> formData(List<Employee> employeeList, Integer page, Integer size, String sort) {
        List<Employee> employees = new ArrayList<>(employeeList);
        if (sort != null) {
            switch (sort) {
                case "lastName" :
                    employees.sort(Comparator.comparing(employee -> employee.getFullName().getLastName()));
                    break;
                case "hired":
                    employees.sort(Comparator.comparing(Employee::getHired));
                    break;
                case "position":
                    employees.sort(Comparator.comparing(o -> o.getPosition().name()));
                    break;
                case "salary":
                    employees.sort(Comparator.comparing(Employee::getSalary));
                    break;
                default: break;
            }
        }
        if (page != null && size != null) {
            int fi = Math.max(page * size, 0);
            int ti = Math.min((page+1)*size, employees.size());
            if (fi > ti) fi = ti = 0;
            employees = employees.subList(fi, ti);
        }
        return employees;
    }
}
