package com.efimchik.ifmo.web.mvc.domain;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;



@RestController
public class Result {

    private ResultSet getResultSet(String sql){

        try {

            return createConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(sql);
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Result() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
    }



    private Employee EmpMapRow(ResultSet resultSet, boolean a, boolean MainManager ){

        try {
            Long id = Long.valueOf(resultSet.getString("ID"));

            FullName fullName = new FullName(
                    resultSet.getString("FIRSTNAME"),
                    resultSet.getString("LASTNAME"),
                    resultSet.getString("MIDDLENAME")
            );

            Position position = Position.valueOf(resultSet.getString("POSITION"));

            LocalDate hiredate = LocalDate.parse(resultSet.getString("HIREDATE"));

            BigDecimal salary = new BigDecimal(String.valueOf(resultSet.getInt("SALARY")));

            Employee manager = null;

            if (!a){int manId = resultSet.getInt("MANAGER");
                ResultSet managerResultSet = getResultSet("SELECT * FROM employee WHERE id = " + manId);
                assert managerResultSet != null;
                if (managerResultSet.next())
                    manager = EmpMapRow(managerResultSet, !MainManager, MainManager);
            }


            Department result = null;
            int depId = resultSet.getInt("DEPARTMENT");

            ResultSet departmentResultSet = getResultSet("SELECT * FROM department WHERE id = " + depId);

            assert departmentResultSet != null;
            if (departmentResultSet.next()){

                Long id1 = Long.valueOf(departmentResultSet.getString("ID"));
                String name = departmentResultSet.getString("NAME");
                String location = departmentResultSet.getString("LOCATION");

                result = new Department(id1, name, location);

            }



            return new Employee(id, fullName, position, hiredate, salary, manager, result);
        }
        catch (SQLException e) {
            return null;
        }
    }

    private List<Employee> getEmployees(String req) throws SQLException{
        List<Employee> allEmployees = new ArrayList<>();
        ResultSet resultSet = getResultSet(req);
        if (resultSet != null){
            while (resultSet.next()){
                allEmployees.add(EmpMapRow(resultSet, false, false));
            }
            return allEmployees;
        }
        else return null;
    }

    private List<Department> getDepartments(String req) throws SQLException {

        ResultSet resultset = getResultSet(req);

        List<Department> allDepartments = new ArrayList<>();

        assert resultset != null;
        while (resultset.next()){
            Long id = Long.valueOf(resultset.getString("ID"));
            String name = resultset.getString("NAME");
            String location = resultset.getString("LOCATION");
            Department dep = new Department(id, name, location);
            allDepartments.add(dep);
        }


        return allDepartments;
    }




    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees(@RequestParam(required = false) Integer page,
                                                          @RequestParam(required = false) Integer size,
                                                          @RequestParam(required = false) String sort) throws SQLException {

        if (sort != null && sort.equals("hired"))
            sort = "HIREDATE";

        String request;
        final String s = (page != null) ? " OFFSET " + size * page : " ";
        if (sort != null) if (size != null) request = "SELECT * FROM EMPLOYEE" +
                (" ORDER BY " + sort) +
                (" LIMIT " + size) +
                s;
        else request = "SELECT * FROM EMPLOYEE" +
                    (" ORDER BY " + sort) +
                    " " +
                    s;
        else if (size != null) request = "SELECT * FROM EMPLOYEE" +
                " " +
                (" LIMIT " + size) +
                s;
        else request = "SELECT * FROM EMPLOYEE" +
                    " " +
                    " " +
                    s;

        return new ResponseEntity<>(getEmployees(request), HttpStatus.OK);
    }

    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable(name = "employeeId") String employeeId,
                                                    @RequestParam(name = "full_chain", required = false, defaultValue = "false") String isFullChain) throws SQLException {

        ResultSet resultSet = getResultSet("SELECT * FROM employee WHERE id = " + Integer.parseInt(employeeId));

        assert resultSet != null;
        if (resultSet.next()) {
            return new ResponseEntity<> (EmpMapRow(resultSet, false, Boolean.parseBoolean(isFullChain)), HttpStatus.OK);
        }
        else
            return null;


    }

    @GetMapping("/employees/by_manager/{managerId}")
    public ResponseEntity<List<Employee>> getEmployeesByManagerId(@RequestParam(required = false) Integer page,
                                                                  @RequestParam(required = false) Integer size,
                                                                  @RequestParam(required = false) String sort,
                                                                  @PathVariable Integer managerId) throws SQLException {

        if (sort != null && sort.equals("hired"))
            sort = "HIREDATE";

        String request;
        String s = (page != null) ? " OFFSET " + size * page : " ";
        if (sort != null) if (size != null) request = "SELECT * FROM employee WHERE manager = " + managerId +
                (" ORDER BY " + sort) +
                (" LIMIT " + size) +
                s;
        else request = "SELECT * FROM employee WHERE manager = " + managerId +
                    (" ORDER BY " + sort) +
                    " " +
                    s;
        else if (size != null) request = "SELECT * FROM employee WHERE manager = " + managerId +
                " " +
                (" LIMIT " + size) +
                s;
        else request = "SELECT * FROM employee WHERE manager = " + managerId +
                    " " +
                    " " +
                    s;

        return new ResponseEntity<>(getEmployees(request), HttpStatus.OK);

    }

    @GetMapping("/employees/by_department/{dep}")
    public ResponseEntity<List<Employee>> getEmployeesByDep(@RequestParam(required = false) Integer page,
                                                            @RequestParam(required = false) Integer size,
                                                            @RequestParam(required = false) String sort,
                                                            @PathVariable String dep) throws SQLException {

        Long departmentId;


        try { departmentId = Long.parseLong(dep); }
        catch (NumberFormatException e) {
            Department res = getDepartments("SELECT * FROM department WHERE name = '" + dep + "'").get(0);
            departmentId = res.getId();
        }

        if (sort != null && sort.equals("hired"))
            sort = "HIREDATE";

        String request;
        if (sort != null) if (size != null)
            if (page != null) request = "SELECT * FROM employee WHERE department = " + departmentId +
                    (" ORDER BY " + sort) +
                    (" LIMIT " + size) +
                    String.format(" OFFSET %d", size * page);
            else request = "SELECT * FROM employee WHERE department = " + departmentId +
                    (" ORDER BY " + sort) +
                    (" LIMIT " + size) +
                    " ";
        else if (page != null) request = "SELECT * FROM employee WHERE department = " + departmentId +
                (" ORDER BY " + sort) +
                " " +
                String.format(" OFFSET %d", size * page);
        else request = "SELECT * FROM employee WHERE department = " + departmentId +
                    (" ORDER BY " + sort) +
                    " " +
                    " ";
        else if (size != null) request = "SELECT * FROM employee WHERE department = " + departmentId +
                " " +
                (" LIMIT " + size) +
                ((page != null) ? String.format(" OFFSET %d", size * page) : " ");
        else if (page != null) request = "SELECT * FROM employee WHERE department = " + departmentId +
                " " +
                " " +
                String.format(" OFFSET %d", size * page);
        else request = "SELECT * FROM employee WHERE department = " + departmentId +
                    " " +
                    " " +
                    " ";

        return new ResponseEntity<>(getEmployees(request), HttpStatus.OK);
    }


}