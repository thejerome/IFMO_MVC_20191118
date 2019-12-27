package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Employee;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;


@RestController
public class EmployeeRestController {
//    @RestController
//    @GetMapping("/{id}", produces = "application/json")
//        public ResponseEntity<List<Employee>> getAll(@PathVariable int id) {
//            return (id);
//        }


    @GetMapping(value = "/employees", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Employee>> getAll(@RequestParam(required = false) Integer page,
                                                 @RequestParam(required = false) Integer size,
                                                 @RequestParam(required = false) String sort) throws SQLException {
        sort = changeToColumnName(sort);
        String sql = "SELECT * FROM EMPLOYEE" +
                ((sort != null) ? " ORDER BY " + sort : " ") +
                ((size != null) ? " LIMIT " + size : " ") +
                ((page != null) ? " OFFSET " + size * page : " ");
        List<Employee> entities = EmployeeService.getAllEmployeesSorted(false, true, sql);
            return ResponseEntity.ok(entities);
    }

    @GetMapping(value = "/employees/{employee_id}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Employee> getById(@PathVariable(name = "employee_id") String employeeId,
                                            @RequestParam(name = "full_chain", required = false, defaultValue = "false") String fullChain) throws SQLException {
        String sql = "SELECT * FROM EMPLOYEE WHERE id = " + employeeId;
        if ("true".equals(fullChain)) {
            return ResponseEntity.ok(EmployeeService.getAllEmployeesSorted(true, true, sql).get(0));
        } else {
            return ResponseEntity.ok(EmployeeService.getAllEmployeesSorted(false, true, sql).get(0));
        }
    }

    @GetMapping(value = "/employees/by_manager/{managerId}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Employee>> getByManagerId(@PathVariable(name = "managerId") Integer managerId,
                                                         @RequestParam(required = false) Integer page,
                                                         @RequestParam(required = false) Integer size,
                                                         @RequestParam(required = false) String sort) throws SQLException {

        sort = changeToColumnName(sort);
        String sql = "SELECT * FROM EMPLOYEE WHERE manager = " +
                managerId +
                ((sort != null) ? " ORDER BY " + sort : " ") +
                ((size != null) ? " LIMIT " + size : " ") +
                ((page != null) ? " OFFSET " + size * page : " ");
        List<Employee> entities = EmployeeService.getAllEmployeesSorted(false, true, sql);
        return ResponseEntity.ok(entities);
    }

    @GetMapping(value = "/employees/by_department/{depIdOrDepName}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Employee>> getByDepId(@PathVariable(name = "depIdOrDepName") String depIdOrDepName,
                                                     @RequestParam(required = false) Integer page,
                                                     @RequestParam(required = false) Integer size,
                                                     @RequestParam(required = false) String sort) throws SQLException {
        sort = changeToColumnName(sort);

        BigInteger depId = null;
        if(!isNumeric(depIdOrDepName)) {
            String query = "SELECT id FROM DEPARTMENT WHERE name = '" + depIdOrDepName + "'";
            depId = EmployeeService.getDepartmentIdByName(query);
        } else {
            depId = BigInteger.valueOf(Integer.parseInt(depIdOrDepName));
        }
        String sql = "SELECT * FROM EMPLOYEE WHERE department = " +
                depId +
                ((sort != null) ? " ORDER BY " + sort : " ") +
                ((size != null) ? " LIMIT " + size : " ") +
                ((page != null) ? " OFFSET " + size * page : " ");
        List<Employee> entities = EmployeeService.getAllEmployeesSorted(false, true, sql);
        return ResponseEntity.ok(entities);

    }

    private String changeToColumnName(String sort) {
        if (sort != null && "hired".equals(sort)) {
            return "HIREDATE";
        }
        return sort;
    }

    private static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Integer num = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

}

