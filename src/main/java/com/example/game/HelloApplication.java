package com.example.game;
import javafx.fxml.FXML;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {

    private static Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        loadScene("start.fxml");
        stage.setTitle("Spot the Difference");
        stage.show();
    }

    public static void loadScene(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(fxmlFile));
        Scene scene = new Scene(loader.load());
        mainStage.setScene(scene);
    }
    @FXML
    private void handleStartGame() {
        try {
            HelloApplication.loadScene("game.fxml");  // Load level 1 when Start is clicked
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch();
    }
}
