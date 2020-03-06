package com.efimchik.ifmo.web.mvc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBC {
    private static final String URL = "jdbc:h2:mem:testdb";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private static DBC instance = new DBC();

    private DBC() {
        try {
            Class.forName("org.h2.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DBC getInstance() {
        return instance;
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
