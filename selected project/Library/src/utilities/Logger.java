package utilities;

public class Logger {
    private static Logger instance;
    
    private Logger() {
        // Constructor private to create users only in this class
    }

    public static synchronized Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void log(String message) {
        System.out.println("[LOG] " + message);
    }
}