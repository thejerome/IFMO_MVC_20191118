package com.efimchik.ifmo.web.mvc;

import java.sql.*;

class ConnectionSource {
    private static final String DB_URL = "jdbc:h2:mem:testdb";
    private static final ConnectionSource instance = new ConnectionSource();

    private static ConnectionSource instance() {
        return instance;
    }

    static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, "SA", "");
    }

    private ConnectionSource() {
        try {
            Class.forName("org.h2.Driver");
        } catch ( ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}