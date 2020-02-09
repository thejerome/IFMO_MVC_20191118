package com.efimchik.ifmo.web.mvc.data;

import com.efimchik.ifmo.web.mvc.domain.Department;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigInteger;
import java.sql.*;

@Repository
public class DepartmentDao {

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    public Department getById(BigInteger id) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement =
                    connection.prepareStatement("select * from department where id = ?");
            statement.setLong(1, id.longValue());
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return extractFromResultSet(resultSet);
        } catch (SQLException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Error performing SQL query");
        }
    }

    protected Department extractFromResultSet(ResultSet resultSet) throws SQLException {
        return new Department(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("location")
        );
    }

}
