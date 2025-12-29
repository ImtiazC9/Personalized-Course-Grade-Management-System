package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {
	private Stage primaryStage;

	// Tracks the currently logged-in user so we know whose files to load
	private User currentUser;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;

		this.primaryStage.setTitle("PCGMS - Personalized Course & Grade Management System");

		// Start application at the Login screen
		switchToLoginScene();
	}

	// --- Session Management ---

	public void setCurrentUser(User user) {
		this.currentUser = user;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	// --- SCENE SWITCHING METHODS ---

	/**
	 * Loads the Login view. Called on startup and logout.
	 */
	public void switchToLoginScene() {
		// Clear user session on logout
		this.currentUser = null;
		loadScene("LoginView.fxml", null);
	}

	/**
	 * Loads the Dashboard view. Called after successful login.
	 */
	public void showDashboardView() {
		// Security check: if no user is logged in, force them to login
		if (currentUser == null) {
			switchToLoginScene();
			return;
		}
		loadScene("DashboardView.fxml", null);
	}

	/**
	 * Loads the Add Course view.
	 */
	public void switchToAddCourseScene() {
		loadScene("AddCourseView.fxml", null);
	}

	/**
	 * Loads the Course Details view for a specific course.
	 * 
	 * @param course The Course data object to be passed to the controller.
	 */
	public void switchToCourseDetailsScene(Course course) {
		loadScene("CourseDetailsView.fxml", course);
	}

	/**
	 * Generic helper method to load FXML files and inject dependencies.
	 * 
	 * @param fxmlFileName The name of the FXML file (e.g., "LoginView.fxml")
	 * @param data         Optional data (like a Course object) to pass to the
	 *                     controller.
	 */
	private void loadScene(String fxmlFileName, Object data) {
		try {
			FXMLLoader loader = new FXMLLoader();
			// Load FXML from the same package as Main.java
			URL fxmlLocation = getClass().getResource(fxmlFileName);

			if (fxmlLocation == null) {
				System.err.println(
						"CRITICAL ERROR: FXML file '" + fxmlFileName + "' not found in 'application' package.");
				return;
			}

			loader.setLocation(fxmlLocation);
			AnchorPane layout = loader.load();

			// Get the controller associated with the FXML
			Object controller = loader.getController();
			// Run time polymorphism to call the appropriate setMainApp method

			// --- Dependency Injection ---
			// connect the Main application to the Controllers so they can switch scenes

			if (controller instanceof LoginController loginController) {
				loginController.setMainApp(this);
			} else if (controller instanceof DashboardController dashboardController) {
				dashboardController.setMainApp(this);
			} else if (controller instanceof AddCourseController addCourseController) {
				addCourseController.setMainApp(this);
			} else if (controller instanceof CourseDetailsController courseDetailsController) {
				courseDetailsController.setMainApp(this);
				// If we passed a Course object (data), give it to the controller
				if (data instanceof Course course) {
					courseDetailsController.setCourse(course);
				}
			}

			// Show the scene
			Scene scene = new Scene(layout);
			String imageUrl = getClass().getResource("images.png").toExternalForm();
			Image image = new Image(imageUrl);
			primaryStage.getIcons().add(image);
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException e) {
			System.err.println("Failed to load scene: " + fxmlFileName);
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}