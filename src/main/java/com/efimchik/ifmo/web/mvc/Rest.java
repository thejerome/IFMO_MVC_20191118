package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Employee;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;


@RestController
public class Rest{

    @GetMapping("/employees")
    public List<Employee> getAllEmployees(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                         @RequestParam(name = "size", required = false, defaultValue = "10000") Integer size,
                                                         @RequestParam(name = "sort", required = false, defaultValue = "") String sort){
        return serviceFactory.getAll(new Paging(page, size), sort);
    }

    @GetMapping("/employees/{employee_id}")
    public Employee getById(@RequestParam(required = false) boolean full_chain, @PathVariable Long employee_id) throws SQLException {
        return serviceFactory.getById(employee_id, full_chain);
    }

    @GetMapping("/employees/by_manager/{managerId}")
    public List<Employee> getByManager(@RequestParam(required = false) Integer page,
                                        @RequestParam(required = false) Integer size,
                                        @RequestParam(required = false, defaultValue = "") String sort,
                                        @PathVariable Long managerId){
        return serviceFactory.getByManager(managerId, new Paging(page, size), sort);
    }

    @GetMapping("/employees/by_department/{depIdOrName}")
    public List<Employee> getByDepartment(@RequestParam(required = false) Integer page,
                                    @RequestParam(required = false) Integer size,
                                    @RequestParam(required = false, defaultValue = "") String sort,
                                    @PathVariable String depIdOrName){
        return serviceFactory.getByDep(depIdOrName, new Paging(page, size), sort);
    }
}
