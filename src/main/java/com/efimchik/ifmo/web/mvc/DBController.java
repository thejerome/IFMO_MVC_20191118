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
import java.util.ArrayList;
import java.util.List;

@Controller
public class DBController {

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Department> departmentList = getDepartmentList();

    private List<Employee> employeeList(ResultSet resultSet) {
        return getEmployeeList(resultSet, true);
    }

    private List<Employee> employeeListWithChain(ResultSet resultSet) {
        return getEmployeeList(resultSet, false);
    }


    private ResultSet getResultSet(String SQLString) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
            Statement statement = connection.createStatement();
            return statement.executeQuery(SQLString);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new UnsupportedOperationException();
        }
    }

    private Department getDepartment(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("ID");
        String name = resultSet.getString("NAME");
        String location = resultSet.getString("LOCATION");
        return new Department(id, name, location);
    }

    private Department getDepartmentById(Long Id) {
        Department resultDepartment = null;
        for (Department department : departmentList)
            if (department.getId().equals(Id))
                resultDepartment = department;
        return resultDepartment;
    }

    private List<Department> getDepartmentList() {
        try {
            List<Department> departmentList = new ArrayList<>();
            String SQLString = "SELECT * FROM DEPARTMENT";
            ResultSet resultSet = getResultSet(SQLString);

            assert resultSet != null;
            while (resultSet.next())
                departmentList.add(getDepartment(resultSet));
            return departmentList;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new UnsupportedOperationException();
        }
    }

    private Employee getEmployee(ResultSet resultSet, boolean chain, boolean firstManager) throws SQLException {
        boolean firstManagerClone = firstManager;
        Long id = resultSet.getLong("ID");
        BigInteger managerId = BigInteger.valueOf(resultSet.getInt("MANAGER"));
        Long departmentId = resultSet.getLong("DEPARTMENT");
        Department department = getDepartmentById(departmentId);
        Employee manager = null;

        if (managerId != null && firstManagerClone) {
            String SQLString = "SELECT * FROM EMPLOYEE";
            if (!chain)
                firstManagerClone = false;
            ResultSet newResultSet = getResultSet(SQLString);
            assert newResultSet != null;
            while (newResultSet.next()) {
                if (BigInteger.valueOf(newResultSet.getInt("ID")).equals(managerId))
                    manager = getEmployee(newResultSet, chain, firstManagerClone);
            }
        }

        return new Employee(
                id,
                new FullName (resultSet.getString("FIRSTNAME"), resultSet.getString("LASTNAME"), resultSet.getString("MIDDLENAME")),
                Position.valueOf(resultSet.getString("POSITION")),
                LocalDate.parse(resultSet.getString("HIREDATE")),
                new BigDecimal(resultSet.getString("SALARY")),
                manager,
                department);
    }

    private List<Employee> getEmployeeList(ResultSet resultSet, boolean chain) {
        try {
            List<Employee> employeeList = new ArrayList<>();
            if (resultSet != null) {
                while (resultSet.next())
                    employeeList.add(getEmployee(resultSet, chain, true));
                return employeeList;
            } else
                return null;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new UnsupportedOperationException();
        }
    }

    private ResponseEntity<List<Employee>> getListResponseEntity(@RequestParam(required = false) Integer size, @RequestParam(required = false) Integer page, String fixedSort, String SQLString) {
        String fixedSortSQLString = ((fixedSort != null) ? " ORDER BY " + fixedSort : "");
        String sizeSQLString = ((size != null) ? " LIMIT " + size : "");
        String pageSQLString = ((page != null && size != null) ? " OFFSET " + size * page : "");
        String finalSQLString = SQLString + fixedSortSQLString + sizeSQLString + pageSQLString;
        ResultSet resultSet = getResultSet(finalSQLString);
        ResponseEntity<List<Employee>> listResponseEntity;
        listResponseEntity = new ResponseEntity<>(employeeListWithChain(resultSet), HttpStatus.OK);
        return listResponseEntity;
    }

    private String getIfClauseResponseEntity(String sort) {
        String fixedSort = sort;
        if (sort != null && sort.equals("hired"))
            fixedSort = "HIREDATE";
        return fixedSort;
    }

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getEmployees(@RequestParam(required = false) Integer size, @RequestParam(required = false) Integer page, @RequestParam(required = false) String sort) {
        String SQLString = "SELECT * FROM EMPLOYEE ";
        return getListResponseEntity(size, page, getIfClauseResponseEntity(sort), SQLString);
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<Employee> getEmployeeById(@RequestParam(name = "full_chain", required = false, defaultValue = "false") String fullChain, @PathVariable Integer id) {
        String SQLString = "SELECT * FROM EMPLOYEE WHERE ID = " + id;
        ResultSet resultSet = getResultSet(SQLString);
        boolean trueString = "true".equals(fullChain);
        if (!fullChain.isEmpty() && trueString) {
            ResponseEntity<Employee> responseEntity;
            responseEntity = new ResponseEntity<>(employeeList(resultSet).get(0), HttpStatus.OK);
            return responseEntity;
        } else {
            ResponseEntity<Employee> responseEntity;
            responseEntity = new ResponseEntity<>(employeeListWithChain(resultSet).get(0), HttpStatus.OK);
            return responseEntity;
        }
    }

    @GetMapping("/employees/by_manager/{managerId}")
    public ResponseEntity<List<Employee>> getEmployeesByManager(@RequestParam(required = false) Integer size, @RequestParam(required = false) Integer page, @RequestParam(required = false) String sort, @PathVariable Integer managerId) {
        String SQLString = "SELECT * FROM EMPLOYEE WHERE MANAGER = " + managerId;
        return getListResponseEntity(size, page, getIfClauseResponseEntity(sort), SQLString);
    }

    @GetMapping("/employees/by_department/{department}")
    public ResponseEntity<List<Employee>> getEmployeesByDepartment(@RequestParam(required = false) Integer size, @RequestParam(required = false) Integer page, @RequestParam(required = false) String sort, @PathVariable String department) {
        try {
            Long departmentId = Long.valueOf(department);
            String SQLString = "SELECT * FROM EMPLOYEE WHERE DEPARTMENT = " + departmentId;
            return getListResponseEntity(size, page, getIfClauseResponseEntity(sort), SQLString);

        } catch (Exception e) {
            String SQLString = "SELECT * FROM EMPLOYEE LEFT JOIN DEPARTMENT ON employee.department = department.id WHERE department.name = '" + department + "'";
            return getListResponseEntity(size, page, getIfClauseResponseEntity(sort), SQLString);
        }
    }
}
