package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Employee;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

import static com.efimchik.ifmo.web.mvc.Service.getDepartmentIdByName;
import static com.efimchik.ifmo.web.mvc.Service.getEmployeeResultListByRequest;

@RestController
public class Controller {

    private String makePagingSQLRequest(String oldrequest, Integer page, Integer size, String sort) {
        String request = oldrequest;
        if (sort != null)
            request += " ORDER BY " + sort + " ASC";
        if (size != null)
            request += " LIMIT " + size;
        if (page != null && size != null)
            request += " OFFSET " + size * page;
        //just fixing the name of the field
        request = request.replace("hired", "hiredate");
        return request;
    }

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sort", required = false) String sort
    ) {
        try {
            return new ResponseEntity<>(
                    getEmployeeResultListByRequest(
                            makePagingSQLRequest("SELECT * FROM EMPLOYEE", page, size, sort),
                            0
                    ),
                    HttpStatus.OK
            );
        } catch (SQLException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable(name = "employeeId") String employeeId,
                                                          @RequestParam(name = "full_chain", required = false, defaultValue = "false") String isFullChain
    ) {
        int managmentDepth = ("true".equals(isFullChain) ? -5 : 0);
        try {
            return new ResponseEntity<>(
                    getEmployeeResultListByRequest(
                            ("SELECT * FROM EMPLOYEE WHERE ID=" + Integer.parseInt(employeeId)), managmentDepth
                    ).get(0), HttpStatus.OK
            );
        } catch (SQLException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/employees/by_manager/{managerId}")
    public ResponseEntity<List<Employee>> getEmployeesByManagerId(@RequestParam(required = false) Integer page,
                                                                  @RequestParam(required = false) Integer size,
                                                                  @RequestParam(required = false) String sort,
                                                                  @PathVariable Integer managerId
    ) {
        try {
            return new ResponseEntity<>(
                    getEmployeeResultListByRequest(
                            makePagingSQLRequest("SELECT * FROM EMPLOYEE WHERE manager = " + managerId.toString(), page, size, sort),
                            0
                    ),
                    HttpStatus.OK
            );
        } catch (SQLException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/employees/by_department/{departmentIdOrName}")
    public ResponseEntity<List<Employee>> getEmployeesByDepartmentIdOdName(@RequestParam(required = false) Integer page,
                                                                           @RequestParam(required = false) Integer size,
                                                                           @RequestParam(required = false) String sort,
                                                                           @PathVariable String departmentIdOrName
    ) {
        try {
            Long departmentId;
            try { departmentId = Long.parseLong(departmentIdOrName); }
            catch(NumberFormatException e) {
                departmentId = getDepartmentIdByName(departmentIdOrName);
            }

            return new ResponseEntity<>(
                    getEmployeeResultListByRequest(
                            makePagingSQLRequest("SELECT * FROM EMPLOYEE WHERE department = " + departmentId.toString(), page, size, sort),
                            0
                    ),
                    HttpStatus.OK
            );
        } catch (SQLException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

}
