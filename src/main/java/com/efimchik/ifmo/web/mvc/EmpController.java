package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.ConnectionSource;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

import static com.efimchik.ifmo.web.mvc.EmpService.*;

@RestController
public class EmpController {
    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees(@RequestParam(required = false) Integer page,
                                                          @RequestParam(required = false) Integer size,
                                                          @RequestParam(required = false) String sort) {
        sort = sortHired(sort);
        String request = "SELECT * FROM employee" +
                ((sort != null) ? " ORDER BY " + sort : " ") +
                ((size != null) ? " LIMIT " + size : " ") +
                ((page != null) ? " OFFSET " + size * page : " ");

        return new ResponseEntity<>(getEmployeeResultList(request), HttpStatus.OK);
    }

    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable(name = "employeeId") String employeeId,
                                                    @RequestParam(name = "full_chain", required = false, defaultValue = "false") String isFCh) throws SQLException {
        return new ResponseEntity<>(EmpService.getEmployeeById(Integer.parseInt(employeeId), Boolean.parseBoolean(isFCh)), HttpStatus.OK);
    }

    @GetMapping("/employees/by_manager/{managerId}")
    public ResponseEntity<List<Employee>> getEmployeesByManagerId(@RequestParam(required = false) Integer page,
                                                                  @RequestParam(required = false) Integer size,
                                                                  @RequestParam(required = false) String sort,
                                                                  @PathVariable Integer managerId) throws SQLException {

        sort = sortHired(sort);
        String request = "SELECT * FROM employee WHERE manager = " + managerId +
                ((sort != null) ? " ORDER BY " + sort : " ") +
                ((size != null) ? " LIMIT " + size : " ") +
                ((page != null) ? " OFFSET " + size * page : " ");

        return new ResponseEntity<>(getEmployeeResultList(request), HttpStatus.OK);

    }

    @GetMapping("/employees/by_department/{departmentIdOrName}")
    public ResponseEntity<List<Employee>> getEmployeesByDepartmentIdOdName(@RequestParam(required = false) Integer page,
                                                                           @RequestParam(required = false) Integer size,
                                                                           @RequestParam(required = false) String sort,
                                                                           @PathVariable String departmentIdOrName) {
        Long depId;
        try { depId = Long.parseLong(departmentIdOrName); }
        catch (NumberFormatException e) {
            depId = getDepartmentIdByName(departmentIdOrName);
        }
        sort = sortHired(sort);
        String request = "SELECT * FROM employee WHERE department = " + depId +
                ((sort != null) ? " ORDER BY " + sort : " ") +
                ((size != null) ? " LIMIT " + size : " ") +
                ((page != null) ? " OFFSET " + size * page : " ");

        return new ResponseEntity<>(getEmployeeResultList(request), HttpStatus.OK);
    }
    private String sortHired(String sorting){
        if (sorting != null && sorting.equals("hired")){
            sorting = "HIREDATE";
        }
        return sorting;
    }
}
