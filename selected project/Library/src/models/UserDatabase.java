package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import utilities.DataConnection;

public class UserDatabase {

    public static void addUser(User user) {
        String query = "INSERT INTO users (name, role) VALUES (?, ?)";
        
        // DataConnection
        try (Connection conn = DataConnection.getInstance().getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getRole());
            stmt.executeUpdate();
            System.out.println("User added: " + user.getName());

        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
        }
    }
}