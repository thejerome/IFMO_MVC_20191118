package com.efimchik.ifmo.web.mvc;

import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;
import com.efimchik.ifmo.web.mvc.source.SourceLaLaLa;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@RestController
@SpringBootApplication
public class MvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(MvcApplication.class, args);
    }

    @GetMapping("/employees")
    public List<Employee> employees(@RequestParam(required = false) Integer page,
                                    @RequestParam(required = false) Integer size,
                                    @RequestParam(required = false) String sort){
        List<Employee> ans = getListOfEmployee(sort);

        return getNeededList(page, size, ans);
    }

    @GetMapping("/employees/{employee_id:[\\d]+}")
    public Employee employee(@PathVariable int employee_id,
                             @RequestParam(required = false) Boolean isFull_chain,
                                   @RequestParam(required = false) String sort){
            return getListOfEmployeeById(
                    "id",
                    employee_id,
                    isFull_chain,
                    sort)
                    .get(0);
    }

    @GetMapping("/employees/by_manager/{managerId:[\\d]+}")
    public List<Employee> employeeByManager(@PathVariable int managerId,
                                            @RequestParam(required = false) Integer page,
                                            @RequestParam(required = false) Integer size,
                                            @RequestParam(required = false) String sort){
        List<Employee> ans = getListOfEmployeeById(
                    "manager",
                    managerId,
                    false,
                    sort);
        return getNeededList(page, size, ans);
    }

    @GetMapping("/employees/by_department/{department}")
    public List<Employee> employeeByDepartmentById(@PathVariable String department,
                                                   @RequestParam(required = false) Integer page,
                                                   @RequestParam(required = false) Integer size,
                                                   @RequestParam(required = false) String sort){
        if ("ACCOUNTING".equals(department)){
            department = "10";
        }else if ("RESEARCH".equals(department)){
            department = "20";
        }else if ("SALES".equals(department)){
            department = "30";
        }else if ("OPERATIONS".equals(department)){
            department = "40";
        }
        List<Employee> ans = getListOfEmployeeById("department",
                    Integer.valueOf(department),
                    false, sort);
        return getNeededList(page, size, ans);
    }

    private List<Employee> getNeededList (Integer page, Integer size, List<Employee> ans){
        if (page != null && size != null){
            int from = page * size;
            int to = ((page + 1) * size < ans.size())? (page + 1) * size : ans.size();
            return ans.subList(from, to);
        }else{
            return ans;
        }
    }

    private List<Employee> getListOfEmployee(String sort){
        try{
            ResultSet rs;
            if (sort != null){
                rs = giveMeResultSet("SELECT * FROM EMPLOYEE ORDER BY " + sort);
            }else{
                 rs = giveMeResultSet("SELECT * FROM EMPLOYEE");
            }
            List<Employee> ans = new ArrayList<>();
            while (rs.next()){
                ans.add(getEmployee(rs, false));
            }
            return ans;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    private List<Employee> getListOfEmployeeById(String param, int id, Boolean isFull_chain, String sort){
        try{
            ResultSet rs;
            if (sort != null){
                rs = giveMeResultSet("SELECT * FROM EMPLOYEE ORDER BY " + sort);
            }else{
                rs = giveMeResultSet("SELECT * FROM EMPLOYEE");
            }
            List<Employee> ans = new ArrayList<>();
            while (rs.next()){
                if (rs.getInt(param) == id){
                    if (isFull_chain != null){
                        ans.add(getEmployee(rs, isFull_chain));
                    }else{
                        ans.add(getEmployee(rs, false));
                    }
                }
            }
            return ans;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }


    private Employee getEmployee(ResultSet resultSet, boolean isFull_chain)throws SQLException{

        int thisRow = resultSet.getRow();
        Employee manager = null;

        if (resultSet.getInt("MANAGER") != 0){
            int managerID = resultSet.getInt("manager");
            resultSet.first();
            while (!resultSet.isAfterLast() &&
                    managerID != resultSet.getInt("id")){
                resultSet.next();
                //search manager row
            }
            if (!resultSet.isAfterLast()){//if found
                if (isFull_chain){
                    manager = getEmployee(resultSet, true);
                }else{
                    manager = new Employee(
                            Long.valueOf(resultSet.getString("id")),
                            new FullName(
                                    resultSet.getString("firstName"),
                                    resultSet.getString("lastName"),
                                    resultSet.getString("middleName")),
                            Position.valueOf(resultSet.getString("Position")),
                            LocalDate.parse(resultSet.getString("hireDate")),
                            new BigDecimal(resultSet.getDouble("Salary")),
                            null,
                            getDepartmentById(resultSet.getInt("DEPARTMENT"))
                    );
                }//*/
            }/*
            if (upperManager != null &&
                    managerID == upperManager.getId().intValue()) manager = upperManager;//*/
        }// we have manager*/
        resultSet.absolute( thisRow);


        return new Employee(
                Long.valueOf(resultSet.getString("id")),
                new FullName(
                        resultSet.getString("FIRSTNAME"),
                        resultSet.getString("LASTNAME"),
                        resultSet.getString("MIDDLENAME")),
                Position.valueOf(resultSet.getString("POSITION")),
                LocalDate.parse(resultSet.getString("HIREDATE")),
                new BigDecimal(resultSet.getDouble("SALARY")),
                manager,
                getDepartmentById(resultSet.getInt("DEPARTMENT"))
        );
    }

    public Department getDepartmentById(int Id) {
        try{
            ResultSet rs = giveMeResultSet("SELECT * FROM DEPARTMENT WHERE ID = " + Id);
            rs.next();
            Department ans = new Department(
                    Long.valueOf(rs.getString("id")),
                    rs.getString("Name"),
                    rs.getString("Location")
            );
            rs.close();
            return ans;
        }catch(SQLException e){
            return null;
        }
    }

    private ResultSet giveMeResultSet(String query) throws SQLException {
        return SourceLaLaLa.getInstance()
                .createConnection()
                .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
                .executeQuery(query);
    }
}



