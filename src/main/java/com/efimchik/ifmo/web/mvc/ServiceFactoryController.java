package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ServiceFactoryController {

    private ResultSet resSet(String s) {
        try {
            return DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "")
                    .createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(s);
        } catch (SQLException e) {
            return null;
        }
    }

    public List<Employee> allEmployees(String from, boolean bool, boolean isManager) {
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
        List<Department> departments = departmentsList("select * from department");
        Department department = null;
        if (resultSet.getObject("department") != null) {
            Long departmentId = Long.parseLong(resultSet.getString("department"));
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
                manager = Objects.requireNonNull(allEmployees("SELECT * FROM employee WHERE id=" + managerId, true, false)).get(0);
            } else {
                manager = Objects.requireNonNull(allEmployees("SELECT * FROM employee WHERE id=" + managerId, false, false)).get(0);
            }
        }
        return manager;
    }

//    private static Employee managerFound(BigInteger managerID, boolean bool, boolean isManager) {
//        Employee manager = null;
//        try {
//            if ( managerID != null && isManager ) {
//                boolean check = false;
//                ResultSet ResultSet = resSet("select * from employee");
//                assert ResultSet != null;
//                while (ResultSet.next()) {
//                    if (BigInteger.valueOf(ResultSet.getInt("id")).equals(managerID)) {
//                        manager = allEmployees("select * from employee", bool, bool).get(0);
//                        check = true;
//                    }
//                }
//                if (!check) {
//                    return null;
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return null;
//        }
//        return manager;
//    }

    private Department departmentMapRow(ResultSet resultSet) {
        try {
            Long ID = Long.parseLong(resultSet.getString("id"));
            return new Department(ID,
                    resultSet.getString("name"),
                    resultSet.getString("location"));
        } catch (SQLException e) {
            return null;
        }
    }

     public BigInteger departmentByFrom(String from) throws SQLException {
        ResultSet resultSet = resSet(from);
        try {
            assert resultSet != null;
            if (resultSet.next()) {
                return new BigInteger(resultSet.getString("id"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Department> departmentsList(String from) {
        ResultSet resultSet = resSet(from);
        List<Department> departments = new ArrayList<>();
        try {
            assert resultSet != null;
            while (resultSet.next()) {
                departments.add(departmentMapRow(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments;
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

}