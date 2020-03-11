package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class EmployeeController {
    @GetMapping(value = "/employees")
    public ResponseEntity<List<Employee>> getAllEmployees(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size, @RequestParam(required = false) String sort) throws SQLException {
        String query = "select * from employee";
        query += ((sort != null) ? " order by " + changeHireDateColumn(sort) : " ") + getWithPaging(page, size);
        return ResponseEntity.ok(getEmployees(false, true, query));
    }

    @GetMapping(value = "/employees/{employee}")
    public ResponseEntity<Employee> getOneEmployee(@PathVariable(name = "employee") String employee, @RequestParam(name = "full_chain", required = false, defaultValue = "false") String fullChain) throws SQLException {
        String query = "select * from employee where id = " + employee;
        if ("true".equals(fullChain)) {
            return ResponseEntity.ok(getEmployees(true, true, query).get(0));
        }
        return ResponseEntity.ok(getEmployees(false, true, query).get(0));
    }

    @GetMapping(value = "/employees/by_manager/{manager}")
    public ResponseEntity<List<Employee>> getEmployeesByManager(@PathVariable(name = "manager") Integer manager, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size, @RequestParam(required = false) String sort) throws SQLException {

        String query = "select * from employee where manager = ";
        query += manager + ((sort != null) ? " order by " + changeHireDateColumn(sort) : " ") + getWithPaging(page, size);
        return ResponseEntity.ok(getEmployees(false, true, query));
    }

    @GetMapping(value = "/employees/by_department/{dep}")
    public ResponseEntity<List<Employee>> getEmployeesByDep(@PathVariable(name = "dep") String dep, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size, @RequestParam(required = false) String sort) throws SQLException {
        Long depId;
        if(!isNumeric(dep)) {
            depId = getDepIDByName(dep);
        } else {
            depId = Long.parseLong(dep);
        }
        String query = "select * from employee where department = ";
        query += depId + ((sort != null) ? " order by " + changeHireDateColumn(sort) : " ") + getWithPaging(page, size);

        return ResponseEntity.ok(getEmployees(false, true, query));

    }

    private String changeHireDateColumn(String sort) {
        if ("hired".equals(sort))
            return "hiredate";
        return sort;
    }

    public Employee getEmployee(ResultSet rs, boolean managerFlag, boolean chainFlag) throws SQLException {
        Object rsManager = rs.getObject("manager");
        Object rsDepartment = rs.getObject("department");

        Long id = Long.parseLong(rs.getString("id"));
        FullName name = new FullName(
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("middleName")
        );
        Position position = Position.valueOf(rs.getString("position"));
        LocalDate hireDate = LocalDate.parse(rs.getString("hireDate"));
        BigDecimal salary = rs.getBigDecimal("salary");
        Employee manager = ((chainFlag || managerFlag) && rsManager != null) ? getEmployees(chainFlag, false, "select * from employee where id = " + new BigInteger(rs.getString("manager"))).get(0) : null;
        Department dep = (rsDepartment != null) ? getDepById(BigInteger.valueOf(rs.getInt("department"))) : null;

        return new Employee(id, name, position, hireDate, salary, manager, dep);
    }

    public List<Employee> getEmployees(boolean chainFlag, boolean managerFlag, String query) throws SQLException {
        List<Employee> Employees = new ArrayList<>();
        ResultSet rs = DBDriver.getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(query);
        while (rs.next()) {
            Employees.add(getEmployee(rs, managerFlag, chainFlag));
        }
        return Employees;
    }

    private Department getDep(ResultSet rs) throws SQLException {
        return new Department(Long.parseLong(rs.getString("ID")), rs.getString("name"), rs.getString("location"));
    }

    private Department getDepById(BigInteger id) throws SQLException {
        ResultSet rs = DBDriver.getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery("select * from department where id = " + id);
        rs.next();
        return getDep(rs);
    }

    public static Long getDepIDByName(String depName) throws SQLException {
        String sql = "select ID from department where name = '" + depName + "'";
        ResultSet rs = DBDriver.getConnection().createStatement().executeQuery(sql);
        if (rs.next()) {
            return Long.parseLong(rs.getString("id"));
        } else {
            return null;
        }
    }

    private static boolean isNumeric(String strNum) {
        return strNum.matches("-?\\d+(\\.\\d+)?");
    }

    private static String getWithPaging(Integer page, Integer size) { return ((size != null) ? " limit " + size : " ") + ((page != null) ? " offset " + size * page : " ");
    }
}
