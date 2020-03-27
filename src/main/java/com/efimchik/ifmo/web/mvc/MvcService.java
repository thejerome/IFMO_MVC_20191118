package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@RestController
public class MvcService {

    @GetMapping(value = "/employees/{employee}")
    public Employee getEmployee(@PathVariable(name = "employee") String employee, @RequestParam(name = "full_chain", required = false) String fullChain) throws SQLException {
        String sql = "select * from employee where id=" + employee;
        if ("true".equals(fullChain)) return mapSetEmployeesWithChain(sql).get(0);
        return mapSetEmployeesWithManager(sql, true).get(0);
    }

    @GetMapping(value = "/employees")
    public List<Employee> getEmployees(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size, @RequestParam(required = false) String sort) throws SQLException {
        String sql = "select * from employee";
        String sortColumn = (!"hired".equals(sort)) ? (" order by "+sort) : " order by hireDate";
        String paging = (size!=null && page!=null) ? (" limit " + size + " offset " + (page*size)) : "";
        return mapSetEmployeesWithManager(sql+sortColumn+paging, true);
    }

    @GetMapping(value = "/employees/by_department/{department}")
    public List<Employee> getEmployeeByDep(@PathVariable(name = "department") String department, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size, @RequestParam(required = false) String sort) throws SQLException {
        String sql = "select * from employee where department=";
        String id = Character.isDigit(department.charAt(0)) ? department : getDepId(department);
        String sortColumn = (!"hired".equals(sort)) ? (" order by "+sort) : " order by hireDate";
        String paging = (size!=null && page!=null) ? (" limit " + size + " offset " + (page*size)) : "";

        return mapSetEmployeesWithManager(sql+id+sortColumn+paging, true);
    }

    @GetMapping(value = "/employees/by_manager/{manager}")
    public List<Employee> getEmployeeByManager(@PathVariable(name = "manager") String manager, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size, @RequestParam(required = false) String sort) throws SQLException {
        String sql = "select * from employee where manager=" + manager;
        String sortColumn = (!"hired".equals(sort)) ? (" order by "+sort) : " order by hireDate";
        String paging = (size!=null && page!=null) ? (" limit " + size + " offset " + (page*size)) : "";

        return mapSetEmployeesWithManager(sql+sortColumn+paging, true);
    }

    private List<Employee> mapSetEmployeesWithManager(String sql, boolean manager_is_not_null) throws SQLException {
        List<Employee> employees = new LinkedList<>();
        ResultSet resultSet = ConnectionSource.instance().createConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(sql);
        while (resultSet.next()) employees.add(mapRowEmployeeWithManager(resultSet, manager_is_not_null));
        return employees;
    }

    private List<Employee> mapSetEmployeesWithChain(String sql) throws SQLException {
        List<Employee> employees = new LinkedList<>();
        ResultSet resultSet = ConnectionSource.instance().createConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(sql);
        while (resultSet.next()) employees.add(mapRowEmployeeWithChain(resultSet));
        return employees;
    }

    private Employee mapRowEmployeeWithManager(ResultSet resultSet, boolean include_manager) throws SQLException {
        return getEmployee(resultSet, resultSet.getObject("manager") != null && include_manager, mapSetEmployeesWithManager("select * from employee where id=" + resultSet.getString("manager"), false));
    }

    private Employee mapRowEmployeeWithChain(ResultSet resultSet) throws SQLException {
        return getEmployee(resultSet, resultSet.getObject("manager") != null, mapSetEmployeesWithChain("select * from employee where id=" + resultSet.getString("manager")));
    }

    private Employee getEmployee(ResultSet resultSet, boolean manager2, List<Employee> manager3) throws SQLException {
        Long id = Long.parseLong(resultSet.getString("id"));
        FullName fullName = new FullName(
                resultSet.getString("firstName"),
                resultSet.getString("lastName"),
                resultSet.getString("middleName")
        );
        Position position = Position.valueOf(resultSet.getString("position"));
        LocalDate date = LocalDate.parse(resultSet.getString("hireDate"));
        BigDecimal salary = resultSet.getBigDecimal("salary");
        Employee manager = (manager2) ? manager3.get(0) : null;
        Department department = (resultSet.getObject("department") != null) ? mapRowDep(Long.parseLong(resultSet.getString("department"))) : null;
        return new Employee(id, fullName, position, date, salary, manager, department);
    }

    private Department mapRowDep(Long id) {
        try {
            ResultSet resultSet = ConnectionSource.instance().createConnection().createStatement().executeQuery("SELECT * FROM DEPARTMENT where id=" + id);
            return (resultSet.next()) ? new Department(id, resultSet.getString("name"), resultSet.getString("location")) : null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getDepId(String name) throws SQLException {
        String sql = "select * from department where name=?";
        PreparedStatement statement = ConnectionSource.instance().createConnection().prepareStatement(sql);
        statement.setString(1, name);
        ResultSet resultSet = statement.executeQuery();
        return resultSet.next() ? resultSet.getString("id") : null;
    }
}
