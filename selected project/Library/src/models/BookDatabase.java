package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import utilities.DataConnection;

public class BookDatabase {
    public static void addBook(Book book) {
        try (Connection conn = DataConnection.getInstance().getConnection();  // connection only
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO books (name, category, available) VALUES (?, ?, ?)")) {
            stmt.setString(1, book.getName());
            stmt.setString(2, book.getCategory());
            stmt.setBoolean(3, book.isAvailable());
            stmt.executeUpdate();
            System.out.println("Book created: " + book.getName());
        } catch (SQLException e) {
            System.err.println("Error adding book to database: " + e.getMessage());
        }
    }
}
