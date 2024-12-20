package factories;

import models.User;

public class UserFactory {
   //add user with id
    public static User createUser(int id, String name, String role) {
        switch (role) {
            case "admin":
                return new User(id, name, "Admin" ); 
            case "Regular User":
                return new User(id, name, "Regular User");  
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }
}
