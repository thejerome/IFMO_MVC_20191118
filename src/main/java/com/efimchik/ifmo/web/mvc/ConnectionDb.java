package com.efimchik.ifmo.web.mvc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDb {

    private static String DB_URL = "jdbc:h2:mem:testdb";
    private static String USER = "sa";
    private static String PASS = "";

    public static final ConnectionDb instance = new ConnectionDb(DB_URL, USER, PASS);

    public static ConnectionDb instance() {
        return instance;
    }

    private ConnectionDb(String DB_URL, String USER, String PASS) {
        this.DB_URL = DB_URL;
        this.USER = USER;
        this.PASS = PASS;
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            return null;
        }
    }
}
