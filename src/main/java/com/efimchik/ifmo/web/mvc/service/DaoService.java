package com.efimchik.ifmo.web.mvc.service;

import com.efimchik.ifmo.web.mvc.ConnectionDb;
import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class DaoService {

    public List<Employee> getEmployees(int chain, String request) {
        try {
            final ConnectionDb connectionDb = ConnectionDb.instance();
            final Connection conn = connectionDb.getConnection();
            final Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            final ResultSet rs = statement.executeQuery(request);
            List<Employee> allEmployees = new LinkedList<>();
            while (rs.next()) {
                Employee emp = employeeMapRow(rs, chain);
                allEmployees.add(emp);
            }
            return allEmployees;
        } catch (SQLException e) {
            return  null;
        }
    }

    private Department getDepartment(int id) {
        try {
            final ConnectionDb connectionDb = ConnectionDb.instance();
            final Connection conn = connectionDb.getConnection();
            final Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            final ResultSet rs = statement.executeQuery("SELECT * FROM DEPARTMENT WHERE ID = " + id);
            if (rs.isBeforeFirst()) rs.next();
            return departmentMapRow(rs);
        } catch (SQLException e) {
            return  null;
        }
    }

    public int getDepartmentByName(String name) {
        try {
            final ConnectionDb connectionDb = ConnectionDb.instance();
            final Connection conn = connectionDb.getConnection();
            //final Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            final PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM DEPARTMENT WHERE NAME = ?");
            preparedStatement.setString(1, name);
            final ResultSet rs = preparedStatement.executeQuery();
            if (rs.isBeforeFirst()) rs.next();
            return rs.getInt("ID");
        } catch (SQLException e) {
            return  0;
        }
    }

    private Employee getManager (int id, int chain) {
        return getEmployees((chain == 3) ? 3 : 2, "SELECT * FROM EMPLOYEE WHERE ID = " + id).get(0);
    }


    private Employee employeeMapRow(ResultSet rs, int chain) {
        try {
            int thisId = rs.getInt("id");
            String fn = rs.getString("firstname");
            String ln = rs.getString("lastname");
            String mn = rs.getString("middlename");
            FullName fullName = new FullName(fn, ln, mn);
            Position pos = Position.valueOf(rs.getString("position"));
            LocalDate date = LocalDate.parse(String.valueOf(rs.getDate("hiredate")));
            BigDecimal salary = rs.getBigDecimal("salary");
            Employee manager = null;
            if (chain != 2 && rs.getString("manager") != null) {
                    int managerId = Integer.valueOf(rs.getString("manager"));
                    manager = getManager(managerId, chain);
            }
            Department department = null;
            if (rs.getString("department") != null) {
                department = getDepartment(rs.getInt("department"));
            }

            return new Employee(Long.valueOf(thisId),
                    fullName,
                    pos,
                    date,
                    salary,
                    manager,
                    department);
        }

        catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }

    }


    private Department departmentMapRow(ResultSet rs) {
        try {
            Long id = Long.valueOf(rs.getInt("id"));
            String name = rs.getString("name");
            String location = rs.getString("location");
            return new Department(id, name,location);

        } catch (SQLException e) {
            return null;
        }

    }
}
