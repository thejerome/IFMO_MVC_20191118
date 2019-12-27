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
import java.sql.Statement;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

public class EmployeeService {

    private static Connection createConnection() throws SQLException {
        DBConnection connectionSource = DBConnection.getInstance();
        Connection con = connectionSource.getConnection();
        System.out.println("connection established");
        return con;
    }

    private static void close(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void close(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> getAllEmployeesSorted(boolean chain, boolean managerNeeded, String sql) throws SQLException {
        Connection con = null;
        Statement stmt = null;
        try {
            List<Employee> listEmployees = new LinkedList<>();
            con = createConnection();
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                Employee employee = getEmployee(resultSet, chain, managerNeeded);
                listEmployees.add(employee);
            }
            return listEmployees;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
                close(stmt);
                close(con);
        }
    }

    private static Employee getEmployee(ResultSet rs, boolean chain, boolean managerNeeded) throws SQLException {
        BigInteger id = new BigInteger(rs.getString("id"));
        FullName fullName = new FullName(
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("middleName")
        );
        Position position = Position.valueOf(rs.getString("position"));
        LocalDate date = LocalDate.parse(rs.getString("hireDate"));
        BigDecimal salary = rs.getBigDecimal("salary");
        Employee manager = null;
        if (rs.getObject("manager") != null) {
            if (chain || managerNeeded) {
                BigInteger managerId = new BigInteger(rs.getString("manager"));
                if (chain) {
                    manager = getAllEmployeesSorted(true, false, "SELECT * FROM employee WHERE id=" + managerId).get(0);
                } else {
                    manager = getAllEmployeesSorted(false, false, "SELECT * FROM employee WHERE id=" + managerId).get(0);
                }
            }
        }
        Department department = null;
        if (rs.getObject("department") != null) {
            BigInteger departmentId = BigInteger.valueOf(rs.getInt("department"));
            department = getDepartmentById(departmentId);
        }
        return new Employee(id, fullName, position, date, salary, manager, department);
    }

    public static Department getDepartment(ResultSet rs) throws SQLException {
        BigInteger id = new BigInteger(rs.getString("id"));
        String name = rs.getString("name");
        String location = rs.getString("location");
        return new Department(id, name, location);
    }

    public static BigInteger getDepartmentIdByName(String sql) throws SQLException {
        Connection con = null;
        Statement stmt = null;
        try {
            con = createConnection();
            stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);
            resultSet.next();
            return new BigInteger(resultSet.getString("id"));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(stmt);
            close(con);
        }
    }

    public static Department getDepartmentById(BigInteger id) {
        Connection con = null;
        Statement stmt = null;
        try {
            Department dep = null;
            con = createConnection();
            stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM DEPARTMENT where id=" + id);
            while (resultSet.next()) {
                dep = getDepartment(resultSet);
            }
            return dep;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(stmt);
            close(con);
        }
    }

}
