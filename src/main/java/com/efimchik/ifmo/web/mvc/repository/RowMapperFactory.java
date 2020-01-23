package com.efimchik.ifmo.web.mvc.repository;

import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RowMapperFactory {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public EmployeeRowMapper employeeRowMapper() {
        return new EmployeeRowMapper() {

            @Override
            public Employee mapRow(ResultSet resultSet, boolean fullManagerChain) {
                try {
                    Employee manager = null;
                    Department department = null;
                    if (resultSet.getLong("DEPARTMENT") != 0) {
                        department = jdbcTemplate.queryForObject("SELECT * FROM DEPARTMENT WHERE ID = " +
                                        resultSet.getString("DEPARTMENT"),
                                (rs, rowNum) -> departmentRowMapper().mapRow(rs));
                    }
                    long managerId = resultSet.getLong("MANAGER");
                    if (managerId != 0) {
                        if (fullManagerChain) {
                            manager = jdbcTemplate.queryForObject(
                                    "SELECT * FROM EMPLOYEE WHERE ID = " + managerId,
                                    (rs, rowNum) -> mapRow(rs, true));
                        } else {
                            manager = jdbcTemplate.queryForObject(
                                    "SELECT * FROM EMPLOYEE WHERE ID = " + managerId,
                                    (rs, rowNum) -> {
                                        Department department1 = null;
                                        if (rs.getObject("DEPARTMENT") != null) {
                                            department1 = jdbcTemplate.queryForObject(
                                                    "SELECT  * FROM DEPARTMENT WHERE ID = " +
                                                            rs.getString("DEPARTMENT"),
                                                    (rs1, rowNum1) -> departmentRowMapper().mapRow(rs1));
                                        }
                                        return new Employee(
                                                rs.getLong("ID"),
                                                new FullName(
                                                        rs.getString("FIRSTNAME"),
                                                        rs.getString("LASTNAME"),
                                                        rs.getString("MIDDLENAME")
                                                ),
                                                Position.valueOf(rs.getString("POSITION")),
                                                rs.getDate("HIREDATE").toLocalDate(),
                                                rs.getBigDecimal("SALARY"),
                                                null,
                                                department1
                                        );
                                    });
                        }
                    }
                    return new Employee(
                            resultSet.getLong("ID"),
                            new FullName(
                                    resultSet.getString("FIRSTNAME"),
                                    resultSet.getString("LASTNAME"),
                                    resultSet.getString("MIDDLENAME")
                            ),
                            Position.valueOf(resultSet.getString("POSITION")),
                            resultSet.getDate("HIREDATE").toLocalDate(),
                            resultSet.getBigDecimal("SALARY"),
                            manager,
                            department
                    );
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public Employee mapRow(ResultSet resultSet) {
                return mapRow(resultSet, false);
            }
        };
    }

    public DepartmentRowMapper departmentRowMapper() {
        return resultSet -> {
            try {
                return new Department(
                        resultSet.getLong("ID"),
                        resultSet.getString("NAME"),
                        resultSet.getString("LOCATION"));
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        };
    }
}
