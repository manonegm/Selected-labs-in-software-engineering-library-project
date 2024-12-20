package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class TransactionDatabase {

    // borrow book function
    public static void borrowBook(User user, Book book, String dueDateStr, Connection conn) {
        String query = "INSERT INTO transactions (user_id, book_id, transaction_type, transaction_date, due_date) VALUES (?, ?, ?, ?, ?)";

        // date to string
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dueDate = null;

        try {
            // date string to java.sql.Date
            dueDate = new Date(dateFormat.parse(dueDateStr).getTime());
        } catch (Exception e) {
            System.err.println("Error parsing date: " + e.getMessage());
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, user.getId());
            stmt.setInt(2, book.getId());
            stmt.setString(3, "borrow"); // transaction type borrow
            stmt.setDate(4, new Date(System.currentTimeMillis())); 
            stmt.setDate(5, dueDate); 
            stmt.executeUpdate();
            System.out.println("Book borrowed: " + book.getName());
        } catch (SQLException e) {
            System.err.println("Error borrowing book: " + e.getMessage());
        }
    }

    // return book function
    public static void returnBook(User user, Book book, String returnDateStr, Connection conn) {
        String query = "INSERT INTO transactions (user_id, book_id, transaction_type, transaction_date, return_date) VALUES (?, ?, ?, ?, ?)";

        // date to string
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date returnDate = null;

        try {
            // date string to java.sql.Date
            returnDate = new Date(dateFormat.parse(returnDateStr).getTime());
        } catch (Exception e) {
            System.err.println("Error parsing return date: " + e.getMessage());
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, user.getId());
            stmt.setInt(2, book.getId());
            stmt.setString(3, "return"); //Transaction type "return"
            stmt.setDate(4, new Date(System.currentTimeMillis())); 
            stmt.setDate(5, returnDate); 
            stmt.executeUpdate();
            System.out.println("Book returned: " + book.getName());
        } catch (SQLException e) {
            System.err.println("Error returning book: " + e.getMessage());
        }
    }
}
