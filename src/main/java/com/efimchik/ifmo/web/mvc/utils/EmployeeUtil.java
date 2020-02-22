package com.efimchik.ifmo.web.mvc.utils;

import com.efimchik.ifmo.web.mvc.DB;
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
import java.util.Objects;

public class EmployeeUtil {
    public static Connection getConnectionForStmt() throws SQLException {
        return DB.getInstance().getConnection();
    }

    public static void closeConAndStmt(Statement stmt, Connection con) throws SQLException {
        stmt.close();
        con.close();
    }

    public static List<Employee> getSortedEmployees(boolean chain, boolean isManagerNeeded, String query) throws SQLException {
        Connection con = getConnectionForStmt();
        Statement stmt = null;
        try {
            List<Employee> listOfEmployees = new LinkedList<>();
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Employee employee = getEmployee(rs, chain, isManagerNeeded);
                listOfEmployees.add(employee);
            }
            return listOfEmployees;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            assert stmt != null;
            closeConAndStmt(stmt, con);
        }
    }

    public static Employee getEmployee(ResultSet rs, boolean chain, boolean isManagerNeeded) throws SQLException {
        Long id = new Long(rs.getString("id"));
        FullName fullName = new FullName(
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("middleName")
        );
        Position pos = Position.valueOf(rs.getString("position"));
        LocalDate date = LocalDate.parse(rs.getString("hireDate"));
        BigDecimal salary = rs.getBigDecimal("salary");
        Employee man = null;
        Department dep = null;
        if (rs.getObject("manager") != null && (chain || isManagerNeeded)) {
            BigInteger managerId = new BigInteger(rs.getString("manager"));
            String query = "SELECT * FROM employee WHERE id=" + managerId;

            man = Objects.requireNonNull(getSortedEmployees(chain, false, query)).get(0);
        }
        if (rs.getObject("department") != null) {
            Long departmentId = (long) rs.getInt("department");
            dep = getDepartmentById(departmentId);
        }
        return new Employee(id, fullName, pos, date, salary, man, dep);
    }

    public static ResultSet getResultSet(String query) throws SQLException {
        Connection con = getConnectionForStmt();
        return con.createStatement().executeQuery(query);
    }

    public static Department getDepartmentById(Long id) throws SQLException {
        try {
            Department dep = null;
            ResultSet rs = getResultSet("SELECT * FROM department where id=" + id);
            while (rs.next()) {
                dep = getDepartment(rs);
            }
            return dep;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Department getDepartment(ResultSet rs) throws SQLException {
        return new Department(new Long(rs.getString("id")), rs.getString("name"), rs.getString("location"));
    }

    public static Long getDepIdByName(String sql) throws SQLException {
        Connection con = getConnectionForStmt();
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return new Long(rs.getString("id"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            assert stmt != null;
            closeConAndStmt(stmt, con);
        }
    }

    public static String makeColName(String sort) {
        if ("hired".equals(sort)) {
            return "HIREDATE";
        }
        return sort;
    }

    public static boolean isNum(String str) {
        if (str == null) {
            return false;
        }
        try {
            Integer.parseInt(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
