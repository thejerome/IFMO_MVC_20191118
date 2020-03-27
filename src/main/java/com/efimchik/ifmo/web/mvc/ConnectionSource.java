package com.efimchik.ifmo.web.mvc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionSource {
    private static ConnectionSource instance;

    static {
        try {
            instance = new ConnectionSource();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ConnectionSource instance() {
        return instance;
    }

    public Connection createConnection() throws SQLException {
        return DriverManager.getConnection(
            "jdbc:h2:mem:testdb",
            "sa",
            ""
        );
    }

    private ConnectionSource() throws ClassNotFoundException {
        Class.forName("org.h2.Driver");
    }
}
