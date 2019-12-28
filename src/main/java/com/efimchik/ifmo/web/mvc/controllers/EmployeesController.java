package com.efimchik.ifmo.web.mvc.controllers;

import com.efimchik.ifmo.web.mvc.utils.EmployeeService;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeesController {

    @Autowired
    private EmployeeService dbEmployee;

    @GetMapping()
    public ResponseEntity<List<Employee>> getAllEmployees(@RequestParam(required = false) Integer page,
                                                          @RequestParam(required = false) Integer size,
                                                          @RequestParam(required = false) String sort) {
        return new ResponseEntity<>(dbEmployee.getEmployeesList(page, size, sort), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable(name = "id") String id,
                                  @RequestParam(name = "full_chain", required = false, defaultValue = "false") String fullChain
    ) {
        return new ResponseEntity<>(dbEmployee.getEmployee(Integer.parseInt(id), Boolean.parseBoolean(fullChain)), HttpStatus.OK);
    }

    @GetMapping("/by_manager/{managerId}")
    public ResponseEntity<List<Employee>> getEmployeesByManagerId(@RequestParam(required = false) Integer page,
                                                                  @RequestParam(required = false) Integer size,
                                                                  @RequestParam(required = false) String sort,
                                                                  @PathVariable(name = "managerId") String managerId) {

        return new ResponseEntity<>(dbEmployee.getEmployeesByManagerIdList(page, size, sort, Integer.parseInt(managerId)), HttpStatus.OK);
    }

    @GetMapping("/by_department/{department}")
    public ResponseEntity<List<Employee>> getEmployeesByDepartment(@RequestParam(required = false) Integer page,
                                                                   @RequestParam(required = false) Integer size,
                                                                   @RequestParam(required = false) String sort,
                                                                   @PathVariable(name = "department") String department) {
        return new ResponseEntity<>(dbEmployee.getEmployeesByDepartmentList(page, size, sort, department), HttpStatus.OK);
    }
}
