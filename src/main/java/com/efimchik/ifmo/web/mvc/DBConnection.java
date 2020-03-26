package com.efimchik.ifmo.web.mvc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final DBConnection instance = new DBConnection();

    public static DBConnection getInstance() {
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
    }

    private DBConnection() {
        try {
            Class.forName("org.h2.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
