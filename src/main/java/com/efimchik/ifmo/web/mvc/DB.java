package com.efimchik.ifmo.web.mvc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {

    private static DB instance = new DB();

    public static DB getInstance() {
        return instance;
    }

    private DB() {
        try {
            Class.forName("org.h2.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        String PASSWORD = "";
        String USER = "sa";
        String URL = "jdbc:h2:mem:testdb";
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
