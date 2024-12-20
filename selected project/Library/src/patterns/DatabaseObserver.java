package patterns;

public class DatabaseObserver implements Observer {
    @Override
    public void update(String message) {
        System.out.println("Database change detected: " + message);
    }
}
