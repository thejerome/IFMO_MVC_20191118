package com.efimchik.ifmo.web.mvc.repository;

import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RowMapperFactory rowMapperFactory;

    public List<Employee> getAll() {
        return jdbcTemplate.query(
                "SELECT * FROM EMPLOYEE",
                (rs, rowNum) -> rowMapperFactory.employeeRowMapper().mapRow(rs));
    }

    public Employee getById(Long id) {
        return getById(id, false);
    }

    public Employee getById(Long id, boolean fullChain) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM EMPLOYEE WHERE ID = " + id,
                (rs, rowNum) -> rowMapperFactory.employeeRowMapper().mapRow(rs, fullChain));
    }

    public List<Employee> getByManager(Long id) {
        return jdbcTemplate.query(
                "SELECT * FROM EMPLOYEE WHERE MANAGER = " + id,
                (rs, rowNum) -> rowMapperFactory.employeeRowMapper().mapRow(rs));
    }

    public List<Employee> getByDepartment(Long id) {
        return jdbcTemplate.query(
                "SELECT * FROM EMPLOYEE WHERE DEPARTMENT = " + id,
                (rs, rowNum) -> rowMapperFactory.employeeRowMapper().mapRow(rs));
    }

    public List<Employee> getByDepartment(String name) {
        Department department = jdbcTemplate.queryForObject("SELECT * FROM DEPARTMENT WHERE NAME = '" + name + "'",
                (rs, rowNum) -> rowMapperFactory.departmentRowMapper().mapRow(rs));
        if (department != null) {
            return getByDepartment(department.getId());
        } else return null;
    }
}
