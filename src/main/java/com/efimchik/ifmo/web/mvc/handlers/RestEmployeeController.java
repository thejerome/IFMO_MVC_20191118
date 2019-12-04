package com.efimchik.ifmo.web.mvc.handlers;

import com.efimchik.ifmo.web.mvc.ConnectionSource;
import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static com.efimchik.ifmo.web.mvc.handlers.EmployeeService.*;

@RestController
public class RestEmployeeController {

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees(@RequestParam(required = false) Integer page,
                                                          @RequestParam(required = false) Integer size,
                                                          @RequestParam(required = false) String sort) throws SQLException {
        sort = mapToNormalColumnName(sort);
        String query = "select * from EMPLOYEE" +
                ((sort != null) ? " order by " + sort : " ") +
                ((size != null) ? " limit " + size : " ") +
                ((page != null) ? " offset " + size * page : " ");
        PreparedStatement preparedStatement = getPreparedStatement(query);
        return ResponseEntity.ok(getEmployeeListByResultSet(preparedStatement.executeQuery()));
    }



    @GetMapping("/employees/{employee_id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable(name = "employee_id") String employeeId,
                                                    @RequestParam(name = "full_chain", required = false, defaultValue = "false") String fullChain) throws SQLException {
        String query = "select * from employee where id = " + employeeId;
        PreparedStatement preparedStatement = getPreparedStatement(query);
        if (fullChain != null && fullChain.equals("true")){
            return ResponseEntity.of(
                    Optional.ofNullable(
                            getEmployeeByIdWithChain(new BigInteger(employeeId))
                    )
            );
        } else {
            return ResponseEntity.of(
                    Optional.ofNullable(
                            getEmployeeListByResultSet(preparedStatement.executeQuery()).get(0))
            );
        }
    }



    @GetMapping("/employees/by_manager/{managerId}")
    public ResponseEntity<List<Employee>> getEmployeesByManagerId(@RequestParam(required = false) Integer page,
                                                                  @RequestParam(required = false) Integer size,
                                                                  @RequestParam(required = false) String sort,
                                                                  @PathVariable Integer managerId) throws SQLException {
        sort = mapToNormalColumnName(sort);
        String query = "select * from EMPLOYEE where MANAGER = " + managerId +
                ((sort != null) ? " order by " + sort : " ") +
                ((size != null) ? " limit " + size : " ") +
                ((page != null) ? " offset " + size * page : " ");
        PreparedStatement preparedStatement = getPreparedStatement(query);
        return ResponseEntity.ok(getEmployeeListByResultSet(preparedStatement.executeQuery()));
    }



    @GetMapping("/employees/by_department/{departmentIdOrName}")
    public ResponseEntity<List<Employee>> getEmployeesByDepartmentId(@RequestParam(required = false) Integer page,
                                             @RequestParam(required = false) Integer size,
                                             @RequestParam(required = false) String sort,
                                             @PathVariable String departmentIdOrName) throws SQLException {
        Long depId;
        try{
            depId = Long.parseLong(departmentIdOrName);
        }catch (NumberFormatException e){
            String findDepQuery = "select * from department where name = '" + departmentIdOrName + "'";
            PreparedStatement preparedStatement = getPreparedStatement(findDepQuery);
            Optional<Department> department = Optional.ofNullable(
                    getDepartmentListByResultSet(preparedStatement.executeQuery()).get(0)
            );
            if (department.isPresent()) {
                depId = department.get().getId();
            } else {
                depId = 0L;
            }
        }


        sort = mapToNormalColumnName(sort);
        String query = "select * from EMPLOYEE where DEPARTMENT = " + depId +
                ((sort != null) ? " order by " + sort : " ") +
                ((size != null) ? " limit " + size : " ") +
                ((page != null) ? " offset " + size * page : " ");
        PreparedStatement preparedStatement = getPreparedStatement(query);
        return ResponseEntity.ok(getEmployeeListByResultSet(preparedStatement.executeQuery()));
    }



    private String mapToNormalColumnName(String sort){
        if (sort != null && sort.equals("hired")){
            sort = "HIREDATE";
        }
        return sort;
    }

    private PreparedStatement getPreparedStatement(String query) throws SQLException {
        return ConnectionSource.instance().createConnection().prepareStatement(query);
    }

}
