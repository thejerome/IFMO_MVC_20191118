package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.service.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Controller {
    @GetMapping("/employees")
    public List<Employee> employees(@RequestParam(required = false) Integer page,
                                    @RequestParam(required = false) Integer size,
                                    @RequestParam(required = false) String sort){
        if ("hired".equals(sort)){
            sort = "HIREDATE";
        }
        List<Employee> ans = Service.getListOfEmployee(sort);

        return Service.getNeededList(page, size, ans);
    }

    @GetMapping("/employees/{employee_id:[\\d]+}")
    public Employee employee(@PathVariable int employee_id,
                             @RequestParam(required = false) Boolean full_chain,
                             @RequestParam(required = false) String sort){
        if ("hired".equals(sort)){
            sort = "HIREDATE";
        }
        List<Employee> ans =  Service.getListOfEmployeeById(
                "id",
                employee_id,
                full_chain,
                sort);
        if (ans != null){
            return ans.get(0);
        }else{
            return null;
        }

    }

    @GetMapping("/employees/by_manager/{managerId:[\\d]+}")
    public List<Employee> employeeByManager(@PathVariable int managerId,
                                            @RequestParam(required = false) Integer page,
                                            @RequestParam(required = false) Integer size,
                                            @RequestParam(required = false) String sort){
        if ("hired".equals(sort)){
            sort = "HIREDATE";
        }
        List<Employee> ans = Service.getListOfEmployeeById(
                "manager",
                managerId,
                false,
                sort);
        return Service.getNeededList(page, size, ans);
    }

    @GetMapping("/employees/by_department/{department}")
    public List<Employee> employeeByDepartmentById(@PathVariable String department,
                                                   @RequestParam(required = false) Integer page,
                                                   @RequestParam(required = false) Integer size,
                                                   @RequestParam(required = false) String sort){
        if ("hired".equals(sort)){
            sort = "HIREDATE";
        }
        List<Employee> ans;
        if ("ACCOUNTING".equals(department)){
            ans = Service.getListOfEmployeeById("department",
                    Integer.valueOf("10"),
                    false, sort);
        }else if ("RESEARCH".equals(department)){
            ans = Service.getListOfEmployeeById("department",
                    Integer.valueOf("20"),
                    false, sort);
        }else if ("SALES".equals(department)){
            ans = Service.getListOfEmployeeById("department",
                    Integer.valueOf("30"),
                    false, sort);
        }else if ("OPERATIONS".equals(department)){
            ans = Service.getListOfEmployeeById("department",
                    Integer.valueOf("40"),
                    false, sort);
        }else{
            ans = Service.getListOfEmployeeById("department",
                    Integer.valueOf(department),
                    false, sort);
        }
        return Service.getNeededList(page, size, ans);
    }

}
