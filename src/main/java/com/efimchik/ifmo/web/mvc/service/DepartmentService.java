package com.efimchik.ifmo.web.mvc.service;

import com.efimchik.ifmo.web.mvc.ConnectionSource;
import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DepartmentService {
    @Autowired
    private static UserService userService;

    static Department departmentMapRow(ResultSet resultSet) {
        try {
            Long id = Long.valueOf(resultSet.getString("ID"));
            String name = resultSet.getString("NAME");
            String location = resultSet.getString("LOCATION");
            return new Department(id, name, location);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Department> getDepartmentResultList(String request) {
        try {
            ResultSet resultSet = ConnectionSource.instance().createConnection().createStatement().executeQuery(request);
            List<Department> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(departmentMapRow(resultSet));
            }
            return result;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Employee> getEmployeeByDepResultList(Long departmentId, String sort, Integer size, Integer page) {
        try {
            String sortLocal = userService.isHired(sort);
            ResultSet resultSet = ConnectionSource.instance().createConnection().createStatement().executeQuery(
                    "SELECT * FROM employee WHERE department = " + departmentId +
                            ((sortLocal != null) ? " ORDER BY " + sortLocal : " ") +
                            ((size != null) ? " LIMIT " + size : " ") +
                            ((page != null && size != null) ? " OFFSET " + size * page : " ")
            );
            List<Employee> result = new ArrayList<>();

            while (resultSet.next()) {
                result.add(userService.employeeMapRow(resultSet, false, false));
            }

            return result;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Long getDepartmentIdByName(String departmentName) {
        Department result = Objects.requireNonNull(getDepartmentResultList("SELECT * FROM department WHERE name = '" + departmentName + "'")).get(0);
        if (result == null)
            return 0L;
        else
            return result.getId();
    }
}
