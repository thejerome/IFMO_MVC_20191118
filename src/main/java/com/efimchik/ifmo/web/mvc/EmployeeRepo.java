package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Employee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepo extends PagingAndSortingRepository<Employee, Long> {
    List<Employee> findByManagerId(Long managerId, Pageable pageRequest);

    List<Employee> findByDepartmentId(Long departmentId, Pageable pageable);

    List<Employee> findByDepartmentName(String  departmentName, Pageable pageable);
}
