package com.efimchik.ifmo.web.mvc.repos;

import org.springframework.data.repository.CrudRepository;

public interface DepartmentRep extends CrudRepository<RepDepartment, Integer> {
    RepDepartment findByName(String name);
}
