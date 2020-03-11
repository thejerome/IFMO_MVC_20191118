package com.efimchik.ifmo.web.mvc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBDriver {
    private static final String s = "jdbc:h2:mem:testdb";
    private static final String s1 = "sa";
    private static final String s2 = "";

    public Connection createConnection() throws SQLException {
        return DriverManager.getConnection(s, s1, s2);
    }

    private DBDriver() {
        try {
            Class.forName("org.h2.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        DBDriver db = new DBDriver();
        return db.createConnection();
    }
}
