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

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
public class MainController {

    private static final String  queryWithManager = "select e.*, m.id mid, m.firstname mfirstName, m.lastName mlastName, " +
            "m.middleName mmiddleName, m.position mposition, m.hireDate mhiredate, " +
            "m.salary msalary, m.department mdepartment, m.manager mmanager, d.name dname, d.location dlocation, " +
            "md.name mdname, md.location mdlocation " +
            "from employee e " +
            "left join employee m on e.manager=m.id " +
            "left join department d on e.department=d.id " +
            "left join department md on m.department=md.id ";
    private static final String queryWithChain = "select e.*, d.name dname, d.location dlocation from employee e left join department d on e.department=d.id";

    @GetMapping(value = "/employees")
    public ResponseEntity<List<Employee>> getAll(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size, @RequestParam(required = false) String sort) {
        String query = queryWithManager + ((sort != null) ? " order by " + fixHireDate(sort) : " ") + getPagingPart(page, size);
        return ResponseEntity.ok(Objects.requireNonNull(getEmployeesByQuery(query)));
    }

    @GetMapping(value = "/employees/{employee}")
    public ResponseEntity<Employee> getOne(@PathVariable(name = "employee") String employee, @RequestParam(name = "full_chain", required = false, defaultValue = "false") String fullChain) throws SQLException {
        String query = queryWithManager + "where e.id=" + employee;
        if ("true".equals(fullChain)) {
            query = queryWithChain + " where e.id=" + employee;
            try {
                ResultSet resultSet = DataBase.instance().createConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(query);
                if (resultSet.next()) {
                    return ResponseEntity.ok(getEmployeeWithChain(resultSet));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.ok(Objects.requireNonNull(getEmployeesByQuery(query)).get(0));
    }

    @GetMapping(value = "/employees/by_manager/{manager}")
    public ResponseEntity<List<Employee>> getByManager(@PathVariable(name = "manager") Integer manager, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size, @RequestParam(required = false) String sort) throws SQLException {

        String query = queryWithManager + "where e.manager=" + manager + ((sort != null) ? " order by " + fixHireDate(sort) : " ") + getPagingPart(page, size);
        return ResponseEntity.ok(Objects.requireNonNull(getEmployeesByQuery(query)));
    }

    @GetMapping(value = "/employees/by_department/{dep}")
    public ResponseEntity<List<Employee>> getByDep(@PathVariable(name = "dep") String dep, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size, @RequestParam(required = false) String sort) throws SQLException {
        String query = queryWithManager;
        if(!isDigit(dep)) {
            query += "where d.id is not null and d.name='" + dep + "'";
        } else {
            query += "where d.id=" + dep;
        }
        query += ((sort != null) ? " order by " + fixHireDate(sort) : " ") + getPagingPart(page, size);

        return ResponseEntity.ok(Objects.requireNonNull(getEmployeesByQuery(query)));
    }

    private String fixHireDate(String sort) {
        if ("hired".equals(sort))
            return "hiredate";
        return sort;
    }

    private List<Employee> getEmployeesByQuery(String query) {
        List<Employee> employees = new ArrayList<>();
        try {
            ResultSet resultSet = DataBase.instance().createConnection().createStatement().executeQuery(query);
            while (resultSet.next()) {
                    employees.add(getEmployeeWithManager(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return employees;
    }

    private Employee getEmployeeWithManager(ResultSet resultSet) throws SQLException {
        Employee manager = (resultSet.getObject("manager")!=null) ? getManager(resultSet) : null;
        Department department = (resultSet.getObject("department") == null) ? null : new Department(
                Long.parseLong(resultSet.getString("department")), resultSet.getString("dname"), resultSet.getString("dlocation")
        );
        return getEmployee(resultSet, manager, department);
    }

    private Employee getManager(ResultSet resultSet) throws SQLException {
        Department department = (resultSet.getObject("mdepartment") == null) ? null : new Department(
                Long.parseLong(resultSet.getString("mdepartment")), resultSet.getString("mdname"), resultSet.getString("mdlocation")
        );
        return new Employee(
                Long.parseLong(resultSet.getString("manager")),
                new FullName(
                        resultSet.getString("mfirstName"),
                        resultSet.getString("mlastName"),
                        resultSet.getString("mmiddleName")
                ),
                Position.valueOf(resultSet.getString("mposition")),
                LocalDate.parse(resultSet.getString("mhireDate")),
                resultSet.getBigDecimal("msalary"),
                null,
                department
        );
    }
    private Employee getEmployee(ResultSet resultSet, Employee manager, Department department) throws SQLException {
        return new Employee(
                Long.parseLong(resultSet.getString("id")),
                new FullName(
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("middleName")
                ),
                Position.valueOf(resultSet.getString("position")),
                LocalDate.parse(resultSet.getString("hireDate")),
                resultSet.getBigDecimal("salary"),
                manager,
                department
        );
    }

    private Employee getEmployeeWithChain(ResultSet resultSet) throws SQLException {
        return getEmployeeForChain(resultSet);
    }

    private Employee getEmployeeForChain(ResultSet resultSet) throws SQLException {
        Department department = (resultSet.getObject("department") == null) ? null : new Department(
                Long.parseLong(resultSet.getString("department")),
                resultSet.getString("dname"), resultSet.getString("dlocation"));
        return new Employee(
                Long.parseLong(resultSet.getString("id")),
                new FullName(
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("middleName")
                ),
                Position.valueOf(resultSet.getString("position")),
                LocalDate.parse(resultSet.getString("hireDate")),
                resultSet.getBigDecimal("salary"),
                (resultSet.getObject("manager")!=null) ? getManagerWithChain(BigInteger.valueOf(resultSet.getInt("manager"))) : null, department
        );
    }

    private Employee getManagerWithChain(BigInteger manager) throws SQLException {
        ResultSet resultSet = DataBase.instance().createConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery((queryWithChain + " where e.id=" + manager.toString()));
        if (resultSet.next()) {
            return getEmployeeForChain(resultSet);
        }
        return null;
    }


    private static boolean isDigit(String str) {
        try {
            Integer.parseInt(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static String getPagingPart(Integer page, Integer size) {
        return (((size != null) ? " limit " + size : " ") + ((page != null) ? " offset " + size * page : " "));
    }
}
