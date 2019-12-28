package com.efimchik.ifmo.web.mvc.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConnectionSource {

    // JDBC driver name and database URL

    private static final String DB_URL = "jdbc:h2:mem:testdb";

    //  Database credentials
    private static final String USER = "sa";
    private static final String PASS = "";

    private static final ConnectionSource instance = new ConnectionSource();

    public static ConnectionSource instance() {
        return instance;
    }

    public Connection createConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    private ConnectionSource() {

        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private static String getSql(final String resourceName) {
        return new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(
                                ConnectionSource.class.getClassLoader().getResourceAsStream(resourceName))))
                .lines()
                .collect(Collectors.joining("\n"));
    }
}