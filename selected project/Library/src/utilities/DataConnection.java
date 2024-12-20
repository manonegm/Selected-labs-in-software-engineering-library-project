package utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataConnection {
    private static DataConnection instance;
    private Connection connection;

    private DataConnection() {
        try {
            // adds the MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // database connection settings
            String url = "jdbc:mysql://localhost:3306/library"; 
            String username = "root";  
            String password = "";

            //intiating connection
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected successfully.");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    // making a single instance in singelton
    public static DataConnection getInstance() {
        if (instance == null) {
            instance = new DataConnection();
        }
        return instance;
    }

    // retrive connection
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Connection is closed or null.");
        }
        return connection;
    }

    // close the connection when closing the program
    public void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("Connection closed.");
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}