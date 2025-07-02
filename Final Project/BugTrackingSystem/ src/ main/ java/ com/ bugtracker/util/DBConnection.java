package com.bugtracker.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for managing database connections.
 * It provides a static method to get a connection to the MySQL database.
 */
public class DBConnection {

    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/BDS"; // Database URL
    private static final String USER = "sql";            // Your MySQL username
    private static final String PASSWORD = "sql";        // Your MySQL password

    /**
     * Establishes and returns a database connection.
     *
     * @return A valid Connection object if successful, null otherwise.
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Register the JDBC driver (optional for newer JDBC versions, but good practice)
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish the connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            // System.out.println("Database connection established successfully."); // For debugging
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found. Make sure MySQL Connector/J is in your classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Closes the provided database connection.
     *
     * @param connection The Connection object to close.
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                // System.out.println("Database connection closed."); // For debugging
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
