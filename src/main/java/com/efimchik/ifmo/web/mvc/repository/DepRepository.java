package com.efimchik.ifmo.web.mvc.repository;

import com.efimchik.ifmo.web.mvc.mydomain.DepartmentEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DepRepository extends CrudRepository<DepartmentEntity, Long> {
    Optional<DepartmentEntity> findById(Long id);

    Optional<DepartmentEntity> findByName(String name);
}
