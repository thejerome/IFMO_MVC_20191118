package com.efimchik.ifmo.web.mvc;

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
import java.util.LinkedList;
import java.util.List;

class EmployeeService {

    public ResultSet resultSet(String s) throws SQLException {
        ConnectionSource connectionSource = ConnectionSource.instance();
        Connection connection = connectionSource.createConnection();
        return connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(s);
    }

    public List<Employee> getEmployeeListWithoutChain(ResultSet rs) throws SQLException {
        rs.beforeFirst();
        List<Employee> ans = new LinkedList<>();
        while (rs.next()) {
            ans.add(employeeRowMapperWithoutChain(rs, true));
        }
        return ans;
    }

    public List<Employee> getEmployeeListWithChain(ResultSet rs) throws SQLException {
        rs.beforeFirst();
        List<Employee> ans = new LinkedList<>();
        while (rs.next()) {
            ans.add(employeeRowMapperWithChain(rs));
        }
        return ans;
    }


    private Department getDepartmentById(BigInteger Id) {
        try {
            ResultSet res = resultSet("select * from department where id = " + Id);
            return getDepartmentListByResultSet(res).get(0);
        } catch (SQLException e) {
            return null;
        }
    }

    private Department departmentRowMapper(ResultSet res) {
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

    private List<Department> getDepartmentListByResultSet(ResultSet res) throws SQLException {
        res.beforeFirst();
        List<Department> ans = new ArrayList<>();
        while (res.next()) {
            ans.add(departmentRowMapper(res));
        }
        return ans;
    }

    private Employee employeeRowMapperWithoutChain(ResultSet rs, boolean level) {
        Employee cur = null;
        try {
            String managerId = rs.getString("MANAGER");
            if (!level) {
                managerId = null;
            }
            String departmentId = rs.getString("DEPARTMENT");
            cur = new Employee(
                    rs.getLong("ID"),
                    new FullName(
                            rs.getString("FIRSTNAME"),
                            rs.getString("LASTNAME"),
                            rs.getString("MIDDLENAME")
                    ),
                    Position.valueOf(rs.getString("POSITION")),
                    LocalDate.parse(rs.getString("HIREDATE")),
                    new BigDecimal(rs.getInt("SALARY")),
                    managerId == null ? null : getEmployeeByIdWithoutChain(new BigInteger(managerId)),
                    departmentId == null ? null : getDepartmentById(new BigInteger(departmentId))
            );
        } catch (SQLException ignored) {
        }
        return cur;
    }

    private Employee employeeRowMapperWithChain(ResultSet rs) {
        Employee cur = null;
        try {
            String managerId = rs.getString("MANAGER");
            String departmentId = rs.getString("DEPARTMENT");
            cur = new Employee(
                    rs.getLong("ID"),
                    new FullName(
                            rs.getString("FIRSTNAME"),
                            rs.getString("LASTNAME"),
                            rs.getString("MIDDLENAME")
                    ),
                    Position.valueOf(rs.getString("POSITION")),
                    LocalDate.parse(rs.getString("HIREDATE")),
                    new BigDecimal(rs.getInt("SALARY")),
                    managerId == null ? null : getEmployeeByIdWithFullChain(new BigInteger(managerId)),
                    departmentId == null ? null : getDepartmentById(new BigInteger(departmentId))
            );
        } catch (SQLException ignored) {
        }
        return cur;
    }

    private Employee getEmployeeByIdWithFullChain(BigInteger Id) {
        try {
            ResultSet res = resultSet("select * from employee where id = " + Id);
            if (res.next())
                return employeeRowMapperWithChain(res);
            else
                return null;
        } catch (SQLException e) {
            return null;
        }
    }

    private Employee getEmployeeByIdWithoutChain(BigInteger Id) {
        try {
            ResultSet res = resultSet("select * from employee where id = " + Id);
            if (res.next())
                return employeeRowMapperWithoutChain(res, false);
            else
                return null;
        } catch (SQLException e) {
            return null;
        }
    }

}