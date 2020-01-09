package com.efimchik.ifmo.web.mvc.Service;

import com.efimchik.ifmo.web.mvc.ConnectionSource;
import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private static DepService depService;

     static Employee employeeMapRow(ResultSet resultSet, boolean isManager, boolean isFullChain) {
        try {
            Long id = Long.valueOf(resultSet.getString("ID"));
            FullName fullName = new FullName(
                    resultSet.getString("FIRSTNAME"),
                    resultSet.getString("LASTNAME"),
                    resultSet.getString("MIDDLENAME"));
            Position position = Position.valueOf(resultSet.getString("POSITION"));
            LocalDate hireDate = LocalDate.parse(resultSet.getString("HIREDATE"));
            BigDecimal salary = resultSet.getBigDecimal("SALARY");

            Employee manager = null;
            if (!isManager) {
                ResultSet managerResultSet = ConnectionSource.instance().createConnection().createStatement()
                        .executeQuery("SELECT * FROM employee WHERE id = " + resultSet.getInt("MANAGER"));

                if (managerResultSet.next())
                    manager = employeeMapRow(managerResultSet, !isFullChain, isFullChain);
            }

            Department department = null;

            ResultSet departmentResultSet = ConnectionSource.instance().createConnection().createStatement()
                    .executeQuery("SELECT * FROM department WHERE id = " + resultSet.getInt("DEPARTMENT"));

            if (departmentResultSet.next())
                department = depService.departmentMapRow(departmentResultSet);

            return new Employee(id, fullName, position, hireDate, salary, manager, department);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
         return null;
     }

    public static List<Employee> getEmployeeResultList(String sort, Integer size, Integer page) {
        try {
            sort = isHired(sort);
            ResultSet resultSet = ConnectionSource.instance().createConnection().createStatement().executeQuery(
                    "SELECT * FROM employee" +
                            ((sort != null) ? " ORDER BY " + sort : " ") +
                            ((size != null) ? " LIMIT " + size : " ") +
                            ((page != null && size != null) ? " OFFSET " + size * page : " ")
            );
            List<Employee> result = new ArrayList<>();

            while (resultSet.next()) {
                result.add(employeeMapRow(resultSet, false, false));
            }

            return result;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Employee> getEmployeeByManagerResultList(Integer managerId, String sort, Integer size, Integer page) {
        try {
            sort = isHired(sort);
            ResultSet resultSet = ConnectionSource.instance().createConnection().createStatement().executeQuery(
                    "SELECT * FROM employee WHERE manager = " + managerId +
                            ((sort != null) ? " ORDER BY " + sort : " ") +
                            ((size != null) ? " LIMIT " + size : " ") +
                            ((page != null && size != null) ? " OFFSET " + size * page : " ")
            );
            List<Employee> result = new ArrayList<>();

            while (resultSet.next()) {
                result.add(employeeMapRow(resultSet, false, false));
            }

            return result;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Employee getEmployeeById(int employeeId, boolean isFullChain) {
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
            e.printStackTrace();
        }
        return null;
    }

    static String isHired(String sort){
        if (sort != null && sort.equals("hired")){
            sort = "HIREDATE";
        }
        return sort;
    }

}