package com.example.game;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public class HelloController {

    @FXML
    private ImageView imageView2;

    @FXML
    private Pane pane;

    @FXML
    private Button submitButton;

    @FXML
    private Button retryButton;

    @FXML
    private Label messageLabel;

    private final List<Circle> userSelections = new ArrayList<>();
    private final List<Circle> correctAreas = new ArrayList<>();

    @FXML
    public void initialize() {
        // Define the 3 correct difference areas (example positions)
        correctAreas.add(new Circle(465, 300, 25));  // x, y, radius
        correctAreas.add(new Circle(602, 262, 30));
        correctAreas.add(new Circle(698, 250, 20));

        messageLabel.setText("");

        // Add circles to the pane to visualize the correct areas (for debugging)
        /*for (Circle circle : correctAreas) {
            circle.setStyle("-fx-fill: rgba(255, 0, 0, 0.5); -fx-stroke: red; -fx-stroke-width: 2;");
            pane.getChildren().add(circle); // Add the circle to the Pane
        }*/
    }

    @FXML
    private void handleImageClick(MouseEvent event) {
        if (event.getSource() == imageView2) {
            // Get the relative position of the mouse click on the image
            double clickX = event.getX();
            double clickY = event.getY();

            // Adjust to the pane coordinates
            double imageViewX = imageView2.getLayoutX();
            double imageViewY = imageView2.getLayoutY();

            // Correct the selected position to match the imageView's absolute position
            double adjustedX = clickX + imageViewX;
            double adjustedY = clickY + imageViewY;

            // Create a marker for the user's selection
            Circle marker = new Circle(adjustedX, adjustedY, 10, Color.RED);
            userSelections.add(marker);
            pane.getChildren().add(marker);  // Add the selection circle to the Pane
        }
    }

    @FXML
    private void handleSubmit() {
        int found = 0;

        // Check if each selected circle is within a correct area
        for (Circle correct : correctAreas) {
            for (Circle selected : userSelections) {
                double dx = selected.getCenterX() - correct.getCenterX();
                double dy = selected.getCenterY() - correct.getCenterY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance <= correct.getRadius()) {
                    found++;
                    break;  // No need to check other selected areas for this correct one
                }
            }
        }

        // Provide feedback to the user
        if (found == correctAreas.size()) {
            messageLabel.setText("🎉 Congratulations! You found all differences!");
            messageLabel.setTextFill(Color.GREEN);
        } else {
            messageLabel.setText("😕 You found " + found + " out of " + correctAreas.size() + " differences.");
            messageLabel.setTextFill(Color.ORANGE);
        }
    }

    @FXML
    private void handleRetry() {
        // Clear all user selections
        for (Circle c : userSelections) {
            pane.getChildren().remove(c);
        }
        userSelections.clear();

        // Clear message and reset the game
        messageLabel.setText("");
    }
}
