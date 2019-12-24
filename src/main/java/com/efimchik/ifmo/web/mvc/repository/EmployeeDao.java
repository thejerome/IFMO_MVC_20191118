package com.efimchik.ifmo.web.mvc.repository;

import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class EmployeeDao implements DAO<Employee, Long>, RowMapper<Employee> {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DepartmentDao departmentDao;

    private static final Map<String, String> SORT = new HashMap<>();

    static {
        SORT.put("lastName", "LASTNAME");
        SORT.put("hired", "HIREDATE");
        SORT.put("position", "POSITION");
        SORT.put("salary", "SALARY");
    }

    @Override
    public List<Employee> findAll(String sort) {
        String sqlQuery = "SELECT * FROM EMPLOYEE";
        if (sort != null) {
            sqlQuery = sqlQuery.concat(" ORDER BY " + SORT.get(sort));
        }
        return jdbcTemplate.query(sqlQuery, this::mapRow);
    }

    @Override
    public Employee findById(Long id) {
        return findById(id, false);
    }

    public Employee findById(Long id, boolean chain) {
        String sqlQuery = "SELECT * FROM EMPLOYEE WHERE ID = ?";
        if (chain) {
            return jdbcTemplate.queryForObject(sqlQuery, new Object[]{id}, this::mapRowWithManagerChain);
        } else {
            return jdbcTemplate.queryForObject(sqlQuery, new Object[]{id}, this);
        }
    }

    public List<Employee> findByManager(Long id, String sort) {
        String sqlQuery = "SELECT * FROM EMPLOYEE WHERE MANAGER = ?";
        if (sort != null) {
            sqlQuery = sqlQuery.concat(" ORDER BY " + SORT.get(sort));
        }
        return jdbcTemplate.query(sqlQuery, new Object[]{id}, this::mapRow);
    }

    public List<Employee> findByDepartment(String dep, String sort) {
        Long id;
        try {
            id = Long.parseLong(dep);
        } catch (NumberFormatException nfe) {
            Department department = departmentDao.findByName(dep);
            id = department.getId();
        }
        String sqlQuery = "SELECT * FROM EMPLOYEE WHERE DEPARTMENT = ?";
        if (sort != null) {
            sqlQuery = sqlQuery.concat(" ORDER BY " + SORT.get(sort));
        }
        return jdbcTemplate.query(sqlQuery, new Object[]{id}, this);
    }

    @Override
    public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Employee(
                rs.getLong("ID"),
                new FullName(
                        rs.getString("FIRSTNAME"),
                        rs.getString("LASTNAME"),
                        rs.getString("MIDDLENAME")),
                Position.valueOf(rs.getString("POSITION")),
                LocalDate.parse(rs.getString("HIREDATE")),
                rs.getBigDecimal("SALARY"),
                getManager(rs.getLong("MANAGER")),
                departmentDao.findById(rs.getLong("DEPARTMENT"))
        );
    }

    private Employee mapRowWithManagerChain(ResultSet rs, int rowNum) throws SQLException{
        return new Employee(
                rs.getLong("ID"),
                new FullName(
                        rs.getString("FIRSTNAME"),
                        rs.getString("LASTNAME"),
                        rs.getString("MIDDLENAME")),
                Position.valueOf(rs.getString("POSITION")),
                LocalDate.parse(rs.getString("HIREDATE")),
                rs.getBigDecimal("SALARY"),
                getManager(rs.getLong("MANAGER"), true),
                departmentDao.findById(rs.getLong("DEPARTMENT"))
        );
    }

    private Employee getManager(Long id) {
        return getManager(id, false);
    }

    private Employee getManager(Long id, boolean chain) {
        if (id != 0) {
            if (chain) {
                return findById(id, true);
            } else {
                return jdbcTemplate.queryForObject(
                        "SELECT * FROM EMPLOYEE WHERE ID = ?",
                        new Object[]{id},
                        (rs, rowNum) -> new Employee(
                                rs.getLong("ID"),
                                new FullName(
                                        rs.getString("FIRSTNAME"),
                                        rs.getString("LASTNAME"),
                                        rs.getString("MIDDLENAME")),
                                Position.valueOf(rs.getString("POSITION")),
                                LocalDate.parse(rs.getString("HIREDATE")),
                                rs.getBigDecimal("SALARY"),
                                null,
                                departmentDao.findById(rs.getLong("DEPARTMENT"))
                        ));
            }
        } else return null;
    }
}
