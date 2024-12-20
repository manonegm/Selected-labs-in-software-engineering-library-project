package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Book;
import factories.BookFactory;
import factories.UserFactory;
import models.TransactionDatabase;
import models.User;
import utilities.DataConnection;
import utilities.Logger;
import patterns.NotificationService;  // Notifaction Service
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private Connection connection;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // connect to Database
        try {
            connection = DataConnection.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        showLoginScreen(primaryStage);
    }
    private Book getBookByName(String bookName) {
        String query = "SELECT * FROM books WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, bookName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Book.BookBuilder(rs.getInt("id"))
                        .setName(rs.getString("name"))
                        .setCategory(rs.getString("category"))
                        .setAvailable(rs.getBoolean("available"))
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // if the book isnt found return null
    }

    
    // Login screen gui
    private void showLoginScreen(Stage primaryStage) {
        VBox vbox = new VBox(10);
        vbox.setSpacing(10);

        Label lblName = new Label("Enter your name:");
        TextField txtName = new TextField();
        txtName.setPromptText("Name");

        Button btnLogin = new Button("Login");
        Label lblMessage = new Label();

        btnLogin.setOnAction(event -> {
            String name = txtName.getText().trim();
            if (!name.isEmpty()) {
                String role = fetchUserRole(name);
                if (role != null) {
                    if (role.equalsIgnoreCase("admin")) {
                        showAdminScreen(primaryStage);
                    } else if (role.equalsIgnoreCase("Regular User")) {
                        showUserScreen(primaryStage, name);
                    } else {
                        lblMessage.setText("Role not recognized! Expected 'admin' or 'Regular User'.");
                    }
                } else {
                    lblMessage.setText("User not found! Please check your name.");
                }
            } else {
                lblMessage.setText("Please enter a name.");
            }
        });

        vbox.getChildren().addAll(lblName, txtName, btnLogin, lblMessage);

        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //Admin screen gui + code
    private void showAdminScreen(Stage primaryStage) {
        VBox vbox = new VBox(10);

        // book creation elements
        TextField txtBookName = new TextField();
        TextField txtBookCategory = new TextField();
        Button btnCreateBook = new Button("Create Book");
        ListView<String> bookListView = new ListView<>();

        // user creation elements
        TextField txtUserName = new TextField();
        TextField txtUserRole = new TextField();

        Button btnCreateUser = new Button("Create User");

        txtBookName.setPromptText("Enter book name");
        txtBookCategory.setPromptText("Enter book category");

        txtUserName.setPromptText("Enter user name");
        txtUserRole.setPromptText("Enter user role (Admin / Regular User)");

        Button btnShowBooks = new Button("Show Books");

        // Add book
        btnCreateBook.setOnAction(event -> {
            String name = txtBookName.getText();
            String category = txtBookCategory.getText();

            if (!name.isEmpty() && !category.isEmpty()) {
                // Notification via observer
                NotificationService notificationService = new NotificationService();

                // create a new book via bookbuilder
                Book newBook = BookFactory.createBook((int) (Math.random() * 1000), name, category, notificationService);

                // add book to database
                addBookToDatabase(newBook);

                // Notify user
                notificationService.update("New book created: " + newBook.getName() + " from category " + newBook.getCategory());
            }
        });

        btnCreateUser.setOnAction(event -> {
            String name = txtUserName.getText();
            String role = txtUserRole.getText();  // gets the user role

            // if the fields aren't empty
            if (!name.isEmpty() && !role.isEmpty()) {
                User newUser = UserFactory.createUser((int) (Math.random() * 1000), name, role);  // to create a user via book factory
                createUserInDatabase(newUser);  // add user to database
            }
        });

        // show book button
        btnShowBooks.setOnAction(event -> {
            List<Book> books = fetchBooksFromDatabase();
            bookListView.getItems().clear();
            for (Book book : books) {
                bookListView.getItems().add("ID: " + book.getId() + " | Name: " + book.getName() + " | Category: " + book.getCategory());
            }
        });

        vbox.getChildren().addAll(
                txtBookName, txtBookCategory, btnCreateBook,
                txtUserName, txtUserRole, btnCreateUser, btnShowBooks, bookListView
        );

        Scene scene = new Scene(vbox, 400, 500);
        primaryStage.setTitle("Admin Screen");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
 
    // show user screen
    private void showUserScreen(Stage primaryStage, String userName) {
        VBox vbox = new VBox(10);

        Button btnShowBooks = new Button("Show Books");
        Button btnBorrowBook = new Button("Borrow Book");
        Button btnReturnBook = new Button("Return Book");
        ListView<String> bookListView = new ListView<>();
        TextField txtBookName = new TextField();
        txtBookName.setPromptText("Enter Book Name");

        btnShowBooks.setOnAction(event -> {
            List<Book> books = fetchBooksFromDatabase();
            bookListView.getItems().clear();
            for (Book book : books) {
                bookListView.getItems().add("ID: " + book.getId() + " | Name: " + book.getName() + " | Category: " + book.getCategory());
            }
        });

        btnBorrowBook.setOnAction(event -> {
            String bookName = txtBookName.getText();
            if (!bookName.isEmpty()) {
                Book book = getBookByName(bookName);
                if (book != null) {
                    String dueDate = "2024-12-31";
                    User user = getUserByName(userName);
                    if (user != null) {
                        TransactionDatabase.borrowBook(user, book, dueDate, connection);
                        Logger.getInstance().log("Book borrowed: " + book.getName() + " by user: " + user.getName());
                    }
                } else {
                    Logger.getInstance().log("Book with name '" + bookName + "' not found.");
                }
            }
        });

        btnReturnBook.setOnAction(event -> {
            String bookName = txtBookName.getText();
            if (!bookName.isEmpty()) {
                Book book = getBookByName(bookName);
                if (book != null) {
                    String returnDate = "2024-12-21";
                    User user = getUserByName(userName);
                    if (user != null) {
                        TransactionDatabase.returnBook(user, book, returnDate, connection);
                        Logger.getInstance().log("Book returned: " + book.getName() + " by user: " + user.getName());
                    }
                } else {
                    Logger.getInstance().log("Book with name '" + bookName + "' not found.");
                }
            }
        });

        vbox.getChildren().addAll(btnShowBooks, bookListView, txtBookName, btnBorrowBook, btnReturnBook);

        Scene scene = new Scene(vbox, 400, 400);
        primaryStage.setTitle("User Screen");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // fetch user role from database
    private String fetchUserRole(String name) {
        String query = "SELECT role FROM users WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Logger.getInstance().log("User '" + name + "' logged in as " + rs.getString("role"));
                return rs.getString("role");
            }
        } catch (SQLException e) {
            Logger.getInstance().log("Error fetching user role for '" + name + "': " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // fetch user by name
    private User getUserByName(String name) {
        String query = "SELECT * FROM users WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("name"), rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // if user isn't found
    }

    //add book to database
    private void addBookToDatabase(Book book) {
        String query = "INSERT INTO books (name, category, available) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, book.getName());
            stmt.setString(2, book.getCategory());
            stmt.setBoolean(3, true);
            stmt.executeUpdate();
            Logger.getInstance().log("Book added to database: " + book.getName() + " (" + book.getCategory() + ")");
        } catch (SQLException e) {
            Logger.getInstance().log("Error adding book to database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void createUserInDatabase(User user) {
        String query = "INSERT INTO users (name, role) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getRole());
            stmt.executeUpdate();
            Logger.getInstance().log("User created: " + user.getName() + " with role: " + user.getRole());
        } catch (SQLException e) {
            Logger.getInstance().log("Error creating user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // fetch book from database
    private List<Book> fetchBooksFromDatabase() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                // use bookbuilder to create the book and passing the id through the constructor
                Book book = new Book.BookBuilder(rs.getInt("id"))
                        .setName(rs.getString("name"))
                        .setCategory(rs.getString("category"))
                        .setAvailable(rs.getBoolean("available"))
                        .build();
                books.add(book);
            }
            Logger.getInstance().log("Books fetched successfully. Total books: " + books.size());
        } catch (SQLException e) {
            Logger.getInstance().log("Error fetching books: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }



    @Override
    public void stop() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            Logger.getInstance().log("Database connection closed.");
        }
    }
}