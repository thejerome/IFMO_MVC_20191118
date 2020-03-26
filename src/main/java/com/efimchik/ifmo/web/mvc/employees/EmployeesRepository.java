package com.efimchik.ifmo.web.mvc.employees;

import com.efimchik.ifmo.web.mvc.departments.DepartmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeesRepository extends JpaRepository<EmployeeEntity, Long> {

    Page<EmployeeEntity> findByManager(EmployeeEntity manager, Pageable pageable);

    Page<EmployeeEntity> findByDepartment(DepartmentEntity department, Pageable pageable);
}