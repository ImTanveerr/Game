package com.example.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HelloController2 {

    // FXML elements
    @FXML private ImageView imageView2;
    @FXML private Pane pane;
    @FXML private Button submitButton;
    @FXML private Button nextButton;
    @FXML private Label messageLabel;
    @FXML private Label timerLabel;

    // List to track user selections and correct areas
    private final List<Circle> userSelections = new ArrayList<>();
    private final List<Circle> correctAreas = new ArrayList<>();

    // Timer variables
    private Timeline timeline;
    private int timeSeconds = 0;

    @FXML
    public void initialize() {
        // Initialize the correct areas where differences are located
        correctAreas.add(new Circle(465, 300, 25));  // Example coordinates
        correctAreas.add(new Circle(602, 262, 30));
        correctAreas.add(new Circle(698, 250, 20));

        // Reset message and timer
        messageLabel.setText("");
        timerLabel.setText(formatTime(0));

        // Start the stopwatch
        startStopwatch();
    }

    // Start the stopwatch to track game time
    private void startStopwatch() {
        timeSeconds = 0;
        timerLabel.setText(formatTime(timeSeconds));

        // Create a Timeline to update the timer every second
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeSeconds++;
            timerLabel.setText(formatTime(timeSeconds));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Format the time in mm:ss format
    private String formatTime(int totalSeconds) {
        int mins = totalSeconds / 60;
        int secs = totalSeconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    // Handle user clicks on the image to select differences
    @FXML
    private void handleImageClick(MouseEvent event) {
        if (event.getSource() == imageView2) {
            // Get the relative position of the mouse click on the image
            double clickX = event.getX();
            double clickY = event.getY();

            // Adjust the click position relative to the pane
            double imageViewX = imageView2.getLayoutX();
            double imageViewY = imageView2.getLayoutY();
            double adjustedX = clickX + imageViewX;
            double adjustedY = clickY + imageViewY;

            // Create a marker at the clicked position
            Circle marker = new Circle(adjustedX, adjustedY, 10, Color.RED);
            userSelections.add(marker);
            pane.getChildren().add(marker);
        }
    }

    // Handle the submit button to check the user's selections
    @FXML
    private void handleSubmit() {
        // Stop the timer when submitting
        if (timeline != null) {
            timeline.stop();
        }

        int found = 0;

        // Check if the selected areas match the correct areas
        for (Circle correct : correctAreas) {
            for (Circle selected : userSelections) {
                double dx = selected.getCenterX() - correct.getCenterX();
                double dy = selected.getCenterY() - correct.getCenterY();
                double distance = Math.sqrt(dx * dx + dy * dy);
                if (distance <= correct.getRadius()) {
                    found++;
                    break;  // No need to check further if this area is found
                }
            }
        }

        // Update the UI based on the result
        if (found == correctAreas.size()) {
            messageLabel.setText("🎉 You found all differences!");
            messageLabel.setTextFill(Color.GREEN);
        } else {
            messageLabel.setText("😕 You found " + found + " out of " + correctAreas.size() + " differences.");
            messageLabel.setTextFill(Color.ORANGE);
        }

        // Print the result to the terminal
        System.out.println("✅ Found " + found + " out of " + correctAreas.size() + " differences.");
        System.out.println("⏱️ Time taken: " + formatTime(timeSeconds));
    }

    // Handle the next button to go to the next stage of the game
    @FXML
    private void handleNext() {
        try {
            // Ensure the correct path to game3.fxml
            HelloApplication.loadScene("game3.fxml");  // Ensure the path is correct
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
