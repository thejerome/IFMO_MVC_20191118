package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.service.DepartmentService;
import com.efimchik.ifmo.web.mvc.service.UserService;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class Controller {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserService userService;

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees(@RequestParam(required = false) Integer page,
                                                          @RequestParam(required = false) Integer size,
                                                          @RequestParam(required = false) String sort) {

        return new ResponseEntity<>(userService.getEmployeeResultList(sort, size, page), HttpStatus.OK);
    }

    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable(name = "employeeId") String employeeId,
                                                    @RequestParam(name = "full_chain", required = false, defaultValue = "false") String isFullChain) {

        return new ResponseEntity<>(userService.getEmployeeById(Integer.parseInt(employeeId), Boolean.parseBoolean(isFullChain)), HttpStatus.OK);
    }

    @GetMapping("/employees/by_manager/{managerId}")
    public ResponseEntity<List<Employee>> getEmployeesByManagerId(@RequestParam(required = false) Integer page,
                                                                  @RequestParam(required = false) Integer size,
                                                                  @RequestParam(required = false) String sort,
                                                                  @PathVariable Integer managerId) {

        return new ResponseEntity<>(userService.getEmployeeByManagerResultList(managerId, sort, size, page), HttpStatus.OK);

    }

    @GetMapping("/employees/by_department/{departmentIdOrName}")
    public ResponseEntity<List<Employee>> getEmployeesByDepartmentIdOdName(@RequestParam(required = false) Integer page,
                                                                           @RequestParam(required = false) Integer size,
                                                                           @RequestParam(required = false) String sort,
                                                                           @PathVariable String departmentIdOrName) {

        Long departmentId;
        try { departmentId = Long.parseLong(departmentIdOrName); }
        catch (NumberFormatException e) {
            departmentId = departmentService.getDepartmentIdByName(departmentIdOrName);
        }
        return new ResponseEntity<>(departmentService.getEmployeeByDepResultList(departmentId, sort, size, page), HttpStatus.OK);
    }
}