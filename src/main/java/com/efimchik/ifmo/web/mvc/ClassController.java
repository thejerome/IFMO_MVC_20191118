package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;


@Controller
public class ClassController {

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private ResultSet getResultSet(String sql) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
            Statement statement = connection.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            return null;
        }
    }


    private List<Department> departmentList = getDepartmentList();

    private List<Department> getDepartmentList() {
        try {
            List<Department> depList = new LinkedList<>();
            ResultSet resultSet = getResultSet("select * from department");
            while (resultSet.next()) {
                depList.add(getDepartment(resultSet));
            }
            return depList;
        } catch (SQLException e) {
            return null;
        }
    }

    private Department getDepartment(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String location = resultSet.getString("location");
        return new Department(
                id,
                name,
                location);
    }


    private Department getDepartmentById(Long Id) {
        Department resultDep = null;
        for (Department department : departmentList) {
            if (department.getId().equals(Id)) {
                resultDep = department;
            }
        }
        return resultDep;
    }

    private List<Employee> employeeList(ResultSet resultSet) {
        return getEmployeeList(resultSet, true);
    }
    private List<Employee> employeeListWithShortChain(ResultSet resultSet) {
        return getEmployeeList(resultSet, false);
    }

    private List<Employee> getEmployeeList(ResultSet resultSet, boolean chain) {
        try {
            List<Employee> empList = new LinkedList<>();
            if (resultSet != null) {
                while (resultSet.next()) {
                    empList.add(getEmployee(resultSet, chain, true));
                }
                return empList;
            }
            else return null;
        } catch (SQLException e) {
            return null;
        }
    }


    private Employee getEmployee(ResultSet resultSet, boolean chain, boolean firstManager) throws SQLException {
        Long id = resultSet.getLong("id");
        FullName fullname = new FullName(
                resultSet.getString("firstname"),
                resultSet.getString("lastname"),
                resultSet.getString("middlename")
        );
        Position position = Position.valueOf(resultSet.getString("position"));
        LocalDate hired = LocalDate.parse(resultSet.getString("hiredate"));
        BigDecimal salary = BigDecimal.valueOf(resultSet.getDouble("salary"));
        BigInteger managerId = BigInteger.valueOf(resultSet.getInt("manager"));
        Long departmentId = resultSet.getLong("department");
        Department department = getDepartmentById(departmentId);
        Employee manager = null;
        if (managerId != null && firstManager) {
            if (!chain) {
                firstManager = false;
            }
            ResultSet newResultSet = getResultSet("select * from employee");
            while (newResultSet.next()) {
                if (BigInteger.valueOf(newResultSet.getInt("id")).equals(managerId)) {
                    manager = getEmployee(newResultSet, chain, firstManager);
                }
            }
        }
        return new Employee(
                id,
                fullname,
                position,
                hired,
                salary,
                manager,
                department);
    }

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getEmployees(@RequestParam(required = false) Integer size,
                                                       @RequestParam(required = false) Integer page,
                                                       @RequestParam(required = false) String sort) {
        if (sort != null && sort.equals("hired")) {
            sort = "hiredate";
        }
        ResultSet resultSet = getResultSet("select * from employee " +
                ((sort != null) ? " order by " + sort : "") +
                ((size != null) ? " limit " + size : "") +
                ((page != null && size != null) ? " offset " + size * page : ""));
        return new ResponseEntity<>(employeeListWithShortChain(resultSet), HttpStatus.OK);
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<Employee> getEmployeeById(@RequestParam(name = "full_chain", required = false, defaultValue = "false") String fullChain,
                                                    @PathVariable Integer id) {
        ResultSet resultSet = getResultSet("select * from employee" +
                " where id = " + id);
        if (!fullChain.isEmpty() && fullChain.equals("true")) {
            return new ResponseEntity<>(employeeList(resultSet).get(0), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(employeeListWithShortChain(resultSet).get(0), HttpStatus.OK);
        }
    }

    @GetMapping("/employees/by_manager/{managerId}")
    public ResponseEntity<List<Employee>> getEmployeesByManager(@RequestParam(required = false) Integer size,
                                                                @RequestParam(required = false) Integer page,
                                                                @RequestParam(required = false) String sort,
                                                                @PathVariable Integer managerId) {
        if (sort != null && sort.equals("hired")) {
            sort = "hiredate";
        }
        ResultSet resultSet = getResultSet("select * from employee" +
                " where manager = " + managerId +
                ((sort != null) ? " order by " + sort : "") +
                ((size != null) ? " limit " + size : "") +
                ((page != null && size != null) ? " offset " + size * page : ""));
        return new ResponseEntity<>(employeeListWithShortChain(resultSet), HttpStatus.OK);
    }

    @GetMapping("/employees/by_department/{department}")
    public ResponseEntity<List<Employee>> getEmployeesByDepartment(@RequestParam(required = false) Integer size,
                                                                   @RequestParam(required = false) Integer page,
                                                                   @RequestParam(required = false) String sort,
                                                                   @PathVariable String department) {
        if (sort != null && sort.equals("hired")) {
            sort = "hiredate";
        }
        try {
            Long departmentId = Long.valueOf(department);
            ResultSet resultSet = getResultSet("select * from employee" +
                    " where department = " + departmentId +
                    ((sort != null) ? " order by " + sort : "") +
                    ((size != null) ? " limit " + size : "") +
                    ((page != null && size != null) ? " offset " + size * page : ""));
            return new ResponseEntity<>(employeeListWithShortChain(resultSet), HttpStatus.OK);
        } catch (Exception e) {
            ResultSet resultSet = getResultSet("select * from employee" +
                    " left join department on employee.department = department.id" +
                    " where department.name = '" + department + "'" +
                    ((sort != null) ? " order by " + sort : "") +
                    ((size != null) ? " limit " + size : "") +
                    ((page != null && size != null) ? " offset " + size * page : ""));
            return new ResponseEntity<>(employeeListWithShortChain(resultSet), HttpStatus.OK);
        }
    }


}