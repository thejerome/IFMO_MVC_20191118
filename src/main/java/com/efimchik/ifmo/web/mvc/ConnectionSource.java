package com.efimchik.ifmo.web.mvc;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionSource {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:mem:testdb";

    //  Database credentials
    private static final String USER = "sa";
    private static final String PASS = "";

    private static final ConnectionSource instance = new ConnectionSource();

    public static ConnectionSource getInstance(){
        return instance;
    }

    public Connection createConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL,USER,PASS);
    }

    private ConnectionSource(){
        try{
            Class.forName(JDBC_DRIVER);
        }catch (ClassNotFoundException e){
            throw  new RuntimeException(e);
        }
    }

}
