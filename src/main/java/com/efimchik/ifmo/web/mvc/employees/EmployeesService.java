package com.efimchik.ifmo.web.mvc.employees;

import com.efimchik.ifmo.web.mvc.departments.DepartmentsRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Stream;

@Service
@Transactional
public class EmployeesService {

    public enum Sorting {

        LAST_NAME("lastName"),
        HIRED("hireDate"),
        POSITION("position"),
        SALARY("salary");

        private final String field;

        Sorting(String field) {
            this.field = field;
        }
    }

    private final EmployeesRepository repository;

    private final DepartmentsRepository departmentsRepository;

    public EmployeesService(EmployeesRepository repository, DepartmentsRepository departmentsRepository) {
        this.repository = repository;
        this.departmentsRepository = departmentsRepository;
    }

    public Stream<EmployeeEntity> findAll(Integer page, Integer size, Sorting sort) {
        return repository.findAll(pageable(page, size, sort)).stream();
    }

    public Optional<EmployeeEntity> findById(long id, boolean fullChain) {
        final Optional<EmployeeEntity> employee = repository.findById(id);

        if (!fullChain) {
            return employee;
        }

        for (Optional<EmployeeEntity> entity = employee; entity.isPresent(); ) {
            entity = Optional.ofNullable(entity.get().getManager());
        }

        return employee;
    }

    public Stream<EmployeeEntity> findByManagerId(long managerId, Integer page, Integer size, Sorting sort) {
        return repository.findByManager(repository.getOne(managerId), pageable(page, size, sort)).stream();
    }

    public Stream<EmployeeEntity> findByDepartmentId(long departmentId, Integer page, Integer size, Sorting sort) {
        return repository.findByDepartment(departmentsRepository.getOne(departmentId), pageable(page, size, sort)).stream();
    }

    public Stream<EmployeeEntity> findByDepartmentName(String departmentName, Integer page, Integer size, Sorting sort) {
        return repository.findByDepartment(departmentsRepository.findByName(departmentName), pageable(page, size, sort)).stream();
    }

    private Pageable pageable(Integer page, Integer size, Sorting sort) {
        final Pageable pageable;

        if (page != null && size != null) {
            if (sort != null) {
                pageable = PageRequest.of(page, size, Sort.by(sort.field));
            } else {
                pageable = PageRequest.of(page, size);
            }
        } else {
            pageable = Pageable.unpaged();
        }

        return pageable;
    }
}
