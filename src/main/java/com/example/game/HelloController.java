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

import java.util.ArrayList;
import java.util.List;

public class HelloController {

    @FXML private ImageView imageView2;
    @FXML private Pane pane;
    @FXML private Button submitButton;
    @FXML private Button NextButton;
    @FXML private Label messageLabel;
    @FXML private Label timerLabel;

    private final List<Circle> userSelections = new ArrayList<>();
    private final List<Circle> correctAreas = new ArrayList<>();

    private Timeline timeline;
    private int timeSeconds = 0;

    @FXML
    public void initialize() {
        correctAreas.add(new Circle(465, 300, 25));
        correctAreas.add(new Circle(602, 262, 30));
        correctAreas.add(new Circle(698, 250, 20));

        messageLabel.setText("");
        timerLabel.setText(formatTime(0));
        startStopwatch();
    }

    private void startStopwatch() {
        timeSeconds = 0;
        timerLabel.setText(formatTime(timeSeconds));

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeSeconds++;
            timerLabel.setText(formatTime(timeSeconds));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private String formatTime(int totalSeconds) {
        int mins = totalSeconds / 60;
        int secs = totalSeconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    @FXML
    private void handleImageClick(MouseEvent event) {
        if (event.getSource() == imageView2) {
            double clickX = event.getX();
            double clickY = event.getY();
            double imageViewX = imageView2.getLayoutX();
            double imageViewY = imageView2.getLayoutY();
            double adjustedX = clickX + imageViewX;
            double adjustedY = clickY + imageViewY;

            Circle marker = new Circle(adjustedX, adjustedY, 10, Color.RED);
            userSelections.add(marker);
            pane.getChildren().add(marker);
        }
    }

    @FXML
    private void handleSubmit() {
        if (timeline != null) {
            timeline.stop();
        }

        int found = 0;
        for (Circle correct : correctAreas) {
            for (Circle selected : userSelections) {
                double dx = selected.getCenterX() - correct.getCenterX();
                double dy = selected.getCenterY() - correct.getCenterY();
                double distance = Math.sqrt(dx * dx + dy * dy);
                if (distance <= correct.getRadius()) {
                    found++;
                    break;
                }
            }
        }

        // Show in UI
        if (found == correctAreas.size()) {
            messageLabel.setText("🎉 You found all differences!");
            messageLabel.setTextFill(Color.GREEN);
        } else {
            messageLabel.setText("😕 You found " + found + " out of " + correctAreas.size());
            messageLabel.setTextFill(Color.ORANGE);
        }

        // Print to terminal
        System.out.println("✅ Found " + found + " out of " + correctAreas.size() + " differences.");
        System.out.println("⏱️ Time taken: " + formatTime(timeSeconds));
    }
/*
    @FXML
    private void handleRetry() {
        for (Circle c : userSelections) {
            pane.getChildren().remove(c);
        }
        userSelections.clear();

        if (timeline != null) {
            timeline.stop();
        }

        timeSeconds = 0;
        submitButton.setDisable(false);
        messageLabel.setText("");
        timerLabel.setText(formatTime(0));
        startStopwatch();
    }*/
}
