package com.efimchik.ifmo.web.mvc.controller;


import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.service.DaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MainController {

    private DaoService allWeHave = new DaoService();


    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees (@RequestParam(required = false) Integer page,
                                                           @RequestParam(required = false) Integer size,
                                                           @RequestParam(required = false) String sort) {
        if ("hired".equals(sort)) {
            sort = "hiredate";
        }
        List<Employee> tmp = allWeHave.getEmployees(1, "SELECT * FROM EMPLOYEE" +
                ((sort != null) ? " order by " + sort : " ") +
                ((size != null) ? " limit " + size : " ") +
                ((page != null) ? " offset " + size * page : " "));
        return ResponseEntity.ok(tmp);
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<Employee> getEmployeeById (@RequestParam(name = "full_chain", required = false) String chain,
                                                     @PathVariable Long id) {
        int ch = 1;
        if ("true".equals(chain)) {
            ch = 3;
        }
    List<Employee> tmp = allWeHave.getEmployees(ch, "SELECT * FROM EMPLOYEE " +
            "WHERE ID = " + id);
        return ResponseEntity.ok(tmp.get(0));
    }

    @GetMapping("/employees/by_manager/{id}")
    public ResponseEntity<List<Employee>> getEmployeesByManager (@PathVariable Long id,
                                                                 @RequestParam(required = false) Integer page,
                                                                 @RequestParam(required = false) Integer size,
                                                                 @RequestParam(required = false) String sort ) {
        if ("hired".equals(sort)) {
            sort = "hiredate";
        }
        List<Employee> tmp = allWeHave.getEmployees(1, "SELECT * FROM EMPLOYEE " +
                "WHERE MANAGER = " + id +
                ((sort != null) ? " order by " + sort : " ") +
                ((size != null) ? " limit " + size : " ") +
                ((page != null) ? " offset " + size * page : " "));
        return ResponseEntity.ok(tmp);
    }

    @GetMapping("/employees/by_department/{idOrName}")
    public ResponseEntity<List<Employee>> getEmployeesByDepartment(@PathVariable String idOrName,
                                                                   @RequestParam(required = false) Integer page,
                                                                   @RequestParam(required = false) Integer size,
                                                                   @RequestParam(required = false) String sort ) {

        if ("hired".equals(sort)) {
            sort = "hiredate";
        }
        int id = 0;
        if (idOrName.charAt(0) <= '9' && idOrName.charAt(0) >= '0') {
            id = Integer.valueOf(idOrName);
        } else {
            id = allWeHave.getDepartmentByName(idOrName);
        }
        List<Employee> tmp = allWeHave.getEmployees(1, "SELECT * FROM EMPLOYEE " +
                "WHERE DEPARTMENT = " + id +
                ((sort != null) ? " order by " + sort : " ") +
                ((size != null) ? " limit " + size : " ") +
                ((page != null) ? " offset " + size * page : " "));
        return ResponseEntity.ok(tmp);
    }


}
