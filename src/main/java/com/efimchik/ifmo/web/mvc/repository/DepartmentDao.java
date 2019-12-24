package com.efimchik.ifmo.web.mvc.repository;

import com.efimchik.ifmo.web.mvc.domain.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class DepartmentDao implements DAO<Department, Long>, RowMapper<Department> {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public List<Department> findAll(String sort) {
        return jdbcTemplate.query("SELECT * FROM DEPARTMENT", this);
    }

    @Override
    public Department findById(Long id) {
        if (id != 0) {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM DEPARTMENT WHERE ID = ?", new Object[]{id}, this);
        } else return null;
    }

    public Department findByName(String name) {
        return jdbcTemplate.queryForObject("SELECT * FROM DEPARTMENT WHERE NAME = ?", new Object[]{name}, this);
    }

    @Override
    public Department mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Department(
                rs.getLong("ID"),
                rs.getString("NAME"),
                rs.getString("LOCATION")
        );
    }
}
