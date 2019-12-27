package com.efimchik.ifmo.web.mvc.source;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionSource {
    private static final String DB_URL = "jdbc:h2:mem:testdb";
    private static final ConnectionSource instance = new ConnectionSource();

    public static ConnectionSource getInstance() {
        return instance;
    }//*/

    public Connection createConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, "SA", "");
    }

    private ConnectionSource() {
        try {
            Class.forName("org.h2.Driver");
        } catch ( ClassNotFoundException e) {
            e.printStackTrace();
        }
    }//*/

}
