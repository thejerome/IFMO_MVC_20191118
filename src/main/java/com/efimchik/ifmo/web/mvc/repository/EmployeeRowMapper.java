package com.efimchik.ifmo.web.mvc.repository;

import com.efimchik.ifmo.web.mvc.domain.Employee;

import java.sql.ResultSet;

public interface EmployeeRowMapper extends RowMapper<Employee> {
    Employee mapRow(ResultSet resultSet, boolean fullManagerChain);
}
