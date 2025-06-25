package com.example.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LetterMemoryGameController {

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

    private List<Character> cardLetters = new ArrayList<>();

    private final String cardBackText = "?";

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
        cardLetters.clear();

        // 8 distinct letters (for 16 cards - pairs)
        char[] baseLetters = new char[] {'A','B','C','D','E','F','G','H'};

        for (char ch : baseLetters) {
            cardLetters.add(ch);
            cardLetters.add(ch);
        }
        Collections.shuffle(cardLetters);

        int index = 0;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                Button cardButton = createCardButton(cardLetters.get(index));
                cardGrid.add(cardButton, col, row);
                index++;
            }
        }
    }

    private Button createCardButton(char letter) {
        Button button = new Button(cardBackText);
        button.setPrefSize(160, 70);
        button.setStyle("-fx-font-size: 24px; -fx-background-color: #444444; -fx-text-fill: white; -fx-border-color: black; -fx-border-width: 1; -fx-background-radius: 6;");

        button.setUserData(letter);
        button.setDisable(false);

        button.setOnAction(e -> {
            if (firstSelected != null && secondSelected != null) return; // waiting to reset
            if (button == firstSelected) return; // same card clicked
            if (button.isDisable()) return; // already matched

            // Reveal letter
            button.setText(String.valueOf(letter));

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
        char c1 = (char) firstSelected.getUserData();
        char c2 = (char) secondSelected.getUserData();

        if (c1 == c2) {
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
                // Hide letters again
                firstSelected.setText(cardBackText);
                secondSelected.setText(cardBackText);
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
        messageLabel.setText("Hint: Try to remember the letters and their positions!");
    }

    @FXML
    private void handleSubmit() {
        messageLabel.setText("Submit pressed - implement scoring or saving!");
    }

    @FXML
    private void handleNext() {
        try {
            // Ensure the correct path to game3.fxml
            HelloApplication.loadScene("start.fxml");  // Ensure the path is correct
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
