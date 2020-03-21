package com.efimchik.ifmo.web.mvc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    // JDBC database URL
    private static final String DB_URL = "jdbc:h2:mem:testdb";

    //  Database credentials
    private static final String USER = "sa";
    private static final String PASS = "";

    private static final DB instance = new DB();

    public static DB instance() {
        return instance;
    }

    public Connection createConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    private DB() {
        try {
            Class.forName("org.h2.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
