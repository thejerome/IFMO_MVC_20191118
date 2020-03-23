package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@RestController
public class Controller {

    private String getSort(String sort) {
        if ("hired".equals(sort)) {
            return "hiredate";
        }
        return sort;
    }

    private Employee getEmployee(long id, boolean fullChain) throws SQLException {
//        System.out.println(id);
        if (!fullChain) {
            ResultSet rs = getRs("SELECT * FROM EMPLOYEE WHERE ID=" + id);
            rs.next();
//            System.out.println(rs.getString("FIRSTNAME"));
//            System.out.println(employee(rs));
            return employee(rs);
        } else {
            ResultSet rs = getRs("SELECT * FROM EMPLOYEE WHERE ID=" + id);
            rs.next();
            return empWMChain(rs);
        }
    }

    private Employee empWMChain(ResultSet rs) {
        try {
            Employee manager = null;
            if (rs.getString("MANAGER") != null) {
                manager = manChain(rs);
            }
            return setEmployee(rs, manager);
        } catch (SQLException e) {
            return null;
        }
    }

    private Employee manChain(ResultSet resultSet) {
        try {
            int manID = resultSet.getInt("MANAGER");
            if (manID == 0) return null;
            ResultSet rs = getRs("SELECT * FROM EMPLOYEE");
            int row = rs.getRow();
            Employee manager = null;
            rs.first();
            rs.previous();
            while (rs.next()) {
                if (rs.getInt("ID") == manID) {
                    manager = empWMChain(rs);
                    break;
                }
            }
            rs.absolute(row);
            return manager;
        } catch (SQLException e) {
            return null;
        }
    }

    private List<Employee> employees(Integer page, Integer size, String sort) {
        List<Employee> employees = new LinkedList<>();
        try {
            ResultSet rs = getRs("SELECT * FROM EMPLOYEE " +
                    ((sort != null) ? "ORDER BY " + getSort(sort) : " ") +
                    ((size != null) ? " LIMIT " + size : " ") +
                    ((page != null && size != null) ? " OFFSET " + size * page : ""));
            while (rs.next()) {
                employees.add(employee(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return employees;
    }

    private Employee employee(ResultSet rs) {
        try {
            Employee manager = null;
            if (rs.getString("MANAGER") != null) {
                manager = manager(new BigInteger(rs.getString("MANAGER")));
            }
            System.out.println("121: " + manager);
            return setEmployee(rs, manager);

        } catch (SQLException e) {
            return null;
        }
    }

    private Employee manager(BigInteger id) {
        try {
            Employee manager = null;
            ResultSet rs = getRs("SELECT * FROM EMPLOYEE");

            if (rs.next()) {
                rs.previous();
            }
            else {
                return null;
            }
            int p = rs.getRow();
            rs.first();
            rs.previous();
            while (rs.next()) {
                if (new BigInteger(rs.getString("ID")).equals(id)) {
                    manager = setEmployee(rs, null);
                    break;
                }
            }
            rs.absolute(p);
            return manager;
        } catch (SQLException execption) {
            return null;
        }
    }

    private Employee setEmployee(ResultSet rs, Employee employee) throws SQLException {
        Long ID = Long.parseLong(rs.getString("ID"));
        FullName name = new FullName(
                rs.getString("FIRSTNAME"),
                rs.getString("LASTNAME"),
                rs.getString("MIDDLENAME")
        );
        Position pos = Position.valueOf(rs.getString("POSITION"));
        LocalDate date = LocalDate.parse(rs.getString("HIREDATE"));
        BigDecimal sal = new BigDecimal(rs.getString("SALARY"));
        Department dep = null;
        if (rs.getString("DEPARTMENT") == null) {
            dep = null;
        } else {
            dep = dep(rs.getString("DEPARTMENT"));
        }
        return new Employee(
                ID,
                name,
                pos,
                date,
                sal,
                employee,
                dep);
    }

    private Department dep(String id) {
//        if (id == null) return null;
        try {
            ResultSet rs = getRs("SELECT * FROM DEPARTMENT WHERE ID=" + id);
            if (rs.next())
                return new Department(
                        Long.parseLong(rs.getString("ID")),
                        rs.getString("NAME"),
                        rs.getString("LOCATION")
                );
            else
                return null;
        } catch (SQLException execption) {
            return null;
        }
    }

    private ResultSet getRs(String query) {
        try {
            Connection connection = DB.instance().createConnection();
            Statement stmnt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            return stmnt.executeQuery(query);
        } catch (SQLException e) {
            return null;
        }
    }

    private List<Employee> employeesByManager(Integer page, Integer size, String sort, Long managerID) {
        List<Employee> employees = new LinkedList<>();
        try {
            ResultSet rs = getRs("SELECT * FROM EMPLOYEE WHERE MANAGER= " + managerID +
                    ((sort != null) ? " ORDER BY " + getSort(sort) : " ") +
                    ((size != null) ? " LIMIT " + size : " ") +
                    ((page != null && size != null) ? " OFFSET " + size * page : ""));
            while (rs.next()) {
                employees.add(employee(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return employees;
    }

    private long depID(String depName) {
        switch (depName){
            case "ACCOUNTING":
                return 10;
            case "RESEARCH":
                return 20;
            case "SALES":
                return 30;
            case "OPERATIONS":
                return 40;
            default:
                return -1;
        }
    }

    private boolean ok(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private List<Employee> employeesByDep(Integer page, Integer size, String sort, String depName) {
        if (!ok(depName)) {
            List<Employee> employees = new LinkedList<>();
            try {
                ResultSet rs = getRs("SELECT * FROM EMPLOYEE WHERE DEPARTMENT=" + depID(depName) +
                        ((sort != null) ? " ORDER BY " + getSort(sort) : " ") +
                        ((size != null) ? " LIMIT " + size : " ") +
                        ((page != null && size != null) ? " OFFSET " + size * page : ""));
                while (rs.next()) {
                    employees.add(employee(rs));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return employees;
        }
        else {
            List<Employee> employees = new LinkedList<>();
            try {
                ResultSet rs = getRs("SELECT * FROM EMPLOYEE WHERE DEPARTMENT=" + depName +
                        ((sort != null) ? " ORDER BY " + getSort(sort) : " ") +
                        ((size != null) ? " LIMIT " + size : " ") +
                        ((page != null && size != null) ? " OFFSET " + size * page : ""));
                while (rs.next()) {
                    employees.add(employee(rs));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return employees;
        }
    }

    @GetMapping(value = "/employees")
    public ResponseEntity<List<Employee>> all(@RequestParam(required = false) Integer page,
                                              @RequestParam(required = false) Integer size,
                                              @RequestParam(required = false) String sort) {

        return new ResponseEntity<List<Employee>>(employees(page, size, sort), HttpStatus.OK);
    }

    @GetMapping(value = "/employees/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable(name = "id") String id,
                                                    @RequestParam(name = "full_chain", required = false, defaultValue = "false") String fullChain) throws SQLException {
//        System.out.println(id);
        return new ResponseEntity<>(getEmployee(Long.parseLong(id), Boolean.parseBoolean(fullChain)), HttpStatus.OK);
//        return null;
    }

    @GetMapping(value = "employees/by_manager/{managerId}")
    public ResponseEntity<List<Employee>> getEmployeesByManagerId(@RequestParam(required = false) Integer page,
                                                                  @RequestParam(required = false) Integer size,
                                                                  @RequestParam(required = false) String sort,
                                                                  @PathVariable(name = "managerId") String managerId) {

        return new ResponseEntity<>(employeesByManager(page, size, sort, Long.parseLong(managerId)), HttpStatus.OK);
    }

    @GetMapping("employees/by_department/{department}")
    public ResponseEntity<List<Employee>> getEmployeesByDepartment(@RequestParam(required = false) Integer page,
                                                                   @RequestParam(required = false) Integer size,
                                                                   @RequestParam(required = false) String sort,
                                                                   @PathVariable(name = "department") String department) {
        return new ResponseEntity<>(employeesByDep(page, size, sort, department), HttpStatus.OK);
    }
}
