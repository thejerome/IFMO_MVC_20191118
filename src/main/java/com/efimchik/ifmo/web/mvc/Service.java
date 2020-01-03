package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Service {

    

    static List<Employee> getEmployeeResultListByRequest(
            String request,
            int managementDepth
    ) throws SQLException {
        ResultSet resultSet = ConnectionSource.instance().createConnection().createStatement().executeQuery(request);

        List<Employee> resultList = new ArrayList<>();

        while (resultSet.next()) {
            resultList.add(getEmployeeFromResultSet(resultSet, managementDepth));
        }
        return resultList;
    }

    private static Employee getEmployeeFromResultSet(ResultSet rs, int managementDepth) {
        try {
            Long id = (long) rs.getInt("id");

            String firstName = rs.getString("firstname");
            String lastName = rs.getString("lastname");
            String middleName = rs.getString("middlename");
            FullName fullName = new FullName(firstName, lastName, middleName);

            Position position = Position.valueOf(rs.getString("position"));

            LocalDate hired = rs.getDate("hiredate").toLocalDate();

            BigDecimal salary = rs.getBigDecimal("salary");

            //GETTING MANAGER
            Employee manager = null;
            int managerId = rs.getInt("manager");
            if(managerId != 0 && managementDepth < 1) {
                ResultSet managerResultSet = ConnectionSource.instance().createConnection().createStatement()
                        .executeQuery("SELECT * FROM EMPLOYEE WHERE id = " + Integer.toString(managerId));
                //Recursion
                if (managerResultSet.next())
                    manager = getEmployeeFromResultSet(managerResultSet, managementDepth + 1);
            }

            //GETTING DEPARTMENT
            Department department = null;
            int departmentId = rs.getInt("department");
            if (departmentId != 0) {
                ResultSet departmentResultSet = ConnectionSource.instance().createConnection().createStatement()
                        .executeQuery("SELECT * FROM department WHERE id = " + departmentId);

                if (departmentResultSet.next())
                    department = getDepartmentFromResultSet(departmentResultSet);
            }
            return new Employee(id, fullName, position, hired, salary, manager, department);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Department getDepartmentFromResultSet(ResultSet rs) {
        try {
            Long id = Long.valueOf(rs.getString("ID"));
            String name = rs.getString("NAME");
            String location = rs.getString("LOCATION");

            return new Department(id, name, location);
        }
        catch (SQLException e) {
            return null;
        }
    }

    static Long getDepartmentIdByName(String departmentName) throws SQLException {
        Department depId = null;

        String request = "SELECT * FROM department WHERE name = '" + departmentName + "'";

        ResultSet resultSet = ConnectionSource.instance().createConnection().createStatement().executeQuery(request);
        if (resultSet.next()) {
            depId = getDepartmentFromResultSet(resultSet);
        }
        return (depId == null ? 0 : depId.getId());
    }

//    public static List<Department> getDepartmentResultListByRequest(String request) throws SQLException{
//        ResultSet resultSet = ConnectionSource.instance().createConnection().createStatement().executeQuery(request);
//
//        List<Department> resultList = new ArrayList<>();
//
//        while (resultSet.next()) {
//            resultList.add(getDepartmentFromResultSet(resultSet));
//        }
//        return resultList;
//    }
}
