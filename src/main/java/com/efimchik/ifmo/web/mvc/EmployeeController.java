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

import static com.efimchik.ifmo.web.mvc.Service.*;

@RestController
public class EmployeeController {
    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable(name = "employeeId") String employeeId, @RequestParam(name = "full_chain", required = false, defaultValue = "false") String isFullChain) throws SQLException {
        int id=Integer.parseInt(employeeId.trim());
        boolean isChained=Boolean.parseBoolean(isFullChain.trim());
        return new ResponseEntity<>(Service.getEmployeeById(id, isChained), HttpStatus.OK);
    }
    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size, @RequestParam(required = false) String sort) {
        if (sort != null && sort.equals("hired"))
            sort = "HIREDATE";
        String addition="";
        if (sort!=null)
        {
            addition+=" ORDER BY ";
            addition+= sort.trim();
        }
        if (size!=null)
        {
            addition+=" LIMIT ";
            addition+= size.toString();
        }
        if (page!=null)
        {
            addition+=" OFFSET ";
            int offset=size*page;
            addition += offset;
        }
        String request = "SELECT * FROM employee" +
                addition;
        return new ResponseEntity<>(getEmployeeResultList(request), HttpStatus.OK);
    }
    @GetMapping("/employees/by_department/{departmentIdOrName}")
    public ResponseEntity<List<Employee>> getEmployeesByDepartmentIdOdName(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size, @RequestParam(required = false) String sort, @PathVariable String departmentIdOrName) {
        Long Id;
        try { Id = Long.parseLong(departmentIdOrName.trim()); }
        catch (NumberFormatException e) {
            Id = getDepartmentIdByName(departmentIdOrName.trim());
        }

        if (sort != null && sort.equals("hired"))
            sort = "HIREDATE";
        String addition="";
        if (sort!=null)
        {
            addition+=" ORDER BY ";
            addition+= sort;
        }
        if (size!=null)
        {
            addition+=" LIMIT ";
            addition+= size.toString();
        }
        if (page!=null)
        {
            addition+=" OFFSET ";
            int offset=size*page;
            addition += offset;
        }
        String request = "SELECT * FROM employee WHERE department = " + Id +
                addition;

        return new ResponseEntity<>(getEmployeeResultList(request), HttpStatus.OK);
    }
    @GetMapping("/employees/by_manager/{managerId}")
    public ResponseEntity<List<Employee>> getEmployeesByManagerId(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size, @RequestParam(required = false) String sort, @PathVariable Integer managerId) throws SQLException {

        if (sort != null && sort.equals("hired"))
            sort = "HIREDATE";
        String addition="";
        if (sort!=null)
        {
            addition+=" ORDER BY ";
            addition+= sort;
        }
        if (size!=null)
        {
            addition+=" LIMIT ";
            addition+= size.toString();
        }
        if (page!=null)
        {
            addition+=" OFFSET ";
            int offset=size*page;
            addition += offset;
        }
        String request = "SELECT * FROM employee WHERE manager = " + managerId +
                addition;
        return new ResponseEntity<>(getEmployeeResultList(request), HttpStatus.OK);

    }
}
