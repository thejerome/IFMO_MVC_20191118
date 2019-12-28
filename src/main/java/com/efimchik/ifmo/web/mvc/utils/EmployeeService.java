package com.efimchik.ifmo.web.mvc.utils;

import com.efimchik.ifmo.web.mvc.db.DBControllerService;
import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private DBControllerService dbController;

    public Employee getEmployee(int id, boolean fullChain) {
        if (fullChain)
            return getEmployeeWithManagerChain(id);
        else
            return getEmployee(id);
    }

    public List<Employee> getEmployeesList(Integer page, Integer size, String sort) {
        String changedSort = checkSort(sort);
        return getAllEmployees(page, size, changedSort);
    }

    public List<Employee> getEmployeesByManagerIdList(Integer page, Integer size, String sort, int managerId) {
        List<Employee> employees = new ArrayList<>();
        String checkedSort = checkSort(sort);
        try (ResultSet rs = dbController.executeSQL(
                "select * from employee where manager=" + managerId +
                        ((checkedSort != null) ? " order by " + checkedSort : " ") +
                        ((size != null) ? " limit " + size : " ") +
                        ((page != null && size != null) ? " offset " + size * page : " "
                        ))) {
            while (rs.next()) {
                employees.add(getEmployee(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    public List<Employee> getEmployeesByDepartmentList(Integer page, Integer size, String sort, String department) {
        if (isNumeric(department))
            return getEmployeesByDepartmentId(page, size, sort, Integer.parseInt(department));
        else
            return getEmployeesByDepartmentName(page, size, sort, department);
    }

    private List<Employee> getEmployeesByDepartmentName(Integer page, Integer size, String sort, String departmentName) {
        List<Employee> employees = new ArrayList<>();
        String checkedSort = checkSort(sort);
        try (ResultSet rs = dbController.executeSQL(
                "select employee.*, department.* from employee join department on employee.department=department.id where name='" + departmentName + "'" +
                        ((checkedSort != null) ? " order by " + checkedSort : " ") +
                        ((size != null) ? " limit " + size : " ") +
                        ((page != null && size != null) ? " offset " + size * page : " "
                        ))) {
            while(rs.next())
                employees.add(getEmployee(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    private List<Employee> getEmployeesByDepartmentId(Integer page, Integer size, String sort, int departmentId) {
        List<Employee> employees = new ArrayList<>();
        String checkedSort = checkSort(sort);
        try (ResultSet rs = dbController.executeSQL(
                "select * from employee where department=" + departmentId +
                        ((checkedSort != null) ? " order by " + checkedSort : " ") +
                        ((size != null) ? " limit " + size : " ") +
                        ((page != null && size != null) ? " offset " + size * page : " "
                        ))) {
            while (rs.next())
                employees.add(getEmployee(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private List<Employee> getAllEmployees(Integer page, Integer size, String sort) {
        List<Employee> dbEmployees = new ArrayList<>();
        try (ResultSet rs = dbController.executeSQL(
                "select * from employee" +
                        ((sort != null) ? " order by " + sort : " ") +
                        ((size != null) ? " limit " + size : " ") +
                        ((page != null && size != null) ? " offset " + size * page : " ")
        )) {
            while (rs.next()) {
                dbEmployees.add(getEmployee(rs));
            }
        } catch (SQLException ignored) {
        }
        return dbEmployees;
    }

    private Employee getEmployee(int id) {
        try (ResultSet rs = dbController.executeSQL("select * from employee where id=" + id)) {
            if (!rs.next()) return null;
            return getEmployee(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Employee getEmployee(ResultSet resultSet) {
        try {
            Employee manager = null;
            if (resultSet.getString("manager") != null) {
                manager = getManager(new BigInteger(resultSet.getString("manager")));
            }
            return mapEmployee(resultSet, manager);

        } catch (SQLException e) {
            return null;
        }
    }

    private Employee getEmployeeWithManagerChain(int employeeId) {
        try (ResultSet rs = dbController.executeSQL("select * from employee where id=" + employeeId)) {
            if (!rs.next()) return null;
            return getEmployeeWithManagerChain(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Employee getEmployeeWithManagerChain(ResultSet resultSet) {
        try {
            Employee manager = null;
            if (resultSet.getString("manager") != null) {
                manager = getManagerWithChain(Integer.parseInt(resultSet.getString("manager")));
            }
            return mapEmployee(resultSet, manager);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Employee getManagerWithChain(int managerId) {
        System.out.println(managerId);
        try (ResultSet rs = dbController.executeSQL("select * from employee where id=" + managerId)) {
            if (!rs.next()) return null;
            if (rs.getString("manager") == null)
                return mapEmployee(rs, null);
            return mapEmployee(rs, getManagerWithChain(Integer.parseInt(rs.getString("manager"))));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Employee mapEmployee(ResultSet resultSet, Employee manager) throws SQLException {
        return new Employee(
                Long.parseLong(resultSet.getString("id")),
                new FullName(
                        resultSet.getString("firstname"),
                        resultSet.getString("lastname"),
                        resultSet.getString("middlename")
                ),
                Position.valueOf(resultSet.getString("position")),
                LocalDate.parse(resultSet.getString("hiredate")),
                new BigDecimal(resultSet.getString("salary")),
                manager,
                resultSet.getString("department") == null ? null : getDepartment(resultSet.getString("department")));
    }

    private Employee getManager(BigInteger managerID) {
        try (ResultSet rs = dbController.executeSQL("select * from employee where id=" + managerID)) {
            if (rs.next())
                return mapEmployee(rs, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Department getDepartment(String id) {
        if (id == null) return null;
        try {
            ResultSet rs = dbController.executeSQL("select * from department where id=" + id);
            if (rs.next())
                return new Department(
                        Long.parseLong(rs.getString("id")),
                        rs.getString("name"),
                        rs.getString("location")
                );
            else
                return null;
        } catch (SQLException e) {
            return null;
        }
    }

    private String checkSort(String sort) {
        if ("hired".equals(sort)) {
            return "hiredate";
        }
        return sort;
    }
}
