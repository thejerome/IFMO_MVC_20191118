package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

@RestController
public class EmployeeController {

    private final ServiceFactoryController serviceFactoryController;

    @Autowired
    public EmployeeController(ServiceFactoryController serviceFactoryController) {
        this.serviceFactoryController = serviceFactoryController;
    }

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAll(@RequestParam(required = false) Integer page,
                                                 @RequestParam(required = false) Integer size,
                                                 @RequestParam(required = false) String sort) {
        String from = buildFrom("SELECT * FROM EMPLOYEE", sort, page, size, null);
        List<Employee> entities = serviceFactoryController.allEmployee(from,false, true);
        assert entities != null;
        return ResponseEntity.ok(entities);
    }

    @GetMapping("/employees/{employee_id}")
    public ResponseEntity<Employee> getById(@PathVariable(name = "employee_id") String employeeId,
                                            @RequestParam(name = "full_chain", required = false, defaultValue = "false") String fullChain) {
        String from = "SELECT * FROM EMPLOYEE WHERE id = " + employeeId;
        if ("true".equals(fullChain)) {
            return ResponseEntity.ok(Objects.requireNonNull(serviceFactoryController.allEmployee(from, true, true)).get(0));
        } else {
            return ResponseEntity.ok(Objects.requireNonNull(serviceFactoryController.allEmployee(from, false, true)).get(0));
        }
    }

    @GetMapping("/employees/by_manager/{managerId}")
    public ResponseEntity<List<Employee>> getByManagerId(@PathVariable(name = "managerId") Integer managerId,
                                                         @RequestParam(required = false) Integer page,
                                                         @RequestParam(required = false) Integer size,
                                                         @RequestParam(required = false) String sort) {
        String from = buildFrom("SELECT * FROM EMPLOYEE WHERE manager = ", sort, page, size, managerId);
        List<Employee> entities = serviceFactoryController.allEmployee(from, false, true);
        assert entities != null;
        return ResponseEntity.ok(entities);
    }

    private static String str(String sort, String newSort, Integer size) {
        return ((sort != null) ? " ORDER BY " + newSort : " ") +
                ((size != null) ? " LIMIT " + size : " ");
    }

    @GetMapping("/employees/by_department/{depIdOrDepName}")
    public ResponseEntity<List<Employee>> getByDepId(@PathVariable(name = "depIdOrDepName") String depIdOrDepName,
                                                     @RequestParam(required = false) Integer page,
                                                     @RequestParam(required = false) Integer size,
                                                     @RequestParam(required = false) String sort) {
        BigInteger depId;
        if(!isNumeric(depIdOrDepName)) {
            String query = "SELECT id FROM DEPARTMENT WHERE name = '" + depIdOrDepName + "'";
            depId = serviceFactoryController.departmentByFrom(query);
        } else {
            depId = BigInteger.valueOf(Integer.parseInt(depIdOrDepName));
        }
        String fixedSort = changeName(sort);
        String from = "SELECT * FROM EMPLOYEE WHERE department = " +
                depId + str(sort, fixedSort, size) +
                ((page != null) ? " OFFSET " + size * page : " ");
        List<Employee> entities = serviceFactoryController.allEmployee(from, false, true);
        assert entities != null;
        return ResponseEntity.ok(entities);

    }

    private static String buildFrom(String from, String sort, Integer page, Integer size, Integer ID){
        String newSort = changeName(sort);
        String s1 = (page != null) ? " OFFSET " + size * page : " ";
        if (ID == null) {
            return from + str(sort, newSort, size) + s1;
        } else {
            return from + ID + str(sort, newSort, size) + s1;
        }
    }

    private static boolean isNumeric(String smth){
        try {
            Integer.parseInt(smth);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static String changeName(String sort) {
        if ("hired".equals(sort))
            return "hiredate";
        return sort;
    }
}