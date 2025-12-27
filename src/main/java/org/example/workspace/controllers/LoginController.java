package org.example.workspace.controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.workspace.db.UserEntrance;
import org.example.workspace.utils.SceneManager;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    public void onLogin() {

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return UserEntrance.login(
                        usernameField.getText(),
                        passwordField.getText()
                );
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue()) {
                SceneManager.switchScene("Workspace.fxml");
            } else {
                System.out.println("Неверный логин или пароль");
            }
        });

        new Thread(task).start();
    }

    public void onBack() {
        SceneManager.switchScene("WelcomePage.fxml");
    }
}
