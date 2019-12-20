package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.*;


import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.min;

public class Service {
    private static Employee employeeMapRow(ResultSet resultSet, boolean isManager, boolean isFullChain) {
        try {
            Long id = Long.valueOf(resultSet.getString("ID"));
            FullName fullName = new FullName(resultSet.getString("FIRSTNAME"), resultSet.getString("LASTNAME"), resultSet.getString("MIDDLENAME"));
            Position position = Position.valueOf(resultSet.getString("POSITION"));
            LocalDate hireDate = LocalDate.parse(resultSet.getString("HIREDATE"));
            BigDecimal salary = resultSet.getBigDecimal("SALARY");


            Employee manager = null;
            if (!isManager) {
                int managerId = resultSet.getInt("MANAGER");

                ResultSet managerResultSet = ConnectionSource.instance().createConnection().createStatement()
                        .executeQuery("SELECT * FROM employee WHERE id = " + managerId);

                // Постарайтесь сами понять, почему тут isManager = !isFullChain
                if (managerResultSet.next())
                    manager = employeeMapRow(managerResultSet, !isFullChain, isFullChain);
            }

            // Тут попроще, просто мапим департамент по айди
            Department department = null;

            int departmentId = resultSet.getInt("DEPARTMENT");

            ResultSet departmentResultSet = ConnectionSource.instance().createConnection().createStatement()
                    .executeQuery("SELECT * FROM department WHERE id = " + departmentId);

            if (departmentResultSet.next())
                department = departmentMapRow(departmentResultSet);

            return new Employee(id, fullName, position, hireDate, salary, manager, department);
        }
        catch (SQLException e) {
            return null;
        }
    }

    private static Department departmentMapRow(ResultSet resultSet) {
        try {
            Long id = Long.valueOf(resultSet.getString("ID"));
            String name = resultSet.getString("NAME");
            String location = resultSet.getString("LOCATION");

            return new Department(id, name, location);
        }
        catch (SQLException e) {
            return null;
        }
    }

    static List<Employee> getEmployeeResultList (String request) {
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

    static List<Department> getDepartmentResultList (String request) {
        try {
            ResultSet resultSet = ConnectionSource.instance().createConnection().createStatement().executeQuery(request);
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

    // for getEmployeeById

    static Employee getEmployeeById(int employeeId, boolean isFullChain) {
        try {
            ResultSet resultSet = ConnectionSource.instance().createConnection().createStatement()
                    .executeQuery("SELECT * FROM employee WHERE id = " + employeeId);

            if (resultSet.next()) {
                return employeeMapRow(resultSet, false, isFullChain);
            }
            else
                return null;
        }
        catch (SQLException e) {
            return null;
        }
    }

    // for getEmployeesByDepartmentFilter

    static Long getDepartmentIdByName(String departmentName) {
        Department res = getDepartmentResultList("SELECT * FROM department WHERE name = '" + departmentName + "'").get(0);
        return (res == null ? 0 : res.getId());

    }
}
