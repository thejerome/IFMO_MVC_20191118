package com.efimchik.ifmo.web.mvc;

import java.sql.*;

public class DBConnection {

    private static final String URL = "jdbc:h2:mem:testdb";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private static DBConnection instance = new DBConnection();

    public static DBConnection getInstance() {
        return instance;
    }

    private DBConnection() {
        try {
            Class.forName("org.h2.Driver");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
