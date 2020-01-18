package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.*;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.time.LocalDate;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmpService {
    private static Employee empMap(ResultSet resultSet, boolean isMan, boolean isFCh) {
        try {
            Long id = Long.valueOf(resultSet.getString("ID"));
            FullName fullName = new FullName(resultSet.getString("FIRSTNAME"), resultSet.getString("LASTNAME"), resultSet.getString("MIDDLENAME"));
            Position position = Position.valueOf(resultSet.getString("POSITION"));
            LocalDate hireDate = LocalDate.parse(resultSet.getString("HIREDATE"));
            BigDecimal salary = resultSet.getBigDecimal("SALARY");

            Employee man = null;
            if (!isMan) {
                int manId = resultSet.getInt("MANAGER");
                ResultSet managerResultSet = ConnectionSource.instance().createConnection().createStatement()
                        .executeQuery("SELECT * FROM employee WHERE id = " + manId);
                if (managerResultSet.next())
                    man = empMap(managerResultSet, !isFCh, isFCh);
            }
            Department dep = null;
            int depId = resultSet.getInt("DEPARTMENT");
            ResultSet departmentResultSet = ConnectionSource.instance().createConnection().createStatement()
                    .executeQuery("SELECT * FROM department WHERE id = " + depId);
            if (departmentResultSet.next())
                dep = depMap(departmentResultSet);
            Employee empans = new Employee(id, fullName, position, hireDate, salary, man, dep);
            return empans;
        }
        catch (SQLException e) {
            return null;
        }
    }

    private static Department depMap(ResultSet resultSet) {
        try {
            Long id = Long.valueOf(resultSet.getString("ID"));
            String name = resultSet.getString("NAME");
            String location = resultSet.getString("LOCATION");
            Department depans = new Department(id, name, location);
            return depans;
        }
        catch (SQLException e) {
            return null;
        }
    }

    static List<Employee> getEmployeeResultList (String request) {
        try {
            ResultSet resSet = ConnectionSource.instance().createConnection().createStatement().executeQuery(request);
            List<Employee> result = new ArrayList<>();
            while (resSet.next()) {
                result.add(empMap(resSet, false, false));
            }
            return result;
        }
        catch (SQLException e) {
            return null;
        }
    }

    static Employee getEmployeeById(int employeeId, boolean isFullChain) {
        try {
            ResultSet resSet = ConnectionSource.instance().createConnection().createStatement()
                    .executeQuery("SELECT * FROM employee WHERE id = " + employeeId);

            if (resSet.next()) {
                return empMap(resSet, false, isFullChain);
            }
            else
                return null;
        }
        catch (SQLException e) {
            return null;
        }
    }

    static List<Department> getDepartmentResultList (String request) {
        try {
            ResultSet resSet = ConnectionSource.instance().createConnection().createStatement().executeQuery(request);
            List<Department> result = new ArrayList<>();

            while (resSet.next()) {
                result.add(depMap(resSet));
            }

            return result;
        }
        catch (SQLException e) {
            return null;
        }
    }

    static Long getDepartmentIdByName(String departmentName) {
        Department res = getDepartmentResultList("SELECT * FROM department WHERE name = '" + departmentName + "'").get(0);
        return (res == null ? 0 : res.getId());

    }
    private static ResultSet getResultEXE(String sql) throws SQLException {
        ConnectionSource connectionSource = ConnectionSource.instance();
        Connection connection = connectionSource.createConnection();
        return connection.createStatement().executeQuery(sql);
    }
}
