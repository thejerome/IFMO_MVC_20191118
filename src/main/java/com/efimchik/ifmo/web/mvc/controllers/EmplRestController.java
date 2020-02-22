package com.efimchik.ifmo.web.mvc.controllers;

import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.utils.EmployeeUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@RestController
public class EmplRestController {
    @GetMapping(value = "/employees")
    public ResponseEntity<List<Employee>> getAll(@RequestParam(required = false) Integer page,
                                                 @RequestParam(required = false) Integer size,
                                                 @RequestParam(required = false) String sort) throws SQLException {
        String query = "SELECT * FROM employee" +
                ((sort != null) ? " ORDER BY " + EmployeeUtil.makeColName(sort) : " ") +
                ((size != null) ? " LIMIT " + size : " ") +
                ((page != null) ? " OFFSET " + size*page : " ");
        List<Employee> employees = EmployeeUtil.getSortedEmployees(false, true, query);
        assert employees != null;
        final ResponseEntity<List<Employee>> listResponseEntity = new ResponseEntity<List<Employee>>(employees);
        return listResponseEntity;
    }

    @GetMapping(value = "/employees/{employee_id}")
    public ResponseEntity<Employee> getById(@PathVariable(name = "employee_id") String employeeId,
                                            @RequestParam(name = "full_chain", required = false) String fullChain) throws SQLException {
        String sql = "SELECT * FROM EMPLOYEE WHERE id = " + employeeId;
        if ("true".equals(fullChain)) {
            return ResponseEntity.ok(Objects.requireNonNull(EmployeeUtil.getSortedEmployees(true, true, sql)).get(0));
        } else {
            return ResponseEntity.ok(Objects.requireNonNull(EmployeeUtil.getSortedEmployees(false, true, sql)).get(0));
        }
    }

    @GetMapping(value = "/employees/by_manager/{managerId}")
    public ResponseEntity<List<Employee>> getByManagerId(@PathVariable(name = "managerId") Integer managerId,
                                                         @RequestParam(required = false) Integer page,
                                                         @RequestParam(required = false) Integer size,
                                                         @RequestParam(required = false) String sort) throws SQLException {

        String sql = "SELECT * FROM employee WHERE manager = " +
                managerId +
                ((sort != null) ? " ORDER BY " + EmployeeUtil.makeColName(sort) : " ") +
                ((size != null) ? " LIMIT " + size : " ") +
                ((page != null) ? " OFFSET " + size*page : " ");
        List<Employee> employees = EmployeeUtil.getSortedEmployees(false, true, sql);
        final ResponseEntity<List<Employee>> listResponseEntity = new ResponseEntity<List<Employee>>(employees);
        return listResponseEntity;
    }

    @GetMapping(value = "/employees/by_department/{depIdOrDepName}")
    public ResponseEntity<List<Employee>> getByDepId(@PathVariable(name = "depIdOrDepName") String dep,
                                                     @RequestParam(required = false) Integer page,
                                                     @RequestParam(required = false) Integer size,
                                                     @RequestParam(required = false) String sort) throws SQLException {
        Long depId = null;
        if(!EmployeeUtil.isNum(dep)) {
            String query = "SELECT id FROM department WHERE name = '" + dep + "'";
            depId = EmployeeUtil.getDepIdByName(query);
        } else {
            depId = (long) Integer.parseInt(dep);
        }
        String query = "SELECT * FROM employee WHERE department = " +
                depId +
                ((sort != null) ? " ORDER BY " + EmployeeUtil.makeColName(sort) : " ") +
                ((size != null) ? " LIMIT " + size : " ") +
                ((page != null) ? " OFFSET " + size*page : " ");
        List<Employee> employees = EmployeeUtil.getSortedEmployees(false, true, query);
        assert employees != null;
        final ResponseEntity<List<Employee>> listResponseEntity = new ResponseEntity<List<Employee>>(employees);
        return listResponseEntity;

    }
}
