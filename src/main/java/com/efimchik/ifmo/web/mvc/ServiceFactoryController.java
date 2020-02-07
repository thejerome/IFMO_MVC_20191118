package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ServiceFactoryController {
    private Connection connection;

    @Autowired
    public void setDataSource(final DataSource dataSource) throws SQLException {
        connection = dataSource.getConnection();
    }

    private ResultSet resSet(String query) {
        try {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            return statement.executeQuery(query);
        } catch (SQLException ignored) {
            return null;
        }
    }

    List<Employee> allEmployee(String from, boolean bool, boolean isManager) {
        ResultSet resultSet = resSet(from);
        try {
            if (resultSet != null) {
                List<Employee> employees = new ArrayList<>();
                while (resultSet.next()) {
                    employees.add(employeeMapRow(resultSet,  bool, isManager));
                }
                return employees;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Employee employeeMapRow(ResultSet resultSet, boolean bool, boolean isManager) throws SQLException {
        Long id = Long. parseLong(resultSet.getString("id"));
        FullName fullName = new FullName(
                resultSet.getString("firstName"),
                resultSet.getString("lastName"),
                resultSet.getString("middleName")
        );
        List<Department> departments = departmentsList();
        Department department = null;
        if (resultSet.getObject("department") != null) {
            Long departmentId = Long.parseLong(resultSet.getString("department"));
            assert departments != null;
            department = departmentFound(departments, departmentId);
        }
        return new Employee(id,
                fullName,
                Position.valueOf(resultSet.getString("position")),
                LocalDate.parse(resultSet.getString("hireDate")),
                resultSet.getBigDecimal("salary"),
                managerFound(resultSet, bool, isManager),
                department);
    }

    private Employee managerFound(ResultSet resultSet, boolean bool, boolean isManager) throws SQLException {
        Employee manager = null;
        if (resultSet.getObject("manager") != null && (bool || isManager)) {
            BigInteger managerId = new BigInteger(resultSet.getString("manager"));
            if (bool) {
                manager = Objects.requireNonNull(allEmployee("SELECT * FROM employee WHERE id=" + managerId, true, false)).get(0);
            } else {
                manager = Objects.requireNonNull(allEmployee("SELECT * FROM employee WHERE id=" + managerId, false, false)).get(0);
            }
        }
        return manager;
    }

    private Department departmentMapRow(ResultSet resultSet) {
        try {
            Long ID = Long.parseLong(resultSet.getString("id"));
            return new Department(ID,
                    resultSet.getString("name"),
                    resultSet.getString("location"));
        } catch (SQLException ignored) {
            return null;
        }
    }

    private Department departmentFound(List<Department> departments, Long departmentID) {
        Department departmentF = null;
        for (Department department : departments) {
            if (department.getId().equals(departmentID)) {
                departmentF = department;
            }
        }
        return departmentF;
    }

    BigInteger departmentByFrom(String from) {
        ResultSet resultSet = resSet(from);
        try {
            assert resultSet != null;
            if (resultSet.next()) {
                return new BigInteger(resultSet.getString("id"));
            } else {
                return null;
            }
        } catch (SQLException ignored) {
            return null;
        }
    }

    private List<Department> departmentsList() {
        ResultSet resultSet = resSet("select * from department");
        List<Department> departments = new ArrayList<>();
        try {
            assert resultSet != null;
            while (resultSet.next()) {
                departments.add(departmentMapRow(resultSet));
            }
        } catch (SQLException ignored) {
            return null;
        }
        return departments;
    }
}