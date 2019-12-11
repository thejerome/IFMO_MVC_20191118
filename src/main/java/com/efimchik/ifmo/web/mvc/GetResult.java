package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class GetResult {
    public GetResult() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement getStatement(String s) throws SQLException {
        return createConnection().prepareStatement(s);
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
    }

    private List<Department> mapSetDepartments(ResultSet resultSet) {
        List<Department> departments = new ArrayList<>();
        try {
            while (resultSet.next()) {
                departments.add(
                        new Department(
                                resultSet.getLong("ID"),
                                resultSet.getString("NAME"),
                                resultSet.getString("LOCATION")
                        )
                );
            }
        } catch (SQLException ignored) {
        }
        return departments;
    }


    private Employee employeeRowMapper(ResultSet resultSet, boolean isFir) {
        try {
            Employee manager = null;
            Department department = null;
            if (resultSet.getString("MANAGER") != null) {
                if (isFir)
                    manager = getMan(resultSet, resultSet.getInt("MANAGER"));
            }

            if (resultSet.getString("DEPARTMENT") != null) {
                department = getDepartment(resultSet.getString("DEPARTMENT"));
            }

            return new Employee(
                    resultSet.getLong("ID"),
                    new FullName(resultSet.getString("FIRSTNAME"),
                            resultSet.getString("LASTNAME"),
                            resultSet.getString("MIDDLENAME")),
                    Position.valueOf(resultSet.getString("POSITION")),
                    LocalDate.parse(resultSet.getString("HIREDATE")),
                    new BigDecimal(resultSet.getDouble("SALARY")),
                    manager,
                    department
            );
        } catch (SQLException ignored) {
            return null;
        }
    }

    private Employee getMan(ResultSet res, Integer id) {
        try {
            ResultSet resultSet = getResultSet("select * from employee");
            Employee man = null;
            int row = resultSet.getRow();
            resultSet.beforeFirst();
            while (resultSet.next()) {
                if (resultSet.getString("ID").equals(String.valueOf(id))) {
                    man = employeeRowMapper(resultSet, false);
                    break;
                }
            }
            resultSet.absolute(row);
            return man;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    private List<Employee> mapSet(ResultSet resultSet) {
        List<Employee> employees = new ArrayList<>();
        try {
            while (resultSet.next()) {
                employees.add(employeeRowMapper(resultSet, true));
            }
        } catch (SQLException ignored) {
        }
        return employees;
    }

    private ResultSet getResultSet(String SQL) throws SQLException {
        Statement statement = createConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        return statement.executeQuery(SQL);
    }

    private Employee employeeRowMapperChain(ResultSet resultSet) {
        try {
            Employee manager = null;
            Department department = null;
            if (resultSet.getString("MANAGER") != null) {
                manager = getManChain(resultSet, resultSet.getInt("MANAGER"));
            }

            if (resultSet.getString("DEPARTMENT") != null) {
                department = getDepartment(resultSet.getString("DEPARTMENT"));
            }

            return new Employee(
                    resultSet.getLong("ID"),
                    new FullName(resultSet.getString("FIRSTNAME"),
                            resultSet.getString("LASTNAME"),
                            resultSet.getString("MIDDLENAME")),
                    Position.valueOf(resultSet.getString("POSITION")),
                    LocalDate.parse(resultSet.getString("HIREDATE")),
                    new BigDecimal(resultSet.getDouble("SALARY")),
                    manager,
                    department
            );
        } catch (SQLException ignored) {
            return null;
        }
    }

    private Department getDepartment(String department) {
        try {
            ResultSet res = getResultSet("select * from department where id = " + department);
            return mapSetDepartments(res).get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Employee getManChain(ResultSet res, Integer id) {
        try {
            ResultSet resultSet = getResultSet("select * from employee");
            Employee man = null;
            int row = resultSet.getRow();
            resultSet.beforeFirst();
            while (resultSet.next()) {
                if (resultSet.getString("ID").equals(String.valueOf(id))) {
                    man = employeeRowMapperChain(resultSet);
                    break;
                }
            }
            resultSet.absolute(row);
            return man;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees(@RequestParam(required = false) Integer page,
                                                          @RequestParam(required = false) Integer size,
                                                          @RequestParam(required = false) String sort) throws SQLException {
        if (sort != null && sort.equals("hired")) {
            sort = "HIREDATE";
        }
        String s = "select * from EMPLOYEE ";
        if (sort != null)
            s += " order by " + sort + " ";
        if (size != null)
            s += " limit " + size + " ";
        if (page != null)
            s += " offset " + size * page + " ";
        ResultSet resultSet = getStatement(s).executeQuery();
        return new ResponseEntity<>( mapSet(resultSet), HttpStatus.OK);
    }


    @GetMapping("/employees/{employee_id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable(name = "employee_id") String employeeId,
                                                    @RequestParam(name = "full_chain", required = false, defaultValue = "false") String fullChain) throws SQLException {
        String s = "select * from employee where id = " + employeeId;
        ResultSet resultSet = getStatement(s).executeQuery();
        if (!resultSet.next()){
            return ResponseEntity.of(Optional.empty());
        }

        if (fullChain != null && fullChain.equals("true")) {
            return new ResponseEntity<>(
                    employeeRowMapperChain(resultSet),
                    HttpStatus.OK
            );
        } else {
            return new ResponseEntity<>(employeeRowMapper(resultSet, true), HttpStatus.OK);
        }
    }


    @GetMapping("/employees/by_manager/{managerId}")
    public ResponseEntity<List<Employee>> getEmployeesByManagerId(@RequestParam(required = false) Integer page,
                                                                  @RequestParam(required = false) Integer size,
                                                                  @RequestParam(required = false) String sort,
                                                                  @PathVariable Integer managerId) throws SQLException {
        if (sort != null && sort.equals("hired")) {
            sort = "HIREDATE";
        }
        String s = "select * from EMPLOYEE where MANAGER = " + managerId + " ";
        if (sort != null)
            s += " order by " + sort + " ";
        if (size != null)
            s += " limit " + size + " ";
        if (page != null)
            s += " offset " + size * page + " ";
        ResultSet resultSet = getStatement(s).executeQuery();
        return new ResponseEntity<>(mapSet(resultSet), HttpStatus.OK);
    }


    @GetMapping("/employees/by_department/{dep}")
    public ResponseEntity<List<Employee>> getEmployeesByDepartmentId(@RequestParam(required = false) Integer page,
                                                                     @RequestParam(required = false) Integer size,
                                                                     @RequestParam(required = false) String sort,
                                                                     @PathVariable String dep) throws SQLException {
        String s;
        try {
            Long.parseLong(dep);
            s = "select * from EMPLOYEE where DEPARTMENT = " + dep + " ";
        } catch (NumberFormatException e) {
            s = "select * from EMPLOYEE inner join DEPARTMENT on employee.department=department.id where department.NAME = '" + dep + "' ";
        }
        if (sort != null && sort.equals("hired")) {
            sort = "HIREDATE";
        }
        if (sort != null)
            s += " order by " + sort + " ";
        if (size != null)
            s += " limit " + size + " ";
        if (page != null)
            s += " offset " + size * page + " ";
        ResultSet resultSet = getStatement(s).executeQuery();
        return new ResponseEntity<>(mapSet(resultSet), HttpStatus.OK);
    }

}
