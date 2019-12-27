package com.efimchik.ifmo.web.mvc;

import java.sql.*;

final class DBConnection {

    private String URL;
    private String USER;

    private static DBConnection instance;

    static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection("jdbc:h2:mem:testdb", "sa");
        }
        return instance;
    }

    private DBConnection(String URL, String USER) {
        this.URL = URL;
        this.USER = USER;
    }

    Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, "");
    }
}