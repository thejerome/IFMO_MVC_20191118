package com.efimchik.ifmo.web.mvc;

import java.sql.*;

class ConnectionSource {
    static Connection createConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:mem:testdb", "SA", "");
    }

    static ResultSet generateSqlQuery(String sql) throws SQLException {
        Connection connection = ConnectionSource.createConnection();
        return connection.createStatement().executeQuery(sql);
    }
}