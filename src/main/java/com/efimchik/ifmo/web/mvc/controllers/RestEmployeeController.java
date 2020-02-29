package com.efimchik.ifmo.web.mvc.controllers;

import com.efimchik.ifmo.web.mvc.DB;
import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@RestController
public class RestEmployeeController {
@GetMapping(value = "/employees")
    public ResponseEntity<List<Employee>> getAll(@RequestParam(required = false) Integer page,
                                                 @RequestParam(required = false) Integer size,
                                                 @RequestParam(required = false) String sort) throws SQLException {
        String query = "select * from employee";
        query += ((sort != null) ? " order by " + makeColName(sort) : " ");
    query += getPagingPartOfQuery(page, size);
    System.out.println(query);
    return ResponseEntity.ok(Objects.requireNonNull(getSortedEmployees(false, true, query)));
    }

    @GetMapping(value = "/employees/{employee}")
    public ResponseEntity<Employee> getById(@PathVariable(name = "employee") String employee,
                                            @RequestParam(name = "full_chain", required = false, defaultValue = "false") String fullChain) {
        String query = "select * from employee where id = " + employee;

        if ("true".equals(fullChain)) {
            return ResponseEntity.ok(getSortedEmployees(true, true, query).get(0));
        } else {
            return ResponseEntity.ok(getSortedEmployees(false, true, query).get(0));
        }
    }

    @GetMapping(value = "/employees/by_manager/{manager}")
    public ResponseEntity<List<Employee>> getByManagerId(@PathVariable(name = "manager") Integer manager,
                                                         @RequestParam(required = false) Integer page,
                                                         @RequestParam(required = false) Integer size,
                                                         @RequestParam(required = false) String sort) {

        String query = "select * from employee where manager = ";
        query += manager;
        query += ((sort != null) ? " order by " + makeColName(sort) : " ");
        query += getPagingPartOfQuery(page, size);
        System.out.println(query);
        return ResponseEntity.ok(Objects.requireNonNull(getSortedEmployees(false, true, query)));
    }

    @GetMapping(value = "/employees/by_department/{dep}")
    public ResponseEntity<List<Employee>> getByDepId(@PathVariable(name = "dep") String dep,
                                                     @RequestParam(required = false) Integer page,
                                                     @RequestParam(required = false) Integer size,
                                                     @RequestParam(required = false) String sort) {
        BigInteger depId;
        if(!isNumeric(dep)) {
            depId = getDepIdByName(dep);
        } else {
            depId = BigInteger.valueOf(Long.parseLong(dep));
        }
        String query = "select * from employee where department = ";
        query += depId;
        query += (sort != null) ? " order by " + makeColName(sort) : " ";
        query += getPagingPartOfQuery(page, size);
        System.out.println(query);
        //List<Employee> response = getSortedEmployees(false, true, query);
        return ResponseEntity.ok(Objects.requireNonNull(getSortedEmployees(false, true, query)));

    }

    private String makeColName(String sort) {
        if ("hired".equals(sort)) {
            return "hiredate";
        } else return sort;
    }

    private ResultSet execQueryAndGetRS(String query) {
        try {
            Statement statement = DB.getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Employee getEmployee(ResultSet rs, boolean implementManager, boolean withChain) throws SQLException {
        Employee manager = null;
        Department dep = null;
        if ((withChain || implementManager) && rs.getObject("manager") != null) {
            BigInteger managerId = new BigInteger(rs.getString("manager"));
            manager = getSortedEmployees(withChain, false, "select * from employee where id=" + managerId).get(0);
        }
        if (rs.getObject("department") != null) {
            dep = getDepById(BigInteger.valueOf(rs.getInt("department")));
        }
        return new Employee(new Long(rs.getString("id")),
                new FullName(
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("middleName")
                ),
                Position.valueOf(rs.getString("position")),
                LocalDate.parse(rs.getString("hireDate")),
                rs.getBigDecimal("salary"),
                manager,
                dep
        );
    }

    private Department getDep(ResultSet rs) throws SQLException {
        return new Department(new Long(rs.getString("id")),
                rs.getString("name"),
                rs.getString("location")
        );
    }

    private Department getDepById(BigInteger id) {
        String query = "select * from department where id=" + id;
        try {
            ResultSet rs = execQueryAndGetRS(query);
            rs.next();
            return getDep(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Employee> getSortedEmployees(boolean withChain, boolean implementManager, String query) {
        try {
            List<Employee> list = new LinkedList<Employee>();
            ResultSet rs = execQueryAndGetRS(query);
            while (rs.next()) {
                Employee employee = getEmployee(rs, implementManager, withChain);
                list.add(employee);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BigInteger getDepIdByName(String name) {
        String query = "select id from department where name = '" +name+ "'";
        Connection con;
        try {
            con = DB.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return new BigInteger(rs.getString("id"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean isNumeric(String str) {
        if (str == null) return false;
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private static String getPagingPartOfQuery(Integer page, Integer size) {
        String query = "";
        query += (size != null) ? " limit " + size : " ";
        query += (page != null) ? " offset " + size * page : " ";
        return query;
    }
}
