package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class Controller {

    @Autowired
    private EmployeeRepo employeeRepo;

    private String getRightSortName(String sort) {
        if (sort == null) {
            return null;
        }
        if (sort.equalsIgnoreCase("lastname")) {
            return "fullName.lastName";
        }
        return sort;
    }

    private Employee clone(Employee e) {
        if (e == null) {
            return null;
        }
        Department department;
        if (e.getDepartment() != null) {
            department = new Department(
                    e.getDepartment().getId(),
                    e.getDepartment().getName(),
                    e.getDepartment().getLocation()
            );
        } else {
            department = null;
        }
        return new Employee(e.getId(),
                new FullName(e.getFullName().getFirstName(),
                        e.getFullName().getLastName(),
                        e.getFullName().getMiddleName()),
                Position.valueOf(e.getPosition().name()),
                LocalDate.parse(e.getHired().toString()),
                BigDecimal.valueOf(e.getSalary().doubleValue()),
                clone(e.getManager()),
                department);
    }

    @GetMapping("/employees")
    List<Employee> getAll(@RequestParam(required = false, defaultValue = "0") Integer page,
                          @RequestParam(required = false, defaultValue = "30") Integer size,
                          @RequestParam(required = false, defaultValue = "id") String sort) {
        sort = getRightSortName(sort);
        List<Employee> employees = employeeRepo.findAll(PageRequest.of(page, size, Sort.by(sort))).getContent();
        return employeesWithoutChain(employees);
    }

    private List<Employee> employeesWithoutChain(List<Employee> employees) {
        List<Employee> answer = new ArrayList<>();
        for (Employee e : employees) {
            Employee cl = clone(e);
            if (cl.getManager() != null) {
                cl.getManager().setManager(null);
            }
            answer.add(cl);
        }
        return answer;
    }

    @GetMapping("/employees/{id}")
    Optional<Employee> getById(@PathVariable Long id, @RequestParam(required = false, defaultValue = "false") String full_chain) {
        if (full_chain.equals("true")) {
            return employeeRepo.findById(id);
        } else {
            Optional<Employee> employee = employeeRepo.findById(id);
            if (employee.isPresent()) {
                if (employee.get().getManager() != null) {
                    employee.get().getManager().setManager(null);
                }
            }
            return employee;
        }
    }

    @GetMapping("/employees/by_manager/{managerId}")
    List<Employee> getByManagerId(@PathVariable Long managerId,
                                  @RequestParam(required = false, defaultValue = "0") Integer page,
                                  @RequestParam(required = false, defaultValue = "30") Integer size,
                                  @RequestParam(required = false, defaultValue = "id") String sort) {
        if (sort != null)
            sort = getRightSortName(sort);
        List<Employee> employees = employeeRepo.findByManagerId(managerId, PageRequest.of(page, size, Sort.by(sort)));
        return employeesWithoutChain(employees);
    }

    @GetMapping("/employees/by_department/{department}")
    List<Employee> getByDep(@PathVariable String department,
                                   @RequestParam(required = false, defaultValue = "0") Integer page,
                                   @RequestParam(required = false, defaultValue = "30") Integer size,
                                   @RequestParam(required = false, defaultValue = "id") String sort) {
        if (sort != null)
            sort = getRightSortName(sort);
        List<Employee> employees;
        if (department.charAt(0) >= '0' && department.charAt(0) <= '9') {
            employees = employeeRepo.findByDepartmentId(new Long(department), PageRequest.of(page, size, Sort.by(sort)));
        } else {
            employees = employeeRepo.findByDepartmentName(department, PageRequest.of(page, size, Sort.by(sort)));
        }
        return employeesWithoutChain(employees);
    }
}