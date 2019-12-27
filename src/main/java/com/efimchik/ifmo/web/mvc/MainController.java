package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Employee;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RestController
public class MainController {
    ConnectionSource connectionSource = ConnectionSource.instance();
    EmployeeService employeeService = new EmployeeService();

    @GetMapping("/employees")
    List<Employee> getAll(@RequestParam(required = false) Integer size, @RequestParam(required = false) Integer page,
                          @RequestParam(required = false) String sort) throws SQLException {
        if (sort != null && sort.equals("hired"))
            sort = "hiredate";
        String query = "select * from employee";
        if (sort != null){
            query += " order by " + sort;
        }
        if (size != null){
            query += " limit " + size + " offset " + size*page;
        }
        ResultSet rs = employeeService.ResultSet(query);
        return employeeService.getEmployeeListWithoutChain(rs);
    }

    @GetMapping("/employees/{id}")
    Optional<Employee> getById(@PathVariable Long id, @RequestParam(required = false, defaultValue = "false") String full_chain) throws SQLException {
        String query = "select * from employee where id="+id;
        ResultSet rs = employeeService.ResultSet(query);
        if (full_chain.equals("false")){
            return Optional.of(employeeService.getEmployeeListWithoutChain(rs).get(0));
        } else {
            return Optional.of(employeeService.getEmployeeListWithChain(rs).get(0));
        }
    }

    @GetMapping("/employees/by_manager/{managerId}")
    List<Employee> getByManager(@RequestParam(required = false) Integer size, @RequestParam(required = false) Integer page,
                          @RequestParam(required = false) String sort, @PathVariable Long managerId) throws SQLException {
        if (sort != null && sort.equals("hired"))
            sort = "hiredate";
        String query = "select * from employee where manager="+managerId;
        if (sort != null){
            query += " order by " + sort;
        }
        if (size != null){
            query += " limit " + size + " offset " + size*page;
        }
        ResultSet rs = employeeService.ResultSet(query);
        return employeeService.getEmployeeListWithoutChain(rs);
    }

    @GetMapping("/employees/by_department/{depNameOrId}")
    List<Employee> getByManager(@RequestParam(required = false) Integer size, @RequestParam(required = false) Integer page,
                                @RequestParam(required = false) String sort, @PathVariable String depNameOrId) throws SQLException {
        if (sort != null && sort.equals("hired"))
            sort = "hiredate";
        String query = "select * from employee";
        if (numeric(depNameOrId)){
            query += " where department="+depNameOrId;
        } else {
            query += " join department dep on employee.department=dep.id where dep.name='" + depNameOrId + "'";
        }
        if (sort != null){
            query += " order by " + sort;
        }
        if (size != null){
            query += " limit " + size + " offset " + size*page;
        }
        ResultSet rs = employeeService.ResultSet(query);
        return employeeService.getEmployeeListWithoutChain(rs);
    }

    private boolean numeric(String s) {
        for (char c : s.toCharArray()){
            if (!(c >= '0' && c <='9'))
                return false;
        }
        return true;
    }

}
