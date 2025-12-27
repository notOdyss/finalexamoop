package org.example.workspace;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.workspace.utils.SceneManager;

public class Launcher extends Application {

    @Override
    public void start(Stage stage) {
        SceneManager.setStage(stage);
        SceneManager.switchScene("WelcomePage.fxml");
        stage.setTitle("Simple Workspace");
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
