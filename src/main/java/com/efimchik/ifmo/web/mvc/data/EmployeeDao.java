package com.efimchik.ifmo.web.mvc.data;

import com.efimchik.ifmo.web.mvc.controller.PagingRequest;
import com.efimchik.ifmo.web.mvc.domain.Department;
import com.efimchik.ifmo.web.mvc.domain.Employee;
import com.efimchik.ifmo.web.mvc.domain.FullName;
import com.efimchik.ifmo.web.mvc.domain.Position;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

@Repository
public class EmployeeDao {

    private static final String EMPTY_STRING = "";

    private final DepartmentDao departmentDao;

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    public EmployeeDao(DepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
    }

    public List<Employee> findAllEmployees(PagingRequest pagingRequest) {
        return performListQueryWithPagination(
                "select * from employee",
                pagingRequest
        );
    }

    public Employee findById(String id, boolean isFirst, boolean cascade) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from employee where id = " + id);
            if (resultSet.next()) {
                return extractFromResultSet(resultSet, isFirst, cascade);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Error performing SQL query");
        }
    }

    public List<Employee> findByManagerId(String managerId, PagingRequest pagingRequest) {
        return performListQueryWithPagination(
                "select * from employee where manager = " + managerId,
                pagingRequest
        );
    }

    public List<Employee> findByDepartmentId(String department, PagingRequest pagingRequest) {
        return performListQueryWithPagination(
                "select * from employee where department = " + department,
                pagingRequest
        );
    }

    public List<Employee> findByDepartmentName(String department, PagingRequest pagingRequest) {
        return performListQueryWithPagination(
                "select * from employee e join department d on e.department = d.id where d.name = '" + department + "'",
                pagingRequest
        );
    }

    private List<Employee> performListQueryWithPagination(String sql, PagingRequest pagingRequest) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql + preparePaging(pagingRequest));
            List<Employee> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(extractFromResultSet(resultSet, true, false));
            }
            return result;
        } catch (SQLException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Error performing SQL query");
        }
    }

    private String preparePaging(PagingRequest pagingRequest) {
        return new StringBuilder()
                .append(
                        ofNullable(pagingRequest.getSort())
                                .map(sort -> " order by " + ("hired".equalsIgnoreCase(sort) ? "hiredate" : sort))
                                .orElse(EMPTY_STRING))
                .append(
                        ofNullable(pagingRequest.getSize())
                                .map(size -> " limit " + pagingRequest.getSize())
                                .orElse(EMPTY_STRING))
                .append(
                        pagingRequest.getSize() != null && pagingRequest.getPage() != null ?
                                " offset " + pagingRequest.getSize() * pagingRequest.getPage() : EMPTY_STRING)
                .toString();
    }

    private Employee extractFromResultSet(ResultSet resultSet, boolean isFirst, boolean cascade) throws SQLException {
        FullName fullName = new FullName(
                resultSet.getString("firstName"),
                resultSet.getString("lastName"),
                resultSet.getString("middleName")
        );

        Employee manager =
                ofNullable(isFirst || cascade ? resultSet.getBigDecimal("manager") : null)
                        .map(id -> findById(id.toString(), false, cascade))
                        .orElse(null);

        Department department =
                ofNullable(resultSet.getBigDecimal("department"))
                        .map(BigDecimal::toBigInteger)
                        .map(departmentDao::getById)
                        .orElse(null);

        return new Employee(
                resultSet.getLong("id"),
                fullName,
                Position.valueOf(resultSet.getString("position")),
                resultSet.getDate("hireDate").toLocalDate(),
                resultSet.getBigDecimal("salary"),
                manager,
                department
        );

    }

}
