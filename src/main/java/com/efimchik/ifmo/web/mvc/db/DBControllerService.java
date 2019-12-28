package com.efimchik.ifmo.web.mvc.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Service
public class DBControllerService {

    private Connection connection;

    @Autowired
    public void setDataSource(final DataSource dataSource) throws SQLException {
        connection = dataSource.getConnection();
    }

    public ResultSet executeSQL(String query) {
        try {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }



}
