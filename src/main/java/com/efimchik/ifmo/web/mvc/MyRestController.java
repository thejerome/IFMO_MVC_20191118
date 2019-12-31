package com.efimchik.ifmo.web.mvc;

import org.springframework.web.bind.annotation.PathVariable;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@RestController
public class MyRestController {

    @GetMapping(value = "/employees", produces = "application/json")
    public ResponseEntity<List<Employee>> getAllEmployee(@RequestParam(required = false) Integer page,
                                                          @RequestParam(required = false) Integer size,
                                                          @RequestParam(required = false) String sort) {

        try {
            List<Employee> entities = new DBHelper().getAllEmployeeFromDb(false, page, size, sort, true);
            return new ResponseEntity<>(entities, HttpStatus.OK);

        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        }
    }


    @GetMapping(value = "/employees/{employee_id}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Employee> getById(@PathVariable(name = "employee_id") Integer employeeId,
                                            @RequestParam(name = "full_chain", required = false, defaultValue = "false") boolean fullChain) throws SQLException {
        LinkedList<Employee> allEmployeeById = new DBHelper().getAllEmployeeById(employeeId, fullChain, true);
        return ResponseEntity.ok(allEmployeeById.get(0));

    }

    @GetMapping(value = "/employees/by_manager/{managerId}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Employee>> getByManagerId(@PathVariable(name = "managerId") Integer managerId,
                                                         @RequestParam(required = false) Integer page,
                                                         @RequestParam(required = false) Integer size,
                                                         @RequestParam(required = false) String sort) throws SQLException {


        LinkedList<Employee> allEmployeeById = new DBHelper().getAllEmployeeByManager(false, page, size, sort, managerId);

        return ResponseEntity.ok(allEmployeeById);
    }

    @GetMapping(value = "/employees/by_department/{depIdOrDepName}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Employee>> getByDepId(@PathVariable(name = "depIdOrDepName") String depIdOrDepName,
                                                     @RequestParam(required = false) Integer page,
                                                     @RequestParam(required = false) Integer size,
                                                     @RequestParam(required = false) String sort) throws SQLException {
        DBHelper dbHelper = new DBHelper();

        try {
            Integer id = Integer.valueOf(depIdOrDepName);
            LinkedList<Employee> allEmployeeByDepartment = dbHelper.getAllEmployeeByDepartment(false, page, size, sort, id);
            return ResponseEntity.ok(allEmployeeByDepartment);

        } catch (NumberFormatException ignored) {
            Integer departmentIdByName = dbHelper.getDepartmentIdByName(depIdOrDepName);
            LinkedList<Employee> allEmployeeByDepartment = dbHelper.getAllEmployeeByDepartment(false, page, size, sort, departmentIdByName);
            return ResponseEntity.ok(allEmployeeByDepartment);

        }
    }
}