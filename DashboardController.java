package application;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.event.ActionEvent;
import java.util.List;

public class DashboardController {

	@FXML
	private VBox courseListVBox;
	@FXML
	private Label welcomeLabel;

	private Main mainApp;

	/**
	 * Sets the Main application reference and initiates data loading for the
	 * logged-in user.
	 */
	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;

		// Personalize the dashboard with the username immediately after injection
		if (mainApp.getCurrentUser() != null) {
			welcomeLabel.setText("Welcome Back, " + mainApp.getCurrentUser().getUsername() + "!");
			loadCourses();
		}
	}

	/**
	 * Loads the course data specific to the current user from the file system.
	 */
	private void loadCourses() {
		courseListVBox.getChildren().clear();

		// Load data from file system for the current user's username via DataManager
		List<Course> courses = DataManager.loadCoursesForUser(mainApp.getCurrentUser().getUsername());

		if (courses.isEmpty()) {
			Label emptyLabel = new Label("No courses yet. Click 'Add Course' to start!");
			emptyLabel.setStyle("-fx-text-fill: #718096; -fx-font-style: italic;");
			courseListVBox.getChildren().add(emptyLabel);
		}

		for (Course course : courses) {
			HBox courseCard = createCourseCard(course);
			courseListVBox.getChildren().add(courseCard);
		}
	}

	/**
	 * Creates a styled, clickable HBox component (the "Course Card").
	 */
	private HBox createCourseCard(Course course) {
		HBox card = new HBox();

		card.setSpacing(15);
		card.setPadding(new Insets(15));

		card.setStyle("-fx-background-color: white; " + "-fx-border-radius: 8px; " + "-fx-background-radius: 8px; "
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");

		// Calculate the current grade using the complex model logic
		double grade = course.calculateCurrentGrade();

		// Indicator color based on the current grade
		Circle indicator = new Circle(8);
		Color color;
		if (grade >= 93) {
			color = Color.web("#2ECC71"); // A+ (Deep Green)
		} else if (grade >= 90) {
			color = Color.web("#48C06C"); // A (Strong Green)
		} else if (grade >= 87) {
			color = Color.web("#6CDE8B"); // A- (Medium Green)
		} else if (grade >= 83) {
			color = Color.web("#A0E88E"); // B+ (Lime Yellow)
		} else if (grade >= 80) {
			color = Color.web("#F4D03F"); // B (Golden Yellow)
		} else if (grade >= 77) {
			color = Color.web("#F7B26E"); // B- (Soft Orange)
		} else if (grade >= 73) {
			color = Color.web("#FA9F68"); // C+ (Warm Orange)
		} else if (grade >= 70) {
			color = Color.web("#F48B57"); // C (Dark Orange)
		} else if (grade >= 67) {
			color = Color.web("#E76E4B"); // C- (Rust/Soft Red)
		} else if (grade >= 60) {
			color = Color.web("#D14739"); // D (Bright Red)
		} else {
			color = Color.web("#C0392B"); // F (Deep Red)
		}

		indicator.setFill(color);
		VBox details = new VBox();
		Label nameLabel = new Label(course.getName());
		nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
		Label idLabel = new Label("Course ID: " + course.getId());
		idLabel.setStyle("-fx-text-fill: #718096;");
		details.getChildren().addAll(nameLabel, idLabel);

		Label gradeLabel = new Label(String.format("%.2f%%", grade));
		gradeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c5282;");

		card.getChildren().addAll(indicator, details, gradeLabel);
		HBox.setHgrow(details, Priority.ALWAYS); // Pushes grade label to the right

		// Attach click handler to switch to details view
		card.setOnMouseClicked(event -> handleCourseClick(event, course));

		// Hover effects
		card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + "-fx-cursor: hand; -fx-opacity: 0.9;"));
		card.setOnMouseExited(e -> card.setStyle(card.getStyle().replace("-fx-cursor: hand; -fx-opacity: 0.9;", "")));

		return card;
	}

	/**
	 * Handles the click on a course card, triggering scene switch to Course
	 * Details.
	 */
	private void handleCourseClick(MouseEvent event, Course course) {
		if (mainApp != null) {
			mainApp.switchToCourseDetailsScene(course);
		}
	}

	// --- Button Handlers ---

	@FXML
	private void handleLogout(ActionEvent event) {
		if (mainApp != null)
			mainApp.switchToLoginScene();
	}

	@FXML
	private void handleAddCourse(ActionEvent event) {
		if (mainApp != null)
			mainApp.switchToAddCourseScene();
	}
}