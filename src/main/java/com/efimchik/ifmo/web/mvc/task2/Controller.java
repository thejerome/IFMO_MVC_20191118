package com.efimchik.ifmo.web.mvc.task2;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class Controller {

    private ResultSet getRs(String query) {
        try {
            return ConnectionPool.instance().createConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(query);
        } catch (SQLException exception) {
            System.out.println("Exception");
            exception.printStackTrace();
            return null;
        }
    }

    private Department mapDep(BigInteger id) {
        ResultSet rs = getRs("SELECT * FROM DEPARTMENT WHERE ID=" + id);
        try {
            if (rs.next()) {
                rs.previous();
            }
        } catch (SQLException e){
            return null;
        }
        Department dep;
        try {
            rs.next();
            dep = new Department( Long.parseLong(rs.getString("ID")), rs.getString("NAME"), rs.getString("LOCATION"));
        } catch (SQLException e) {
            return null;
        }
        return dep;
    }

    private Employee mapManager(BigInteger id) {
        ResultSet rs = getRs("SELECT * FROM EMPLOYEE WHERE ID=" + id);
        assert rs != null;
        try {
            rs.next();
            Long Id = Long.parseLong(rs.getString("ID"));
            FullName name = new FullName(rs.getString("FIRSTNAME"), rs.getString("LASTNAME"),
                    rs.getString("MIDDLENAME"));
            Position pos = Position.valueOf(rs.getString("POSITION"));
            LocalDate date = LocalDate.parse(rs.getString("HIREDATE"));
            BigDecimal wage = new BigDecimal(rs.getString("SALARY"));
            Department dep = rs.getString("DEPARTMENT") == null ? null : mapDep(new BigInteger(rs.getString("DEPARTMENT")));
            Employee manager = null;
            return new Employee(
                    Id,
                    name,
                    pos,
                    date,
                    wage,
                    manager,
                    dep
            );
        } catch (SQLException e) {
            return null;
        }
    }

    private Employee mapEmployee(ResultSet rs) throws SQLException {
        Long id = Long.parseLong(rs.getString("ID"));
        FullName name = new FullName(rs.getString("FIRSTNAME"), rs.getString("LASTNAME"),
                rs.getString("MIDDLENAME"));
        Position pos = Position.valueOf(rs.getString("POSITION"));
        LocalDate date = LocalDate.parse(rs.getString("HIREDATE"));
        BigDecimal wage = new BigDecimal(rs.getString("SALARY"));
        Department dep = rs.getString("DEPARTMENT") == null ? null : mapDep(new BigInteger(rs.getString("DEPARTMENT")));
        Employee manager = rs.getString("MANAGER") == null ? null : mapManager(new BigInteger(rs.getString("MANAGER")));
        return new Employee(
                id,
                name,
                pos,
                date,
                wage,
                manager,
                dep
        );
    }

    private List<Employee> rsToList(ResultSet rs) {
        List<Employee> employees = new ArrayList<>();
        try {
            while (rs.next()) {
                employees.add(mapEmployee(rs));
            }
            return employees;
        } catch (SQLException e) {
            return null;
        }
    }

    private String changeHiredate(String sort) {
        return checkDate(sort);
    }

    private String checkDate(String sort) {
        if ("hired".equals(sort)) {
            return "hiredate";
        }
        return sort;
    }

    @GetMapping(value = "/employees")
    public ResponseEntity<List<Employee>> all(@RequestParam(required = false) Integer page,
                                              @RequestParam(required = false) Integer size,
                                              @RequestParam(required = false) String sort) {
        ResultSet rs = getRs( "SELECT * FROM EMPLOYEE " +
                ((changeHiredate(sort) != null) ? " ORDER BY " + changeHiredate(sort) : " ") +
                ((size != null) ? " LIMIT " + size : " ") +
                ((page != null && size != null) ? " OFFSET " + size * page : " "));
//        try {
//            rs.next();
//        }catch (SQLException e) {
//            return null;
//        }
        List<Employee> employees = rsToList(rs);
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @GetMapping(value="/employees/by_manager/{id}")
    public ResponseEntity<List<Employee>> manager(@PathVariable(name = "id") Long id,
                                                  @RequestParam(required = false) Integer page,
                                                  @RequestParam(required = false) Integer size,
                                                  @RequestParam(required = false) String sort) {
        ResultSet rs = getRs( "SELECT * FROM EMPLOYEE WHERE MANAGER= " + id.toString() +
                ((changeHiredate(sort) != null) ? " ORDER BY " + changeHiredate(sort) : " ") +
                ((size != null) ? " LIMIT " + size : " ") +
                ((page != null && size != null) ? " OFFSET " + size * page : " "));
        assert rs != null;
//        try {
//            rs.next();
//        }catch (SQLException e) {
//            return null;
//        }
        List<Employee> employees = rsToList(rs);
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    private int id(String nameOrId) {
        if (isNumeric(nameOrId)) {
            return Integer.parseInt(nameOrId);
        } else {
            return getId(nameOrId);
        }
    }

    private int getId(String depName) {
        switch (depName) {
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

    private boolean isNumeric(String id) {
        try {
            Integer.parseInt(id);
            return true;
        }catch (NumberFormatException e) {
            return false;
        }
    }

    @GetMapping(value="/employees/by_department/{nameOrId}")
    public ResponseEntity<List<Employee>> department(@PathVariable(name = "nameOrId") String nameOrId,
                                                     @RequestParam(required = false) Integer page,
                                                     @RequestParam(required = false) Integer size,
                                                     @RequestParam(required = false) String sort) {
        ResultSet rs = getRs( "SELECT * FROM EMPLOYEE WHERE DEPARTMENT= " + id(nameOrId) +
                ((changeHiredate(sort) != null) ? " ORDER BY " + changeHiredate(sort) : " ") +
                ((size != null) ? " LIMIT " + size : " ") +
                ((page != null && size != null) ? " OFFSET " + size * page : " "));
        assert rs != null;
//        try {
//            rs.next();
//        }catch (SQLException e) {
//            return null;
//        }
        List<Employee> employees = rsToList(rs);
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @GetMapping(value="/employees/{id}")
    public ResponseEntity<Employee> id(@PathVariable(name = "id") Long id,
                                             @RequestParam(name = "full_chain", required = false, defaultValue = "false") String fullChain) {
        System.out.println(id);
        ResultSet rs = getRs( "SELECT * FROM EMPLOYEE WHERE ID= " + id.toString());
        assert rs != null;
//        try {
//            rs.next();
//        }catch (SQLException e) {
//            return null;
//        }
        List<Employee> employees;
        if (Boolean.parseBoolean(fullChain))
             employees = rsToListMC(rs);
        else {
            employees = rsToList(rs);
        }
        System.out.println(employees);
        return new ResponseEntity<>(employees.get(0), HttpStatus.OK);
    }

    private List<Employee> rsToListMC(ResultSet rs) {
        List<Employee> employees = new ArrayList<>();
        try {
            while (rs.next()) {
                employees.add(mapEmployeeWMC(rs));
            }
            return employees;
        } catch (SQLException e) {
            return null;
        }
    }

    private Employee mapEmployeeWMC(ResultSet rs) throws SQLException {
        Long id = Long.parseLong(rs.getString("ID"));
        FullName name = new FullName(rs.getString("FIRSTNAME"), rs.getString("LASTNAME"),
                rs.getString("MIDDLENAME"));
        Position pos = Position.valueOf(rs.getString("POSITION"));
        LocalDate date = LocalDate.parse(rs.getString("HIREDATE"));
        BigDecimal wage = new BigDecimal(rs.getString("SALARY"));
        Department dep = rs.getString("DEPARTMENT") == null ? null : mapDep(new BigInteger(rs.getString("DEPARTMENT")));
        Employee manager = rs.getString("MANAGER") == null ? null : mapManagerWMC(new BigInteger(rs.getString("MANAGER")));
        return new Employee(
                id,
                name,
                pos,
                date,
                wage,
                manager,
                dep
        );
    }

    private Employee mapManagerWMC(BigInteger id) {
        ResultSet rs = getRs("SELECT * FROM EMPLOYEE WHERE ID=" + id);
        try {
            if (rs.next()) {
                rs.previous();
            }
        } catch (SQLException e){
            return null;
        }
        assert rs != null;
        try {
            rs.next();
            Long Id = Long.parseLong(rs.getString("ID"));
            FullName name = new FullName(rs.getString("FIRSTNAME"), rs.getString("LASTNAME"),
                    rs.getString("MIDDLENAME"));
            Position pos = Position.valueOf(rs.getString("POSITION"));
            LocalDate date = LocalDate.parse(rs.getString("HIREDATE"));
            BigDecimal wage = new BigDecimal(rs.getString("SALARY"));
            Department dep = rs.getString("DEPARTMENT") == null ? null : mapDep(new BigInteger(rs.getString("DEPARTMENT")));
            Employee manager = rs.getString("MANAGER") == null ? null : mapManagerWMC(new BigInteger(rs.getString("MANAGER")));
            return new Employee(
                    Id,
                    name,
                    pos,
                    date,
                    wage,
                    manager,
                    dep
            );
        } catch (SQLException e) {
            return null;
        }
    }
}
