package com.efimchik.ifmo.web.mvc.repository;

import com.efimchik.ifmo.web.mvc.mydomain.EmployeeEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpRepository extends JpaRepository<EmployeeEntity, Long> {
    Optional<EmployeeEntity> findById(Long id);

    List<EmployeeEntity> findAllByManager(Long manager, Pageable pageable);

    Iterable<EmployeeEntity> findAllByManager(Long manager);

    List<EmployeeEntity> findAllByDepartment(Long department, Pageable pageable);
}
