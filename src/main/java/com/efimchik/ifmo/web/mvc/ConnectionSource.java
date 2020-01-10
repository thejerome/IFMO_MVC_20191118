package com.efimchik.ifmo.web.mvc;

import java.sql.*;

class ConnectionSource {
    private static final String DB_URL = "jdbc:h2:mem:testdb";

    private static final String USER = "sa";
    private static final String PASS = "";

    private static final ConnectionSource instance = new ConnectionSource();

    static ConnectionSource instance(){
        return instance;
    }

    Connection createConnection() throws SQLException{
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    private ConnectionSource(){
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
