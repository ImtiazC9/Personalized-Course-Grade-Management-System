package application;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddCourseController {

    // FXML Inputs and Containers
    @FXML private TextField courseNameField;
    @FXML private TextField courseIdField;
    @FXML private VBox evaluationMethodsVBox; // Container for dynamic rows
    @FXML private Label totalWeightLabel; // Displays running total

    private Main mainApp;
    // Tracks the HBox rows dynamically added to the UI
    private List<HBox> evaluationRows = new ArrayList<>(); 

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    public void initialize() {
        // Start with one evaluation row ready for the user
        handleAddEvaluationMethod(null);
        updateTotalWeight();
    }
    
    /**
     * Creates and adds a new HBox row for defining an evaluation group structure (name, weight, best-of-N).
     * This method now builds the complex row expected by handleSaveCourse.
     */
    @FXML
    private void handleAddEvaluationMethod(ActionEvent event) {
        HBox row = new HBox(10);
	
        
        // --- Input Fields ---
        
        // 1. Evaluation Name (Index 0)
        TextField nameField = new TextField();
        nameField.setPromptText("Name (e.g. Quizzes, Midterm)");
        nameField.setPrefWidth(120);

        // 2. Weight (Index 2)
        TextField weightField = new TextField();
        weightField.setPromptText("Weight (%)");
        weightField.setPrefWidth(80);
        weightField.textProperty().addListener((obs, oldVal, newVal) -> updateTotalWeight());

        // 3. Total Items (N) (Index 4)
        TextField totalItemsField = new TextField("1");
        totalItemsField.setPrefWidth(40);
        totalItemsField.setPromptText("N");

        // 4. Items to Count (Best of M) (Index 6)
        TextField countItemsField = new TextField("1");
        countItemsField.setPrefWidth(40);
        countItemsField.setPromptText("M");
        
        // 5. Remove Button (Index 7)
        Button removeButton = new Button("X");
        removeButton.setStyle("-fx-background-color: #e53e3e; -fx-text-fill: white;");
        removeButton.setOnAction(e -> {
            evaluationMethodsVBox.getChildren().remove(row);
            evaluationRows.remove(row);
            updateTotalWeight();
        });
        
        // CRITICAL: Ensure all children (including Labels) are added in the correct order 
        // to match the indices used in handleSaveCourse.
        row.getChildren().addAll(
            nameField,                             // 0: Name Field
            new Label("Weight:"),                  // 1: Label
            weightField,                           // 2: Weight Field (Read for total weight)
            new Label("Total:"),                   // 3: Label
            totalItemsField,                       // 4: Total Items (N)
            new Label("Count Best:"),              // 5: Label
            countItemsField,                       // 6: Count Best (M)
            removeButton                           // 7: Button
        );
        evaluationMethodsVBox.getChildren().add(row);
        evaluationRows.add(row);
    }

    /**
     * Calculates the current sum of all entered weights for validation.
     */
    private double calculateTotalWeight() {
        double totalWeight = 0.0;
        for (HBox row : evaluationRows) {
            // Weight field is at index 2
            TextField weightField = (TextField) row.getChildren().get(2);
            try {
                totalWeight += Double.parseDouble(weightField.getText());
            } catch (NumberFormatException e) {
                // Ignore invalid input during typing
            }
        }
        return totalWeight;
    }
    
    /**
     * Updates the total weight label and sets the color based on validity (100%).
     */
    private void updateTotalWeight() {
        double totalWeight = calculateTotalWeight();
        totalWeightLabel.setText(String.format("Total Weight: %.1f%%", totalWeight));
        
        if (totalWeight == 100.0) totalWeightLabel.setStyle("-fx-text-fill: #38a169; -fx-font-weight: bold;");
        else totalWeightLabel.setStyle("-fx-text-fill: #e53e3e; -fx-font-weight: bold;");
    }

    /**
     * Handles the saving of the new course data to the file system.
     */
    @FXML
    private void handleSaveCourse(ActionEvent event) {
        String name = courseNameField.getText().trim();
        String id = courseIdField.getText().trim();
        
        // Validation 1: Check required fields
        if (name.isBlank() || id.isBlank()) {
            System.err.println("Validation Error: Course Name and ID are required.");
            return;
        }
        
        // Validation 2: Check total weight
        if (calculateTotalWeight() != 100.0) {
             System.err.println("Validation Error: Total weight must equal 100.0%.");
             return;
        }

        // 1. Create Course Object (using the currently logged in user)
        String username = mainApp.getCurrentUser().getUsername();
        Course newCourse = new Course(id, name, username);
        
        // 2. Add evaluation groups from UI to Object
        for (HBox row : evaluationRows) {
            // Retrieve fields based on their known position (Name=0, Weight=2, Total=4, Count=6)
            TextField nameField = (TextField) row.getChildren().get(0);
            TextField weightField = (TextField) row.getChildren().get(2);
            TextField totalItemsField = (TextField) row.getChildren().get(4);
            TextField countItemsField = (TextField) row.getChildren().get(6);

            try {
                String groupName = nameField.getText();
                double weight = Double.parseDouble(weightField.getText());
                int totalItems = Integer.parseInt(totalItemsField.getText());
                int itemsToCount = Integer.parseInt(countItemsField.getText());
             // Validation 3: Check Weightage validity
                if(weight<=0 || weight >100) {
                	 System.err.println("Validation Error: Invalid weightage for " + groupName);
                     return;
                }
                // Validation 4: Check Best-of-N rule validity
                if (totalItems <= 0 || itemsToCount <= 0 || itemsToCount > totalItems) {
                    System.err.println("Validation Error: Invalid Best-of-N settings for " + groupName);
                    return;
                }

                // Add the group to the new Course model
                newCourse.addEvaluationGroup(groupName, weight, totalItems, itemsToCount);
                
            } catch (NumberFormatException e) {
                System.err.println("Validation Error: Non-numeric data entered in a weight/count field.");
                return;
            }
        }

        // 3. Save to File System
        try {
            DataManager.saveCourse(newCourse);
            System.out.println("Course saved successfully!");
            // Switch back to dashboard after successful save
            handleBackToDashboard(null);
        } catch (IOException e) {
            System.err.println("Failed to save course to file.");
            e.printStackTrace();
        }
    }
    
    /**
     * Switches back to the main dashboard view.
     */
    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        if (mainApp != null) mainApp.showDashboardView();
    }
}