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
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@RestController
public class MvcController {
    @GetMapping(value = "/employees")
    public List<Employee> getAllWithPaging(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size, @RequestParam(required = false) String sort) {
        String query = "select e.*, m.id manager_id, m.firstName manager_firstName, m.lastName manager_lastName, m.middleName manager_middleName, m.position manager_position, " +
                "m.hireDate manager_hiredate, m.salary manager_salary, m.department manager_department" +
                " from employee e left join employee m on e.manager=m.id" + ((sort != null) ? " order by " + colfix(sort) : " ") + ((size != null) ? (" limit " + size + " offset " + ((page) * size)) : "");
        return getEmployees(query);
    }

    @GetMapping(value = "/employees/by_manager/{man}")
    public List<Employee> getEmpByManWithPaging(@PathVariable(name = "man") Integer man, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size, @RequestParam(required = false) String sort) throws SQLException {
        String query = "select e.*, m.id manager_id, m.firstName manager_firstName, m.lastName manager_lastName, m.middleName manager_middleName, m.position manager_position, " +
                "m.hireDate manager_hiredate, m.salary manager_salary, m.department manager_department" +
                " from employee e left join employee m on e.manager=m.id" + " where e.manager=" + man + ((sort != null) ? " order by " + colfix(sort) : " ") + ((size != null) ? (" limit " + size + " offset " + ((page) * size)) : "");
        System.out.println(query);
        System.out.println(getEmployees(query));
        return getEmployees(query);
    }

    @GetMapping(value = "/employees/by_department/{dep}")
    public List<Employee> getEmpByDepWithPaging(@PathVariable(name = "dep") String dep, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size, @RequestParam(required = false) String sort) throws SQLException {
        String query = "select e.*, manager.id manager_id, manager.firstName manager_firstName, manager.lastName manager_lastName, manager.middleName manager_middleName, manager.position manager_position, " +
                "manager.hireDate manager_hiredate, manager.salary manager_salary, manager.department manager_department" +
                " from employee e left join employee manager on e.manager=manager.id left join department d on e.department=d.id";
        boolean isId = true;
        try {
            Integer.parseInt(dep);
        } catch (Exception e) {
            isId = false;
        }
        if (!isId) query += " where e.department is not null and d.name='" + dep + "'";
        else query += " where e.department=" + dep;
        query += ((sort != null) ? " order by " + colfix(sort) : " ") + ((size != null) ? (" limit " + size + " offset " + ((page) * size)) : "");
        return getEmployees(query);
    }

    @GetMapping(value = "/employees/{employee}")
    public Employee getEmployeeSupportsChain(@PathVariable(name = "employee") String employee, @RequestParam(name = "full_chain", required = false, defaultValue = "false") String fullChain) throws SQLException {
        String query = "select e.*, manager.id manager_id, manager.firstName manager_firstName, manager.lastName manager_lastName, manager.middleName manager_middleName, manager.position manager_position, " +
                "manager.hireDate manager_hiredate, manager.salary manager_salary, manager.department manager_department" +
                " from employee e left join employee manager on e.manager=manager.id where e.id=" + employee;
        if ("true".equals(fullChain)) {
            query = "select e.*, d.name dname, d.location dlocation from employee e left join department d on e.department=d.id where e.id=" + employee;
                ResultSet RS = DBConnection.getInstance().getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(query);
                if (RS.next()) return getChain(RS);
        } else {
                ResultSet RS = DBConnection.getInstance().getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(query);
                if (RS.next()) return getEmpWManager(RS);
        }
        return null;
    }

    private String colfix(String sort) {
        return "hired".equals(sort) ? "hiredate" : sort;
    }

    private List<Employee> getEmployees(String query) {
        List<Employee> employees = new LinkedList<>();
        try {
            ResultSet RS = DBConnection.getInstance().getConnection().createStatement().executeQuery(query);
            while (RS.next()) employees.add(getEmpWManager(RS));
            return employees;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Employee getEmpWManager(ResultSet RS) {
        try {
            BigInteger id = new BigInteger(RS.getString("id"));
            FullName fName = new FullName(RS.getString("firstName"), RS.getString("lastName"), RS.getString("middleName"));
            Position position = Position.valueOf(RS.getString("position"));
            LocalDate date = LocalDate.parse(RS.getString("hireDate"));
            BigDecimal salary = RS.getBigDecimal("salary");
            Department dep;
            Department mdep;
            Employee man;
            if (RS.getObject("manager") != null) {
                BigInteger mid = new BigInteger(RS.getString("manager_id"));
                FullName mfName = new FullName(RS.getString("manager_firstName"), RS.getString("manager_lastName"), RS.getString("manager_middleName"));
                Position mposition = Position.valueOf(RS.getString("manager_position"));
                LocalDate mdate = LocalDate.parse(RS.getString("manager_hireDate"));
                BigDecimal msalary = RS.getBigDecimal("manager_salary");
                int mdepInt = (RS.getObject("manager_department")!= null) ? RS.getInt("manager_department") : 0;
                int depInt = (RS.getObject("department") != null) ? RS.getInt("department") : 0;
                dep = (depInt != 0) ? getDep(depInt) : null;
                mdep = (mdepInt != 0) ? getDep(mdepInt) : null;
                man = new Employee(Long.parseLong(String.valueOf(mid)), mfName, mposition, mdate, msalary, null, mdep);
                return new Employee(Long.parseLong(String.valueOf(id)), fName, position, date, salary, man, dep);
            }
            dep = RS.getObject("department") != null ? getDep(Integer.parseInt(RS.getString("department"))) : null;
            return new Employee(Long.parseLong(String.valueOf(id)), fName, position, date, salary, null, dep);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Department getDep(Integer id) throws SQLException {
        String query = "select * from department where id=?";
        PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(query);
        statement.setInt(1, id);
        ResultSet RS = statement.executeQuery();
        if (RS.next()) return new Department(Long.parseLong(RS.getString("id")), RS.getString("name"), RS.getString("location"));
        return null;
    }

    private Employee getChain(ResultSet RS) throws SQLException {
        Department dep = (RS.getObject("department") == null) ? null : new Department(Long.parseLong(RS.getString("department")), RS.getString("dname"), RS.getString("dlocation"));
        return new Employee(
                Long.parseLong(RS.getString("id")),
                new FullName(RS.getString("firstName"),RS.getString("lastName"),RS.getString("middleName")),
                Position.valueOf(RS.getString("position")),
                LocalDate.parse(RS.getString("hireDate")),
                RS.getBigDecimal("salary"),
                (RS.getObject("manager")!=null) ? getManChain(BigInteger.valueOf(RS.getInt("manager"))) : null,
                dep
        );
    }

    private Employee getManChain(BigInteger id) throws SQLException {
        String query = "select e.*, d.name dname, d.location dlocation from employee e left join department d on e.department=d.id where e.id=" + String.valueOf(id);
        ResultSet RS = DBConnection.getInstance().getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(query);
        if (RS.next()) return getChain(RS);
        return null;
    }
}
