package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Employee;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class Controller {

    @GetMapping
    public static List<Employee> employees(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    ) throws SQLException {
        sort = isHired(sort);
        return EmployeeService.getAllEmployees(page, size, sort);
    }

    @GetMapping("/{employee_id}")
    public static Employee employee(@PathVariable (name = "employee_id") String id,
                                    @RequestParam(name = "full_chain", required = false, defaultValue = "false") String needFull) throws SQLException {
        return EmployeeService.getEmployeeById(id, needFull);
    }

    @GetMapping("/by_manager/{managerId}")
    public static List<Employee> employeesByManager(@PathVariable() Long managerId,
                                                    @RequestParam(required = false) Integer page,
                                                    @RequestParam(required = false) Integer size,
                                                    @RequestParam(required = false) String sort) throws SQLException {
        sort = isHired(sort);
        return EmployeeService.getEmployeesByManager(managerId, page, size, sort);
    }
    @GetMapping("/by_department/{departmentName}")
    public static List<Employee> employeesByDepartment(@PathVariable() String departmentName,
                                                       @RequestParam(required = false) Integer page,
                                                       @RequestParam(required = false) Integer size,
                                                       @RequestParam(required = false) String sort) throws SQLException {
        sort = isHired(sort);
        Integer departmentId = EmployeeService.getDepartmentId(departmentName);
        return EmployeeService.getEmployeesByDepartment(departmentId,page, size, sort);
    }


    public static String isHired(String sort){
        if (sort != null){
            if (sort.equals("hired")){
                sort = "hiredate";
                return sort;
        }
            return sort;
        }
        return null;

    }


}
