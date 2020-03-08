package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.db.DepartmentEntity;
import com.efimchik.ifmo.web.mvc.db.DepartmentRepository;
import com.efimchik.ifmo.web.mvc.db.EmployeeEntity;
import com.efimchik.ifmo.web.mvc.db.EmployeeRepository;
import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional
@RestController
@RequestMapping("/employees")
public class MvcLabController {

    private final EmployeeRepository repository;
    private final DepartmentRepository departmentRepository;

    public MvcLabController(EmployeeRepository repository, DepartmentRepository departmentRepository) {
        this.repository = repository;
        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    public List<Employee> get(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    ) {
        Pageable pagination = pagination(page, size, sort);

        Stream<EmployeeEntity> stream;
        if (pagination.isUnpaged() && sort != null) {
            stream = repository.findAll(sorting(sort)).stream();
        } else {
            stream = repository.findAll(pagination).stream();
        }

        return stream.map(entity -> mapToDto(entity, false))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id:[0-9]+}")
    public Employee get(@PathVariable long id, @RequestParam(name = "full_chain", defaultValue = "false") boolean fullChain) {
        return repository.findById(id).map(entity -> mapToDto(entity, fullChain)).orElse(null);
    }

    @GetMapping("/by_manager/{id:\\d+}")
    public List<Employee> getByManager(
            @PathVariable long id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    ) {
        return getByExample(Example.of(new EmployeeEntity(repository.getOne(id))), page, size, sort);
    }

    @GetMapping("/by_department/{id:\\d+}")
    public List<Employee> getByDepartment(
            @PathVariable long id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    ) {
        return getByExample(Example.of(new EmployeeEntity(departmentRepository.getOne(id))), page, size, sort);
    }

    @GetMapping("/by_department/{name:[a-zA-Z][\\da-zA-Z]*|[\\da-zA-Z]+[a-zA-Z]}")
    public List<Employee> getByDepartment(
            @PathVariable String name,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    ) {
        DepartmentEntity department = departmentRepository
                .findOne(Example.of(new DepartmentEntity(name)))
                .orElse(null);

        return getByExample(Example.of(new EmployeeEntity(department)), page, size, sort);
    }

    private List<Employee> getByExample(
            Example<EmployeeEntity> example,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    ) {
        Pageable pagination = pagination(page, size, sort);

        Stream<EmployeeEntity> stream;
        if (pagination.isUnpaged() && sort != null) {
            stream = repository.findAll(example, sorting(sort)).stream();
        } else {
            stream = repository.findAll(example, pagination).stream();
        }

        return stream.map(entity -> mapToDto(entity, false))
                .collect(Collectors.toList());
    }

    private Employee mapToDto(EmployeeEntity entity, boolean fullChain) {
        if (entity == null) {
            return null;
        }

        if (!fullChain) {
            return new Employee(entity.getId(), new FullName(entity.getFirstname(), entity.getLastname(), entity.getMiddlename()), entity.getPosition(), entity.getHiredate(), entity.getSalary(), mapToDto(entity.getManager()), mapToDto(entity.getDepartment()));
        }

        return new Employee(entity.getId(), new FullName(entity.getFirstname(), entity.getLastname(), entity.getMiddlename()), entity.getPosition(), entity.getHiredate(), entity.getSalary(), mapToDto(entity.getManager(), true), mapToDto(entity.getDepartment()));
    }

    private Employee mapToDto(EmployeeEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Employee(entity.getId(), new FullName(entity.getFirstname(), entity.getLastname(), entity.getMiddlename()), entity.getPosition(), entity.getHiredate(), entity.getSalary(), null, mapToDto(entity.getDepartment()));
    }

    private Department mapToDto(DepartmentEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Department(entity.getId(), entity.getName(), entity.getLocation());
    }

    private Sort sorting(String sort) {
        switch (sort) {
            case "lastName":
                return Sort.by("lastname");

            case "hired":
                return Sort.by("hiredate");

            default:
                return Sort.by(sort);
        }
    }

    private Pageable pagination(Integer page, Integer size, String sort) {
        if (page == null || size == null) {
            return Pageable.unpaged();
        }

        if (sort != null) {
            return PageRequest.of(page, size, sorting(sort));
        }

        return PageRequest.of(page, size);
    }
}
