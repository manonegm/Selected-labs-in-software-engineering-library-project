package factories;

import models.Book;
import patterns.Observer;
import patterns.NotificationService;  

public class BookFactory {
    public static Book createBook(int id, String name, String category, Observer observer) {
        // create a book using the builder
        Book.BookBuilder builder = new Book.BookBuilder(id)
                .setName(name);
        
        // book categorization
        switch (category.toLowerCase()) {
            case "software":
                builder.setCategory("Software Engineering")
                       .setAvailable(true);
                break;
            case "ai":
                builder.setCategory("Artificial Intelligence")
                       .setAvailable(true);
                break;
            case "management":
                builder.setCategory("Management")
                       .setAvailable(true);
                break;
            default:
                throw new IllegalArgumentException("Unknown category: " + category);
        }

        // Building a book
        Book book = builder.build();

        //add an observer when creating a book
        if (observer != null) {
            book.addObserver(observer);
        }

        return book;
    }
}
