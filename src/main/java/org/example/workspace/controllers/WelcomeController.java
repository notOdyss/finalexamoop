package org.example.workspace.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.workspace.utils.SceneManager;

public class WelcomeController {

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    public void onLogin() {
        SceneManager.switchScene("LoginPage.fxml");
    }

    @FXML
    public void onRegister() {
        SceneManager.switchScene("RegisterPage.fxml");
    }
}
