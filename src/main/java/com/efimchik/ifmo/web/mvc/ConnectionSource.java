package com.efimchik.ifmo.web.mvc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionSource {

    //database URL
    private static final String DB_URL = "jdbc:h2:mem:testdb";

    //database  login and password
    private static final String USER = "sa";
    private static final String PASS = "";

    private static final ConnectionSource instance = new ConnectionSource();

    public static ConnectionSource instance() {
        return instance;
    }

    public Connection createConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

//    private ConnectionSource() {
//        try {
//            Class.forName("org.h2.Driver");
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException();
//        }
//    }
}
