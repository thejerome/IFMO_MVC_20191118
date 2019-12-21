package com.efimchik.ifmo.web.mvc.controller;

import com.efimchik.ifmo.web.mvc.mydomain.EmployeeEntity;
import com.efimchik.ifmo.web.mvc.repository.DepRepository;
import com.efimchik.ifmo.web.mvc.repository.EmpRepository;
import com.efimchik.ifmo.web.mvc.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EmployeeController {
    @Autowired
    DepRepository depRepository;

    @Autowired
    EmpRepository empRepository;

    @Autowired
    EmployeeService employeeService;


    @GetMapping("/employees")
    List<com.efimchik.ifmo.web.mvc.domain.Employee> getAllEmployees(@RequestParam(required = false) String page,
                                                                    @RequestParam(required = false) String size,
                                                                    @RequestParam(required = false) String sort) {
        Iterable<EmployeeEntity> employees;

        if (sort != null) {
            sort = sort.toLowerCase();
            if (sort.equals("hired")) {
                sort = "hiredate";
            }
        }

        if (page != null && size != null && sort != null) {
            employees = employeeService.getAll(PageRequest.of(Integer.parseInt(page), Integer.parseInt(size), Sort.DEFAULT_DIRECTION, sort));
        } else if (sort != null) {
            employees = employeeService.getAll(Sort.by(Sort.DEFAULT_DIRECTION, sort));
        } else {
            employees = empRepository.findAll();
        }
        return employeeService.toNormalClasses(employees);
    }

    @GetMapping("/employees/{employee_id}")
    com.efimchik.ifmo.web.mvc.domain.Employee getEmployee(@PathVariable(name = "employee_id") String employeeId,
                                                          @RequestParam(name = "full_chain", required = false, defaultValue = "false") String fullChain) {

        if (fullChain != null && fullChain.equals("true")) {
            return employeeService.getEmployeeWithChain(employeeService.findById(Long.parseLong(employeeId)));
        } else {
            return employeeService.getEmployeeWithoutChain(employeeService.findById(Long.parseLong(employeeId)));
        }
    }

    @GetMapping("/employees/by_manager/{managerId}")
    List<com.efimchik.ifmo.web.mvc.domain.Employee> getByManager(@PathVariable(name = "managerId") String managerId,
                                                                 @RequestParam(required = false) String page,
                                                                 @RequestParam(required = false) String size,
                                                                 @RequestParam(required = false) String sort) {
        Iterable<EmployeeEntity> employees;
        if (sort != null) {
            sort = sort.toLowerCase();
            if (sort.equals("hired")) {
                sort = "hiredate";
            }
        }

        if (page != null && size != null && sort != null) {
            Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size), Sort.DEFAULT_DIRECTION, sort);
            employees = empRepository.findAllByManager(Long.parseLong(managerId), pageable);
        } else {
            employees = empRepository.findAllByManager(Long.parseLong(managerId));
        }

        return employeeService.toNormalClasses(employees);

    }

    @GetMapping("/employees/by_department/{dept}")
    public List<com.efimchik.ifmo.web.mvc.domain.Employee> getByDepartment(@PathVariable(name = "dept") String dept,
                                                                           @RequestParam(required = false) String page,
                                                                           @RequestParam(required = false) String size,
                                                                           @RequestParam(required = false) String sort) {
        Iterable<EmployeeEntity> employees;

        if (sort != null) {
            sort = sort.toLowerCase();
            if (sort.equals("hired")) {
                sort = "hiredate";
            }
        }
        if (!(dept.length() > 2)) {

            Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size), Sort.DEFAULT_DIRECTION, sort);
            employees = empRepository.findAllByDepartment(Long.parseLong(dept), pageable);
        } else {

            Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size), Sort.DEFAULT_DIRECTION, sort);
            employees = empRepository.findAllByDepartment(depRepository.findByName(dept).get().getId(), pageable);
        }

        return employeeService.toNormalClasses(employees);
    }
}
