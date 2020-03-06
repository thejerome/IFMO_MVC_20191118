package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@RestController
public class MyController {
    @GetMapping(value = "/employees")
    public ResponseEntity<List<Employee>> getAllOfThem(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    ) {
        String sqlQuery = "SELECT * FROM EMPLOYEE" +
                ((sort != null) ? " ORDER BY " + getNormalName(sort) : " ");
        sqlQuery = pageOf(sqlQuery, size, page);
        return ResponseEntity.ok(queryEmployees(sqlQuery));
    }

    @GetMapping(value = "/employees/{employee_id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable(name = "employee_id") String employeeId,
                                            @RequestParam(name = "full_chain", required = false, defaultValue = "false") String fmc) {
        String sqlQuery = "SELECT * FROM EMPLOYEE WHERE id = " + employeeId;
        if ("true".equals(fmc)) {
            return ResponseEntity.ok(Objects.requireNonNull(queryEmployees(sqlQuery, true)).get(0));
        } else {
            return ResponseEntity.ok(queryEmployees(sqlQuery).get(0));
        }
    }

    @GetMapping(value = "/employees/by_manager/{manager}")
    public ResponseEntity<List<Employee>> getByManager(
            @PathVariable(name = "manager") Integer managerId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    ) {

        String sqlQuery = "SELECT * FROM EMPLOYEE WHERE manager = " + managerId + ((sort != null) ? " ORDER BY " + getNormalName(sort) : " ");
        sqlQuery = pageOf(sqlQuery, size, page);
        List<Employee> entities = queryEmployees(sqlQuery);
        return ResponseEntity.ok(entities);
    }

    @GetMapping(value = "/employees/by_department/{dep}")
    public ResponseEntity<List<Employee>> getByDepId(
            @PathVariable(name = "dep") String dep,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    ) throws SQLException {
        BigInteger depId;
        if(isInt(dep)) {
            depId = BigInteger.valueOf(Integer.parseInt(dep));
        } else {
            String query = "SELECT id FROM DEPARTMENT WHERE name = '" + dep + "'";
            depId = getDepartmentId(query);
        }
        String sqlQuery = "SELECT * FROM EMPLOYEE WHERE department = " +
                depId +
                ((sort != null) ? " ORDER BY " + getNormalName(sort) : " ");
        sqlQuery = pageOf(sqlQuery, size, page);
        return ResponseEntity.ok(queryEmployees(sqlQuery));

    }

    private String getNormalName(String sort) {
        if ("hired".equals(sort)) {
            return "HIREDATE";
        }
        return sort;
    }

    private static boolean isInt(String str) {
        if (str == null) {
            return false;
        }
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private List<Employee> queryEmployees(String sql) {
        return queryEmployees(sql, false);
    }

    private String pageOf(String sql, Integer size, Integer page) {
        String newSql = sql + ((size != null) ? " LIMIT " + size : " ") + ((page != null) ? " OFFSET " + size * page : " ");
        return newSql;
    }

    private List<Employee> queryEmployees(String sql, boolean fmc) {
        try (Connection connection = DBC.getInstance().getConnection();
             Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
             ResultSet resultSet = statement.executeQuery(sql);
             System.out.println(resultSet);
             return new ListMapperFactory().employeeListMapper().mapList(resultSet, fmc);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BigInteger getDepartmentId(String sql) throws SQLException {
        try {
            ResultSet resultSet = DBC.getInstance().getConnection().createStatement().executeQuery(sql);
            if (resultSet.next()) {
                return new BigInteger(resultSet.getString("id"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
