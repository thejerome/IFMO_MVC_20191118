package com.efimchik.ifmo.web.mvc;


import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.RestController;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@RestController
public class ControllerAndSomethingElse {

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getEmployees(@RequestParam(required = false) Integer size,
                                                        @RequestParam(required = false) Integer page,
                                                        @RequestParam(required = false) String sort) {
        String sqlQuery = "select * from employee";
        return newResponseEntity(sqlQuery, size, page, sort);
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id, @RequestParam(required = false) String full_chain) {
        String sqlQuery = "select * from employee where id=" + id;
        Employee employee = null;
        if (full_chain != null && full_chain.equals("true")) {
            employee = getWithDepartmentAndFullManagerChain(id);
        } else {
            List<Employee> employees = newList(new Paging(0, 50), sqlQuery);
            if (employees != null) {
                employee = employees.get(0);
            }
        }
        if (employee == null) {
            return ResponseEntity.of(Optional.empty());
        } else {
            return ResponseEntity.of(Optional.of(employee));
        }
    }

    @GetMapping("/employees/by_manager/{managerID}")
    public ResponseEntity<List<Employee>> getEmployeesByManager(@RequestParam(required = false) Integer size,
                                                                 @RequestParam(required = false) Integer page,
                                                                 @RequestParam(required = false) String sort,
                                                                 @PathVariable Integer managerID) {
        String sqlQuery = "SELECT * FROM EMPLOYEE WHERE MANAGER=" + managerID;
        return newResponseEntity(sqlQuery, size, page, sort);
    }

    @GetMapping("/employees/by_department/{department}")
    public ResponseEntity<List<Employee>> getEmployeesByDepartment(@RequestParam(required = false) Integer size,
                                                                    @RequestParam(required = false) Integer page,
                                                                    @RequestParam(required = false) String sort,
                                                                    @PathVariable String department) {
        String sqlQuery;
        if (isNumber(department)) {
            sqlQuery = "select * from employee where department=" + department;
        } else {
            sqlQuery = "select * from employee inner join department on department.id=employee.department where department.name='" + department + "'";
        }
        return newResponseEntity(sqlQuery, size, page, sort);
    }

    private boolean isNumber(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    private ResultSet newResultSet(String query) throws SQLException {
        return DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "").createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(query);
    }

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }


    private Employee newEmployee(ResultSet resultSet, boolean withChain, int level) {
        try {
            if (level > 1) {
                return null;
            }
            Employee manager = null;
            if (resultSet.getString("manager") != null) {
                if (withChain) {
                    String manID = resultSet.getString("manager");
                    int rowID = resultSet.getRow();
                    resultSet.beforeFirst();
                    while (resultSet.next()) {
                        if (resultSet.getString("id").equals(manID)) {
                            manager = newEmployee(resultSet, true, 0);
                            break;
                        }
                    }
                    resultSet.absolute(rowID);
                } else {
                    ResultSet newResultSet = newResultSet("select * from employee");
                    while (newResultSet.next()) {
                        if (newResultSet.getString("id").equals(resultSet.getString("manager"))) {
                            manager = newEmployee(newResultSet, false, level + 1);
                            break;
                        }
                    }
                }
            }
            return new Employee(
                    resultSet.getLong("id"),
                    new FullName(
                            resultSet.getString("firstname"),
                            resultSet.getString("lastname"),
                            resultSet.getString("middlename")
                    ),
                    Position.valueOf(resultSet.getString("position")),
                    LocalDate.parse(resultSet.getString("hiredate")),
                    new BigDecimal(resultSet.getString("salary")),
                    manager,
                    newDepartment(resultSet.getString("department")));
        } catch (SQLException e) {
            return null;
        }
    }

    private Department newDepartment(String ID) {
        try {
            if (ID == null) {
                return null;
            }
            ResultSet resultSet = newResultSet("select * from department");
            while (resultSet.next()) {
                if (ID.equals(resultSet.getString("id"))) {
                    return new Department(
                            resultSet.getLong("id"),
                            resultSet.getString("name"),
                            resultSet.getString("location")
                    );
                }
            }
            return null;
        } catch (SQLException exception) {
            return null;
        }
    }

    private List<Employee> newList(Paging paging, String SQLString) {
        try {
            ResultSet resultSet = newResultSet(SQLString);
            List<Employee> result = new LinkedList<>();
            int item = paging.page * paging.itemPerPage;
            resultSet.absolute(item);
            while (resultSet.next() && item < (paging.page + 1) * paging.itemPerPage) {
                result.add(newEmployee(resultSet, false, 0));
                item++;
            }
            return result;
        } catch (SQLException exception) {
            return null;
        }
    }

    private Employee getWithDepartmentAndFullManagerChain(Long employeeId) {
        try {
            ResultSet resultSet = newResultSet("select * from employee");
            while (resultSet.next()) {
                if (resultSet.getString("id").equals(String.valueOf(employeeId))) {
                    return newEmployee(resultSet, true, 0);
                }
            }
            return null;
        } catch (SQLException exception) {
            return null;
        }
    }

    private ResponseEntity<List<Employee>> newResponseEntity(String sqlQuery, Integer size, Integer page, String sort) {
        String newSqlQuery = sqlQuery;
        if (sort != null && sort.equals("hired")) {
            newSqlQuery += " order by hiredate";
        } else if (sort != null) {
            newSqlQuery += " order by " + sort;
        }
        if (page == null) {
            return ResponseEntity.ok(newList(new Paging(0, 50), newSqlQuery));
        } else {
            return ResponseEntity.ok(newList(new Paging(page, size), newSqlQuery));
        }
    }

}
