package com.efimchik.ifmo.web.mvc;

import java.sql.*;

final class DBConnection {

    public String URL;
    public String USER;

    public static DBConnection instance;

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection("jdbc:h2:mem:testdb", "sa");
        }
        return instance;
    }

    public DBConnection(String URL, String USER) {
        this.URL = URL;
        this.USER = USER;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, "");
    }
}