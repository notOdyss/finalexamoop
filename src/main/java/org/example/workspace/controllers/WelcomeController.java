package org.example.workspace.controllers;

import org.example.workspace.utils.SceneManager;

public class WelcomeController {

    public void onLogin() {
        System.out.println("LOGIN CLICKED");
        SceneManager.switchScene("LoginPage.fxml");
    }

    public void onRegister() {
        System.out.println("REGISTER CLICKED");
        SceneManager.switchScene("RegisterPage.fxml");
    }
}

