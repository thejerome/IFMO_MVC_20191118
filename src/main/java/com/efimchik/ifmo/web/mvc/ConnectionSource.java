package com.efimchik.ifmo.web.mvc;

import java.sql.*;

public class ConnectionSource {

    private static final ConnectionSource instance = new ConnectionSource();

    private static final String DB_URL = "jdbc:h2:mem:testdb";
    private static final String USER = "sa";
    private static final String PASS = "";

    public static ConnectionSource instance() {
        return instance;
    }

    public Connection createConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
}