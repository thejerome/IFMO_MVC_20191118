package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.*;


import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.min;
import static java.lang.Integer.toUnsignedLong;

public class Service {

    public static List<Employee> getEmployeeResultList (String request) {
        try {
            ResultSet resultSet = ConnectionSource.instance().createConnection().createStatement().executeQuery(request);
            List<Employee> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(employeeMapRow(resultSet, false, false));
            }
            return result;
        }
        catch (SQLException e) {
            return null;
        }
    }

    public static Long getDepartmentIdByName(String departmentName) {
        String query="SELECT * FROM department WHERE name = '" + departmentName + "'";
        Department res = getDepartmentResultList(query.trim()).get(0);
        if (res != null)
        {
            return res.getId();
        }
        else
        {
            Long result=toUnsignedLong(0);
            return result;
        }
    }

    private static Employee employeeMapRow(ResultSet resultSet, boolean isManager, boolean isFullChain) {
        try {
            Department department = null;
            int departmentId = resultSet.getInt("DEPARTMENT");
            String query = "SELECT * FROM department WHERE id = " + departmentId;
            ResultSet departmentResultSet = ConnectionSource.instance().createConnection().createStatement()
                    .executeQuery(query);

            Employee manager = null;
            if (isManager==false) {
                int managerId = resultSet.getInt("MANAGER");
                query="SELECT * FROM employee WHERE id = " + managerId;
                ResultSet managerResultSet = ConnectionSource.instance().createConnection().createStatement()
                        .executeQuery(query);
                if (managerResultSet.next())
                    manager = employeeMapRow(managerResultSet, !isFullChain, isFullChain);
            }
            if (departmentResultSet.next())
                department = departmentMapRow(departmentResultSet);
            return new Employee(Long.valueOf(resultSet.getString("ID")), new FullName(resultSet.getString("FIRSTNAME"), resultSet.getString("LASTNAME"),
                    resultSet.getString("MIDDLENAME")), Position.valueOf(resultSet.getString("POSITION")), LocalDate.parse(resultSet.getString("HIREDATE")),
                    resultSet.getBigDecimal("SALARY"), manager, department);
        }
        catch (SQLException e) {
            return null;
        }
    }

    public static Employee getEmployeeById(int employeeId, boolean isFullChain) {
        try {
            String query="SELECT * FROM employee WHERE id = " + employeeId;
            ResultSet resultSet = ConnectionSource.instance().createConnection().createStatement().executeQuery(query.trim());
            if (resultSet.next())
            {
                return employeeMapRow(resultSet, false, isFullChain);
            }
            else
                return null;
        }
        catch (SQLException e) {
            return null;
        }
    }

    public static List<Department> getDepartmentResultList (String request) {
        try {
            ResultSet resultSet = ConnectionSource.instance().createConnection().createStatement().executeQuery(request.trim());
            List<Department> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(departmentMapRow(resultSet));
            }

            return result;
        }
        catch (SQLException e) {
            return null;
        }
    }

    private static Department departmentMapRow(ResultSet resultSet) {
        try {
            String query=resultSet.getString("ID");
            return new Department(Long.valueOf(query),
                    resultSet.getString("NAME"),
            resultSet.getString("LOCATION"));
        }
        catch (SQLException e) {
            return null;
        }
    }

}
