package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;

public class DBHelper {
    private Connection connection;

    public DBHelper() throws SQLException {
        connection = DBConnection.getInstance().getConnection();
    }

    private String getSort(String sort) {
        if ("hired".equals(sort)) {
            return "HIREDATE";
        }
        return sort;
    }


    public LinkedList<Employee> getAllEmployeeFromDb(boolean full_chain, Integer page, Integer size, String sort, boolean managerNeeded) throws SQLException {
        PreparedStatement preparedStatement;
        String sort1 = getSort(sort);


        String sql = "SELECT * FROM employee" +
                ((sort != null) ? " ORDER BY " + sort1 : " ") +
                ((size != null) ? " LIMIT " + size : " ") +
                ((page != null && size != null) ? " OFFSET " + size * page : " ");


        preparedStatement = connection.prepareStatement(sql);
        return get(preparedStatement, full_chain, managerNeeded);
    }

    public LinkedList<Employee> getAllEmployeeByManager(boolean full_chain, Integer page, Integer size, String sort, int manager) throws SQLException {
        PreparedStatement preparedStatement;
        String sort1 = getSort(sort);

        preparedStatement = connection.prepareStatement("SELECT * FROM employee WHERE manager=?" +
                ((sort != null) ? " ORDER BY " + sort1 : " ") +
                ((size != null) ? " LIMIT " + size : " ") +
                ((page != null && size != null) ? " OFFSET " + size * page : " "));
        preparedStatement.setInt(1, manager);
        return get(preparedStatement, full_chain, true);
    }

    public LinkedList<Employee> getAllEmployeeByDepartment(boolean full_chain, Integer page, Integer size, String sort, int department) throws SQLException {
        PreparedStatement preparedStatement;
        String sort1 = getSort(sort);

        preparedStatement = connection.prepareStatement("SELECT * FROM employee WHERE department=?" +
                ((sort != null) ? " ORDER BY " + sort1 : " ") +
                ((size != null) ? " LIMIT " + size : " ") +
                ((page != null && size != null) ? " OFFSET " + size * page : " "));
        preparedStatement.setInt(1, department);
        return get(preparedStatement, full_chain, true);
    }


    public LinkedList<Employee> getAllEmployeeById(Integer managerId, boolean full_chain, boolean managerNeeded) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM employee WHERE id=?");
        preparedStatement.setInt(1, managerId);
        return get(preparedStatement, full_chain, managerNeeded);
    }


    private LinkedList<Employee> get(PreparedStatement preparedStatement, boolean full_chain, boolean managerNeeded) throws SQLException {
        ResultSet result = preparedStatement.executeQuery();
        LinkedList<Employee> employees = new LinkedList<>();
        while (result.next()) employees.add(getEmployeeFromResultSet(result, full_chain, managerNeeded));
        System.out.println(employees);
        return employees;
    }

    private Employee getEmployeeFromResultSet(ResultSet resultSet, boolean full_chain, boolean managerNeeded) throws SQLException {
        Long id = Long.valueOf(resultSet.getString("id"));
        FullName fullName = new FullName(resultSet.getString("firstName"),
                resultSet.getString("lastName"), resultSet.getString("middleName"));
        Position position = Position.valueOf(resultSet.getString("position"));
        LocalDate hireDate = LocalDate.parse(resultSet.getString("hireDate"));
        BigDecimal salary = resultSet.getBigDecimal("salary");

        Employee manager = null;
        if (resultSet.getObject("manager") != null && (full_chain || managerNeeded)) {
            Integer managerId = resultSet.getInt("manager");
            manager = getAllEmployeeById(managerId, full_chain, false).get(0);
        }
        Department department = null;
        if (resultSet.getObject("department") != null) {
            int departmentId = resultSet.getInt("department");
            department = getDepartmentById(departmentId);
        }

        return new Employee(id, fullName, position, hireDate, salary, manager, department);
    }


    public Department getDepartmentById(int id) {
        PreparedStatement preparedStatement;
        try {
            Department dep = null;
            preparedStatement = connection.prepareStatement("SELECT * FROM DEPARTMENT where id=?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) dep = getDepartmentFromResultSet(resultSet);
            return dep;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Department getDepartmentFromResultSet(ResultSet resultSet) throws SQLException {
        return new Department(resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("location"));
    }

    public Integer getDepartmentIdByName(String name) {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement("SELECT id FROM DEPARTMENT where name=?");
            preparedStatement.setString(1, name);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}