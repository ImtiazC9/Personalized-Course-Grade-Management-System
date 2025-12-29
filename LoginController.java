package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private Main mainApp;

    // Connects this controller to the Main application
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Triggered when the "Sign In" button is clicked.
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isBlank() || password.isBlank()) {
            showError("Please enter both username and password.");
            return;
        }

        // 1. Load user data from the file system
        User user = DataManager.loadUser(username);
        
        // 2. Validate password
        if (user != null && user.getPassword().equals(password)) {
            // Login Success: Set the session and move to Dashboard
            mainApp.setCurrentUser(user);
            mainApp.showDashboardView();
        } else {
            showError("Invalid username or password.");
        }
    }

    /**
     * Triggered when the "Create Account" link is clicked.
     */
    @FXML
    private void handleRegister(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isBlank() || password.isBlank()) {
            showError("Enter a username and password to register.");
            return;
        }

        // 1. Check if user already exists
        if (DataManager.loadUser(username) != null) {
            showError("User '" + username + "' already exists.");
            return;
        }

        // 2. Create new User object
        User newUser = new User(username, password);
        
        // 3. Save to file system
        try {
            DataManager.saveUser(newUser);
            showError("Account created! You can now Sign In.", true); 
        } catch (IOException e) {
            showError("Error saving user data.");
            e.printStackTrace();
        }
    }

    // Helper to display messages to the user
    private void showError(String message) {
        showError(message, false);
    }

    private void showError(String message, boolean isSuccess) {
        errorLabel.setVisible(true);
        errorLabel.setText(message);
        if (isSuccess) {
            errorLabel.setStyle("-fx-text-fill: #38a169;"); // Green for success
        } else {
            errorLabel.setStyle("-fx-text-fill: #e53e3e;"); // Red for error
        }
    }
}