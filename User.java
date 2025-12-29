package application;

import java.io.Serializable;

/**
 * Represents a user in the Personalized Course & Grade Management System.
 * This class is Serializable to allow it to be saved to and loaded from the file system.
 */
public class User implements Serializable {
    // Unique ID required for Java Serialization
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
}