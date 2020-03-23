package com.efimchik.ifmo.web.mvc.domain;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

@RestController
public class Core {
    private ResultSet getResultSet(String sql) {
        try {
            return createConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Core() throws Exception {
        Class.forName("org.h2.Driver");
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
    }

    private Employee empMapRow(ResultSet resultSet, boolean flag, boolean MainManager) {
        try {
            Long id = Long.valueOf(resultSet.getString("ID"));
            FullName fullName = new FullName(
                    resultSet.getString("FIRSTNAME"),
                    resultSet.getString("LASTNAME"),
                    resultSet.getString("MIDDLENAME")
            );
            Position position = Position.valueOf(resultSet.getString("POSITION"));
            LocalDate hireDate = LocalDate.parse(resultSet.getString("HIREDATE"));
            BigDecimal salary = new BigDecimal(String.valueOf(resultSet.getInt("SALARY")));
            Employee manager = null;

            if (!flag) {
                int managerId = resultSet.getInt("MANAGER");
                ResultSet managerResultSet = getResultSet("SELECT * FROM employee WHERE id=" + managerId);
                assert managerResultSet != null;
                if (managerResultSet.next()) {
                    manager = empMapRow(managerResultSet, !MainManager, MainManager);
                }
            }

            Department department = null;
            int departmentId = resultSet.getInt("DEPARTMENT");

            ResultSet departmentResultSet = getResultSet("SELECT * FROM department WHERE id=" + departmentId);
            assert departmentResultSet != null;

            if (departmentResultSet.next()) {
                Long id1 = Long.valueOf(departmentResultSet.getString("ID"));
                String name = departmentResultSet.getString("NAME");
                String location = departmentResultSet.getString("LOCATION");
                department = new Department(id1, name, location);
            }

            return new Employee(id, fullName, position, hireDate, salary, manager, department);
        } catch (SQLException ex) {
            return null;
        }
    }

    private List<Employee> getEmployees(String req) throws SQLException {
        List<Employee> allEmployees = new ArrayList<Employee>();
        ResultSet resultSet = getResultSet(req);
        if (resultSet != null) {
            while (resultSet.next()) {
                allEmployees.add(empMapRow(resultSet, false, false));
            }
            return allEmployees;
        }
        return null;
    }

    private List<Department> getDepartments(String req) throws SQLException {
        List<Department> allDepartments = new ArrayList<Department>();
        ResultSet resultSet = getResultSet(req);
        assert resultSet != null;
        while (resultSet.next()) {
            Long id = Long.valueOf(resultSet.getString("ID"));
            String name = resultSet.getString("NAME");
            String location = resultSet.getString("LOCATION");
            allDepartments.add(new Department(id, name, location));
        }
        return allDepartments;
    }

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort) throws SQLException {
        String sort1 = sort;
        if (sort1 != null && sort1.equals("hired")) {
            sort1 = "HIREDATE";
        }
        String request;
        final String s = (page != null) ? " OFFSET " + size * page : " ";
        if (sort1 != null) {
            if (size != null) {
                request = "SELECT * FROM employee " +
                        "ORDER BY " + sort1 + " " +
                        "LIMIT " + size + s;
            } else {
                request = "SELECT * FROM employee " +
                        "ORDER BY " + sort1 + " " + s;
            }
        } else if (size != null) {
            request = "SELECT * FROM employee " +
                    "LIMIT " + size + s;
        } else {
            request = "SELECT * FROM employee " + s;
        }
        return new ResponseEntity<List<Employee>>(getEmployees(request), HttpStatus.OK);
    }

    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<Employee> getEmployeeByid(
            @PathVariable(name = "employeeId") String employeeId,
            @RequestParam(name = "full_chain", required = false, defaultValue = "false") String fullChain) throws SQLException {
        ResultSet resultSet = getResultSet("SELECT * FROM employee WHERE id=" + Integer.parseInt(employeeId));
        assert resultSet != null;
        if (resultSet.next()) {
            return new ResponseEntity<Employee>(empMapRow(resultSet, false, Boolean.parseBoolean(fullChain)), HttpStatus.OK);
        }
        return null;
    }

    @GetMapping("/employees/by_manager/{managerId}")
    public ResponseEntity<List<Employee>> getEmployeesByManagerId(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @PathVariable Integer managerId) throws SQLException {
        String sort1 = sort;
        if (sort1 != null && sort1.equals("hired")) {
            sort1 = "HIREDATE";
        }
        String request;
        String s = (page != null) ? " OFFSET " + size * page : " ";
        if (sort1 != null) {
            if (size != null) {
                request = "SELECT * FROM employee WHERE manager=" + managerId + " " +
                        "ORDER BY " + sort1 + " " +
                        "LIMIT " + size + s;
            } else {
                request = "SELECT * FROM employee WHERE manager=" + managerId + " " +
                        "ORDER BY " + sort1 + s;
            }
        } else if (size != null) {
            request = "SELECT * FROM employee WHERE manager=" + managerId + " " +
                    "LIMIT " + size + s;
        } else {
            request = "SELECT * FROM employee WHERE manager=" + managerId + s;
        }
        return new ResponseEntity<List<Employee>>(getEmployees(request), HttpStatus.OK);
    }

    @GetMapping("/employees/by_department/{dep}")
    public ResponseEntity<List<Employee>> getEmployeesByDep(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @PathVariable String dep) throws SQLException {
        String sort1 = sort;
        Long departmentId;
        try {
            departmentId = Long.parseLong(dep);
        } catch (NumberFormatException ex) {
            Department departments = getDepartments("SELECT * FROM department WHERE name='" + dep +"'").get(0);
            departmentId = departments.getId();
        }
        if (sort1 != null && sort1.equals("hired")) {
            sort1 = "HIREDATE";
        }
        String request;
        if (sort1 != null) {
            if (size != null) {
                if (page != null) {
                    request = "SELECT * FROM employee WHERE department=" + departmentId + " " +
                            "ORDER BY " + sort1 + " " +
                            "LIMIT " + size + " " +
                            String.format("OFFSET %d", size * page);
                } else {
                    request = "SELECT * FROM employee WHERE department=" + departmentId + " " +
                            "ORDER BY " + sort1 + " " +
                            "LIMIT " + size;
                }
            } else if (page != null) {
                request = "SELECT * FROM employee WHERE department=" + departmentId + " " +
                        "ORDER BY " + sort1 + " " +
                        String.format("OFFSET %d", size * page);
            } else {
                request = "SELECT * FROM employee WHERE department=" + departmentId + " " +
                        "ORDER BY " + sort1;
            }
        } else if (size != null) {
            request = "SELECT * FROM employee WHERE department=" + departmentId + " " +
                    "LIMIT " + size + " " +
                    (page != null ? String.format("OFFSET %d", size * page) : "");
        } else if (page != null) {
            request = "SELECT * FROM employee WHERE department=" + departmentId + " " + String.format("OFFSET %d", size * page);
        } else {
            request = "SELECT * FROM employee WHERE department=" + departmentId;
        }
        return new ResponseEntity<List<Employee>>(getEmployees(request), HttpStatus.OK);
    }
}