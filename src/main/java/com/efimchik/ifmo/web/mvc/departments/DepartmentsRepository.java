package com.efimchik.ifmo.web.mvc.departments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentsRepository extends JpaRepository<DepartmentEntity, Long> {

    DepartmentEntity findByName(String name);
}
