package com.efimchik.ifmo.web.mvc.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;


public interface EmployeeRep extends CrudRepository<RepEmployee, Integer> {
    Iterable<RepEmployee> findAll(Sort sort);
    Page<RepEmployee> findAll(Pageable request);
    Iterable<RepEmployee> findAllByManager(Integer manager);
    Iterable<RepEmployee> findAllByManager(Integer manager, Sort sort);
    Page<RepEmployee> findAllByManager(Integer manager, Pageable pageable);
    Iterable<RepEmployee> findAllByDepartment(Integer department);
    Iterable<RepEmployee> findAllByDepartment(Integer department, Sort sort);
    Page<RepEmployee> findAllByDepartment(Integer department, Pageable pageable);
}
