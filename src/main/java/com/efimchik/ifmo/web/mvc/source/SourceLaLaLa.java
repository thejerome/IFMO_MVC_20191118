package com.efimchik.ifmo.web.mvc.source;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.stream.Collectors;

public class SourceLaLaLa {
    private static final String DB_URL = "jdbc:h2:mem:myDb";
    private static final SourceLaLaLa instance = new SourceLaLaLa();

    public static SourceLaLaLa getInstance() {
        return instance;
    }//*/

    public Connection createConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, "SA", "");
    }
    private static String getSql(final String resourceName) {
        return new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(
                                SourceLaLaLa.class.getClassLoader().getResourceAsStream(resourceName))))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    private SourceLaLaLa() {
        try {
            Statement statement = DriverManager
                    .getConnection(DB_URL, "SA", "")
                    .createStatement();
            statement.execute(getSql("schema.sql"));
            statement.execute(getSql("data.sql"));
        }catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }//*/

}
