package ch.hearc.ig.orderresto.persistence.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    private static final String url = "jdbc:your_database_url";
    private static final String username = "your_username";
    private static final String password = "your_password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
