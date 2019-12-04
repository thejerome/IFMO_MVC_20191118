package com.efimchik.ifmo.web.mvc.handlers;

import com.efimchik.ifmo.web.mvc.ConnectionSource;
import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class EmployeeService {

    private EmployeeService(){

    }

    static List<Employee> getEmployeeListByResultSet(ResultSet res) throws SQLException {
        res.beforeFirst();
        List<Employee> ans = new ArrayList<>();
        while (res.next()) {
            ans.add(employeeRowMapper(res, true));
        }
        return ans;
    }

    private static Employee employeeRowMapper(ResultSet resultSet, boolean isFirstLevel) {
        Employee cur = null;
        try {
            String managerId = resultSet.getString("MANAGER");
            if (!isFirstLevel) {
                managerId = null;
            }
            String departmentId = resultSet.getString("DEPARTMENT");
            cur = new Employee(
                    (long) resultSet.getInt("ID"),
                    new FullName(
                            resultSet.getString("FIRSTNAME"),
                            resultSet.getString("LASTNAME"),
                            resultSet.getString("MIDDLENAME")
                    ),
                    Position.valueOf(resultSet.getString("POSITION")),
                    LocalDate.parse(resultSet.getString("HIREDATE")),
                    new BigDecimal(resultSet.getInt("SALARY")),
                    managerId == null ? null : getEmployeeByIdFromService(new BigInteger(managerId)),
                    departmentId == null ? null : getDepartmentById(new BigInteger(departmentId))
            );
        } catch (SQLException ignored) {
        }
        return cur;
    }

    private static Employee employeeRowMapper(ResultSet resultSet) {
        Employee cur = null;
        try {
            String managerId = resultSet.getString("MANAGER");
            String departmentId = resultSet.getString("DEPARTMENT");
            cur = new Employee(
                    (long) resultSet.getInt("ID"),
                    new FullName(
                            resultSet.getString("FIRSTNAME"),
                            resultSet.getString("LASTNAME"),
                            resultSet.getString("MIDDLENAME")
                    ),
                    Position.valueOf(resultSet.getString("POSITION")),
                    LocalDate.parse(resultSet.getString("HIREDATE")),
                    new BigDecimal(resultSet.getInt("SALARY")),
                    managerId == null ? null : getEmployeeByIdWithChain(new BigInteger(managerId)),
                    departmentId == null ? null : getDepartmentById(new BigInteger(departmentId))
            );
        } catch (SQLException ignored) {
        }
        return cur;
    }

    static Employee getEmployeeByIdWithChain(BigInteger Id) {
        try {
            ResultSet res = getResultSetOfExecute("select * from employee where id = " + Id);
            res.next();
            return employeeRowMapper(res);
        } catch (SQLException e) {
            return null;
        }
    }

    private static Employee getEmployeeByIdFromService(BigInteger Id) {
        try {
            ResultSet res = getResultSetOfExecute("select * from employee where id = " + Id);
            res.next();
            return employeeRowMapper(res, false);
        } catch (SQLException e) {
            return null;
        }
    }

    private static Department getDepartmentById(BigInteger Id) {
        try {
            ResultSet res = getResultSetOfExecute("select * from department where id = " + Id);
            return getDepartmentListByResultSet(res).get(0);
        } catch (SQLException e) {
            return null;
        }
    }

    private static Department departmentRowMapper(ResultSet res) {
        Department cur = null;
        try {
            cur = new Department(
                    new Long(res.getString("ID")),
                    res.getString("NAME"),
                    res.getString("LOCATION")
            );
        } catch (SQLException ignored) {
        }
        return cur;
    }

    static List<Department> getDepartmentListByResultSet(ResultSet res) throws SQLException {
        res.beforeFirst();
        List<Department> ans = new ArrayList<>();
        while (res.next()) {
            ans.add(departmentRowMapper(res));
        }
        return ans;
    }

    private static ResultSet getResultSetOfExecute(String sql) throws SQLException {
        ConnectionSource connectionSource = ConnectionSource.instance();
        Connection connection = connectionSource.createConnection();
        return connection.createStatement().executeQuery(sql);
    }

}