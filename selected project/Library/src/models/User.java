package models;

public class User {
    private int id;
    private String name;
    private String role; // Admin or Regular User

    // Constructor with id (for when fetching from the database)
    public User(int id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    // Constructor without id (for creating a new user)
    public User(String name, String role) {
        this.name = name;
        this.role = role;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

}
