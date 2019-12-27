package com.efimchik.ifmo.web.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import com.efimchik.ifmo.web.mvc.domain.Employee;

import java.util.List;


@RestController
@SpringBootApplication
public class MvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(MvcApplication.class, args);
    }

    @GetMapping("/employees")
    public List<Employee> employees(@RequestParam(required = false) Integer page,
                                    @RequestParam(required = false) Integer size,
                                    @RequestParam(required = false) String sort){
        List<Employee> ans = Service.getListOfEmployee(sort);

        return getNeededList(page, size, ans);
    }

    @GetMapping("/employees/{employee_id:[\\d]+}")
    public Employee employee(@PathVariable int employee_id,
                             @RequestParam(required = false) Boolean isFull_chain,
                                   @RequestParam(required = false) String sort){
            return Service.getListOfEmployeeById(
                    "id",
                    employee_id,
                    isFull_chain,
                    sort)
                    .get(0);
    }

    @GetMapping("/employees/by_manager/{managerId:[\\d]+}")
    public List<Employee> employeeByManager(@PathVariable int managerId,
                                            @RequestParam(required = false) Integer page,
                                            @RequestParam(required = false) Integer size,
                                            @RequestParam(required = false) String sort){
        List<Employee> ans = Service.getListOfEmployeeById(
                    "manager",
                    managerId,
                    false,
                    sort);
        return getNeededList(page, size, ans);
    }

    @GetMapping("/employees/by_department/{department}")
    public List<Employee> employeeByDepartmentById(@PathVariable String department,
                                                   @RequestParam(required = false) Integer page,
                                                   @RequestParam(required = false) Integer size,
                                                   @RequestParam(required = false) String sort){
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
        return getNeededList(page, size, ans);
    }

    private List<Employee> getNeededList (Integer page, Integer size, List<Employee> ans){
        if (page != null && size != null){
            int from = page * size;
            int to = ((page + 1) * size < ans.size())? (page + 1) * size : ans.size();
            return ans.subList(from, to);
        }else{
            return ans;
        }
    }
}



