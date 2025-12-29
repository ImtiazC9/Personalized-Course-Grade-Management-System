package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import java.io.IOException;

public class CourseDetailsController {

	// FXML Elements
	@FXML
	private Label courseTitleLabel;
	@FXML
	private Label courseIdLabel;
	@FXML
	private Label currentGradeLabel;
	@FXML
	private VBox scoresVBox;

	@FXML
	private Pane chartPane; // Replaced GridPane with Pane for visualization

	private Main mainApp;
	private Course course;

	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
	}

	/**
	 * Called by Main.java to inject the selected Course object and initialize the
	 * view.
	 */
	public void setCourse(Course course) {
		this.course = course;
		courseTitleLabel.setText(course.getName());
		courseIdLabel.setText(course.getId());

		loadEvaluationStructure();
		updateGradeDisplay();
		loadWeightDistributionChart();
	}

	/**
	 * Dynamically creates UI groups and score input fields based on the Course
	 * model.
	 */
	private void loadEvaluationStructure() {
		scoresVBox.getChildren().clear();

		for (Course.EvaluationGroup group : course.getEvaluationGroups()) {
			// 1. Group Header (e.g., "Quizzes (Best 2 of 3) - 15%")
			Label groupHeader = new Label(group.getName() + " (Best " + group.getItemsToCount() + " of "
					+ group.getTotalItems() + ") - " + group.getTotalWeight() + "%");
			groupHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c5282; -fx-font-size: 14px;");
			scoresVBox.getChildren().add(groupHeader);

			// 2. Container for individual scores within the group
			VBox groupScoresVBox = new VBox(5);

			int index = 0;
			for (Course.IndividualScore item : group.getIndividualScores()) {
				// HBox for: Item Name | Score Field | Max Field
				HBox scoreRow = createScoreRow(group, item, index);
				groupScoresVBox.getChildren().add(scoreRow);
				index++;
			}
			scoresVBox.getChildren().add(groupScoresVBox);
		}
	}

	/**
	 * Creates a single HBox row for entering score and max points for an individual
	 * item.
	 */
	private HBox createScoreRow(Course.EvaluationGroup group, Course.IndividualScore item, int index) {
		HBox row = new HBox(120);
		row.setStyle(
				"-fx-padding: 5 10 5 10; -fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-radius: 5;");
		 row.setAlignment(javafx.geometry.Pos.CENTER_LEFT); 

		Label nameLabel = new Label(item.getItemName() + ":");
		nameLabel.setPrefWidth(120);

		// Score Input Field
		TextField scoreField = new TextField();
		scoreField.setPromptText("Score");
		scoreField.setPrefWidth(60);

		// Max Points Input Field
		TextField maxField = new TextField();
		maxField.setPromptText("Max");
		maxField.setPrefWidth(60);

		Label outOfLabel = new Label("out of");

		// Set initial values if data exists
		if (item.isGraded()) {
			scoreField.setText(String.valueOf(item.getScore()));
			maxField.setText(String.valueOf(item.getMaxPoints()));
		}

		// Listener to update the model, recalculate grade, and update required score
		Runnable updateAction = () -> {
			try {
				// Parse input. If blank, assume 0 for score and 1 for max (to allow ungrading)
				double score = scoreField.getText().isBlank() ? -1 : Double.parseDouble(scoreField.getText());
				double max = maxField.getText().isBlank() ? 1 : Double.parseDouble(maxField.getText());
				group.updateScore(index, score, max);
				updateGradeDisplay();
				

			} catch (NumberFormatException e) {
			}
		};

		scoreField.textProperty().addListener((obs, oldVal, newVal) -> updateAction.run());
		maxField.textProperty().addListener((obs, oldVal, newVal) -> updateAction.run());

		row.getChildren().addAll(nameLabel, scoreField, outOfLabel, maxField);
		return row;
	}

	/**
	 * Updates the main grade display label using the Course model's calculation.
	 */
	private void updateGradeDisplay() {
		double grade = course.calculateCurrentGrade();
		currentGradeLabel.setText(String.format("%.2f%%", grade));

		// Dynamic Color coding for grade status

		if (grade >= 93)
			currentGradeLabel.setStyle("-fx-text-fill: #2ECC71; -fx-font-weight: bold; -fx-font-size: 36px;");
		else if (grade >= 90)
			currentGradeLabel.setStyle("-fx-text-fill: #48C06C; -fx-font-weight: bold; -fx-font-size: 36px;");
		else if (grade >= 87)
			currentGradeLabel.setStyle("-fx-text-fill: #6CDE8B; -fx-font-weight: bold; -fx-font-size: 36px;");
		else if (grade >= 83)
			currentGradeLabel.setStyle("-fx-text-fill: #A0E88E; -fx-font-weight: bold; -fx-font-size: 36px;");
		else if (grade >= 80)
			currentGradeLabel.setStyle("-fx-text-fill: #F4D03F; -fx-font-weight: bold; -fx-font-size: 36px;");
		else if (grade >= 77)
			currentGradeLabel.setStyle("-fx-text-fill: #F7B26E; -fx-font-weight: bold; -fx-font-size: 36px;");
		else if (grade >= 73)
			currentGradeLabel.setStyle("-fx-text-fill: #FA9F68; -fx-font-weight: bold; -fx-font-size: 36px;");
		else if (grade >= 70)
			currentGradeLabel.setStyle("-fx-text-fill: #F48B57; -fx-font-weight: bold; -fx-font-size: 36px;");
		else if (grade >= 67)
			currentGradeLabel.setStyle("-fx-text-fill: #E76E4B; -fx-font-weight: bold; -fx-font-size: 36px;");
		else if (grade >= 60)
			currentGradeLabel.setStyle("-fx-text-fill: #D14739; -fx-font-weight: bold; -fx-font-size: 36px;");
		else
			currentGradeLabel.setStyle("-fx-text-fill: #C0392B; -fx-font-weight: bold; -fx-font-size: 36px;");
	}

	/**
	 * Visualization: Loads a simple bar chart showing weight distribution.
	 */
	private void loadWeightDistributionChart() {
		VBox chartContent = new VBox(10);
		chartContent.setStyle("-fx-padding: 15;");

		double totalWeight = 100.0;
	

		for (Course.EvaluationGroup group : course.getEvaluationGroups()) {
			double weight = group.getTotalWeight();

			// Container for the bar and text
			HBox entry = new HBox(5);

			// Text Label (e.g., "Midterm 40%")
			Label label = new Label(group.getName() + " (" + (int) weight + "%)");
			label.setPrefWidth(120);
			label.setFont(Font.font("System", 12));
			entry.getChildren().add(label);

			// Bar visualization
			HBox bar = new HBox();
			bar.setStyle("-fx-background-color: #3182ce; -fx-background-radius: 3; -fx-padding: 3;");
			double barWidth = (weight / totalWeight) * 240;
			bar.setPrefWidth(barWidth);
			bar.setPrefHeight(15);

			entry.getChildren().add(bar);
			chartContent.getChildren().add(entry);
		}

		// Replace the placeholder content
		chartPane.getChildren().clear();
		chartPane.getChildren().add(chartContent);
	}

	// --- Button Handlers ---

	@FXML
	private void handleBack(ActionEvent event) {
		if (mainApp != null)
			mainApp.showDashboardView();
	}

	@FXML
	private void handleSaveChanges(ActionEvent event) {
		try {
			// Save the updated course object (with updated scores) to the file system.
			DataManager.saveCourse(course);
			System.out.println("Grades saved successfully for " + course.getName());
			if (mainApp != null)
				mainApp.showDashboardView();
		} catch (IOException e) {
			System.err.println("Failed to save grades to file.");
			e.printStackTrace();
		}
	}
}