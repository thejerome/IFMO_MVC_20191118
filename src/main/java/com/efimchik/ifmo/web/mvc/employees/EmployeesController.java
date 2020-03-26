package com.efimchik.ifmo.web.mvc.employees;

import com.efimchik.ifmo.web.mvc.departments.DepartmentEntity;
import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/employees")
public class EmployeesController {

    private static final Map<String, EmployeesService.Sorting>  stringSortingMap = new HashMap<>();

    static {
        stringSortingMap.put("lastName", EmployeesService.Sorting.LAST_NAME);
        stringSortingMap.put("hired", EmployeesService.Sorting.HIRED);
        stringSortingMap.put("position", EmployeesService.Sorting.POSITION);
        stringSortingMap.put("salary", EmployeesService.Sorting.SALARY);
    }

    private final EmployeesService service;

    public EmployeesController(EmployeesService service) {
        this.service = service;
    }

    @GetMapping
    public List<Employee> get(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    ) {
        return service.findAll(page, size, sort == null ? null : stringSortingMap.get(sort))
                .map(entity -> entityToDto(entity, false))
                .collect(Collectors.toList());
    }

    @GetMapping("{employeeId}")
    public Employee get(
            @PathVariable long employeeId,
            @RequestParam(name = "full_chain", required = false, defaultValue = "false") boolean fullChain
    ) {
        return service.findById(employeeId, fullChain)
                .map(entity -> entityToDto(entity, fullChain))
                .orElse(null);
    }

    @GetMapping("by_manager/{managerId}")
    public List<Employee> getByManager(
            @PathVariable long managerId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    ) {
        return service.findByManagerId(managerId, page, size, sort == null ? null : stringSortingMap.get(sort))
                .map(entity -> entityToDto(entity, false)).collect(Collectors.toList());
    }

    @GetMapping("by_department/{department}")
    public List<Employee> getByDepartment(
            @PathVariable String department,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    ) {
        try {
            final long departmentId = Long.parseLong(department);

            return service.findByDepartmentId(departmentId, page, size, sort == null ? null : stringSortingMap.get(sort))
                    .map(entity -> entityToDto(entity, false)).collect(Collectors.toList());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return service.findByDepartmentName(department, page, size, sort == null ? null : stringSortingMap.get(sort))
                .map(entity -> entityToDto(entity, false)).collect(Collectors.toList());
    }

    private static Employee entityToDto(EmployeeEntity entity, boolean fullChain) {
        if (entity == null) {
            return null;
        }

        return new Employee(
                entity.getId(),
                new FullName(entity.getFirstName(), entity.getLastName(), entity.getMiddleName()),
                entity.getPosition(),
                entity.getHireDate(),
                entity.getSalary(),
                fullChain ? entityToDto(entity.getManager(), true) : entityToDtoLast(entity.getManager()),
                entityToDto(entity.getDepartment())
        );
    }

    private static Employee entityToDtoLast(EmployeeEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Employee(
                entity.getId(),
                new FullName(entity.getFirstName(), entity.getLastName(), entity.getMiddleName()),
                entity.getPosition(),
                entity.getHireDate(),
                entity.getSalary(),
                null,
                entityToDto(entity.getDepartment())
        );
    }

    private static Department entityToDto(DepartmentEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Department(entity.getId(), entity.getName(), entity.getLocation());
    }
}
