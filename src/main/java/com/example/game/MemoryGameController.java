package com.example.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryGameController {

    @FXML
    private GridPane cardGrid;

    @FXML
    private Label timerLabel, messageLabel, player1Label, player2Label, matchesFoundLabel, attemptsLabel;

    @FXML
    private Button restartButton, hintButton, submitButton, nextButton;

    private Timeline timeline;
    private int secondsElapsed;

    private Button firstSelected = null;
    private Button secondSelected = null;

    private List<Color> cardColors = new ArrayList<>();

    private final Color cardBackColor = Color.web("#444444");  // Dark gray back color

    private boolean playerOneTurn = true;
    private int player1Matches = 0;
    private int player2Matches = 0;
    private int attempts = 0;
    private int matchesFound = 0;

    @FXML
    public void initialize() {
        setupCards();
        startTimer();
        updateLabels();
        messageLabel.setText("Player 1's turn");
    }

    private void setupCards() {
        cardGrid.getChildren().clear();
        cardColors.clear();

        // 8 distinct colors (for 16 cards - pairs)
        Color[] baseColors = new Color[] {
                Color.RED, Color.GREEN, Color.BLUE, Color.ORANGE,
                Color.PURPLE, Color.CYAN, Color.YELLOW, Color.PINK
        };

        for (Color c : baseColors) {
            cardColors.add(c);
            cardColors.add(c);
        }
        Collections.shuffle(cardColors);

        int index = 0;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                Button cardButton = createCardButton(cardColors.get(index));
                cardGrid.add(cardButton, col, row);
                index++;
            }
        }
    }

    private Button createCardButton(Color color) {
        Button button = new Button();
        button.setPrefSize(160, 70);

        // Initially card back color
        button.setStyle("-fx-background-color: " + toRgbString(cardBackColor) + "; -fx-border-color: black; -fx-border-width: 1; -fx-background-radius: 6;");

        button.setUserData(color);
        button.setDisable(false);

        button.setOnAction(e -> {
            if (firstSelected != null && secondSelected != null) return; // waiting to reset
            if (button == firstSelected) return; // same card clicked
            if (button.isDisable()) return; // already matched

            // Show actual color
            button.setStyle("-fx-background-color: " + toRgbString(color) + "; -fx-border-color: black; -fx-border-width: 1; -fx-background-radius: 6;");

            if (firstSelected == null) {
                firstSelected = button;
            } else if (secondSelected == null) {
                secondSelected = button;
                attempts++;
                attemptsLabel.setText("• Attempts: " + attempts);
                checkForMatch();
            }
        });

        return button;
    }

    private void checkForMatch() {
        Color c1 = (Color) firstSelected.getUserData();
        Color c2 = (Color) secondSelected.getUserData();

        if (c1.equals(c2)) {
            matchesFound++;
            matchesFoundLabel.setText("• Matches Found: " + matchesFound);

            if (playerOneTurn) {
                player1Matches++;
                player1Label.setText("• Player 1: " + player1Matches + " matches");
                messageLabel.setText("Player 1 found a match! Play again.");
            } else {
                player2Matches++;
                player2Label.setText("• Player 2: " + player2Matches + " matches");
                messageLabel.setText("Player 2 found a match! Play again.");
            }

            firstSelected.setDisable(true);
            secondSelected.setDisable(true);
            resetSelection();

            if (matchesFound == 8) {
                messageLabel.setText("Game Over! Player 1: " + player1Matches + ", Player 2: " + player2Matches);
                stopTimer();
            }
        } else {
            messageLabel.setText("No match! Switching turn...");
            Timeline pause = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                // Flip back
                firstSelected.setStyle("-fx-background-color: " + toRgbString(cardBackColor) + "; -fx-border-color: black; -fx-border-width: 1; -fx-background-radius: 6;");
                secondSelected.setStyle("-fx-background-color: " + toRgbString(cardBackColor) + "; -fx-border-color: black; -fx-border-width: 1; -fx-background-radius: 6;");
                resetSelection();
                switchPlayer();
            }));
            pause.play();
        }
    }

    private void resetSelection() {
        firstSelected = null;
        secondSelected = null;
    }

    private void switchPlayer() {
        playerOneTurn = !playerOneTurn;
        messageLabel.setText(playerOneTurn ? "Player 1's turn" : "Player 2's turn");
    }

    private void startTimer() {
        secondsElapsed = 0;
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed++;
            timerLabel.setText(formatTime(secondsElapsed));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void stopTimer() {
        if (timeline != null) timeline.stop();
    }

    private String formatTime(int totalSeconds) {
        int min = totalSeconds / 60;
        int sec = totalSeconds % 60;
        return String.format("%02d:%02d", min, sec);
    }

    private void updateLabels() {
        player1Label.setText("• Player 1: 0 matches");
        player2Label.setText("• Player 2: 0 matches");
        matchesFoundLabel.setText("• Matches Found: 0");
        attemptsLabel.setText("• Attempts: 0");
    }

    @FXML
    private void handleRestart() {
        stopTimer();
        setupCards();
        updateLabels();
        messageLabel.setText("Game restarted! Player 1's turn");
        playerOneTurn = true;
        player1Matches = 0;
        player2Matches = 0;
        attempts = 0;
        matchesFound = 0;
        resetSelection();
        startTimer();
    }

    @FXML
    private void handleHint() {
        messageLabel.setText("Hint: Try to remember the colors and their positions!");
    }

    @FXML
    private void handleSubmit() {
        messageLabel.setText("Submit pressed - implement scoring or saving!");
    }

    @FXML
    private void handleNext() {
        messageLabel.setText("Next stage pressed - implement level progression!");
    }

    private String toRgbString(Color c) {
        int r = (int)(c.getRed() * 255);
        int g = (int)(c.getGreen() * 255);
        int b = (int)(c.getBlue() * 255);
        return "rgb(" + r + "," + g + "," + b + ")";
    }
}
