package org.example.workspace.controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.workspace.db.UserEntrance;
import org.example.workspace.utils.SceneManager;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    public void onRegister() {

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return UserEntrance.register(
                        usernameField.getText(),
                        passwordField.getText()
                );
            }
        };

        task.setOnSucceeded(e ->
                SceneManager.switchScene("Workspace.fxml")
        );

        new Thread(task).start();
    }

    public void onBack() {
        SceneManager.switchScene("WelcomePage.fxml");
    }
}
