package com.efimchik.ifmo.web.mvc.service;

import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class Mapper {

    private Connection connection;
    private int maxLayer = 1000;

    public Mapper(int maxLayer){
        this();
        this.maxLayer = maxLayer;
    }

    public Mapper() {
        try {
            connection = ConnectionSource.instance().createConnection();
        } catch (Exception exc) {
            System.out.println("Exception in getResultSet");
        }
    }

    public List<Employee> employeesListMapper(String request, int layer) {

        List<Employee> employeesList = new ArrayList<>();
        try {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet = statement.executeQuery(request);
            while (resultSet.next()) {
                Employee employee = getEmployee(resultSet, layer);
                employeesList.add(employee);
            }
            return employeesList;
        } catch (SQLException ex) {
            return null;
        }
    }

    public Employee getEmployee(ResultSet resultSet, int layers) throws SQLException {
        try {

            // результат запроса
            // парсинг
            Long id = new Long(String.valueOf(resultSet.getInt("id")));
            FullName fullName = new FullName(resultSet.getString("firstname"),
                    resultSet.getString("lastname"),
                    resultSet.getString("middlename"));
            Position position = Position.valueOf(resultSet.getString("position"));
            LocalDate hired = LocalDate.parse(resultSet.getString("hiredate"));
            BigDecimal salary = resultSet.getBigDecimal("salary");
            Employee manager = null;
            if ((resultSet.getInt("manager") != 0) && (layers < maxLayer)){
                manager = employeesListMapper("SELECT * FROM EMPLOYEE WHERE ID = " + resultSet.getInt("manager"), layers+1).get(0);
            }

            int depId = resultSet.getInt("department");
            Department department = getDepartmentById(depId);

            return new Employee(id, fullName, position, hired, salary, manager, department);
        } catch (SQLException exc) {
            return null;
        }
    }

    public Department getDepartmentById(int id){
        try {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String request = "SELECT * FROM DEPARTMENT";
            ResultSet rs = statement.executeQuery(request);
            if (rs.isBeforeFirst()) rs.next();
            while ((!rs.isAfterLast()) && (rs.getInt("id") != id)){
                rs.next();
            }
            Long depId = new Long(String.valueOf(rs.getInt("id")));
            String depName = rs.getString("name");
            String depLocation = rs.getString("location");
            return new Department(depId, depName, depLocation);
        } catch (SQLException exc) {
            return null; }
    }

    public Department getDepartmentByName(String name){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM DEPARTMENT WHERE name = ?");
            statement.setString(1,name);
            ResultSet rs = statement.executeQuery();
            if (rs.isBeforeFirst())rs.next();
            return getDepartmentById(rs.getInt("id"));

        } catch (SQLException exc) {
            return null; }
    }
}



