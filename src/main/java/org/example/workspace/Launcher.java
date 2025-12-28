package org.example.workspace;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.workspace.database.DatabaseManager;
import org.example.workspace.utils.SceneManager;

public class Launcher extends Application {

    @Override
    public void start(Stage stage) {
        try {
            DatabaseManager.initializeDatabase();

            SceneManager.setStage(stage);
            SceneManager.switchScene("WelcomePage.fxml");

            stage.setTitle("Task Manager - OOP Final Project");
            stage.setMinWidth(1000);
            stage.setMinHeight(700);
            stage.setMaximized(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        DatabaseManager.closeDataSource();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
