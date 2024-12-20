package models;

import patterns.Subject;
import patterns.Observer;
import java.util.ArrayList;
import java.util.List;

public class Book implements Subject, Cloneable {
    private List<Observer> observers = new ArrayList<>();  // observer list
    private int id;
    private String name;
    private String category;
    private boolean isAvailable;

    // Constructor private to prevent direct instantiation
    private Book(BookBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.category = builder.category;
        this.isAvailable = builder.isAvailable;
    }
    @Override
    public Book clone() {
        try {
            return (Book) super.clone(); // Use deep copy of object
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public boolean isAvailable() { return isAvailable; }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
        notifyObservers("The book '" + name + "' is now " + (isAvailable ? "available" : "borrowed"));
    }

    // Implementing methods of the Subject interface
    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);  // Send message to observers
        }
    }

    // BookBuilder Class
    public static class BookBuilder {
        private int id;  // set id directly in constructor
        private String name;
        private String category;
        private boolean isAvailable;

        // Constructor to set mandatory fields
        public BookBuilder(int id) {
            this.id = id;
        }

        // Setters for optional fields
        public BookBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public BookBuilder setCategory(String category) {
            this.category = category;
            return this;
        }

        public BookBuilder setAvailable(boolean isAvailable) {
            this.isAvailable = isAvailable;
            return this;
        }

        // Build the Book object
        public Book build() {
            return new Book(this);
        }
    }
    

}