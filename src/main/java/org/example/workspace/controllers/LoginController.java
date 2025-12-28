package org.example.workspace.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.workspace.dao.UserDAO;
import org.example.workspace.models.User;
import org.example.workspace.utils.AlertUtil;
import org.example.workspace.utils.SceneManager;
import org.example.workspace.utils.Session;

import java.util.Optional;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private UserDAO userDAO;

    @FXML
    private void initialize() {
        userDAO = new UserDAO();
    }

    @FXML
    public void onLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            AlertUtil.showError("Login Error", "Please enter both username and password");
            return;
        }

        Optional<User> userOpt = userDAO.authenticate(username, password);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Session.getInstance().setCurrentUser(user);
            AlertUtil.showSuccess("Login Successful", "Welcome back, " + user.getUsername() + "!");
            SceneManager.switchScene("CalendarView.fxml");
        } else {
            AlertUtil.showError("Login Failed", "Invalid username or password");
            passwordField.clear();
        }
    }

    @FXML
    public void onBack() {
        SceneManager.switchScene("WelcomePage.fxml");
    }

    @FXML
    public void onRegister() {
        SceneManager.switchScene("RegisterPage.fxml");
    }
}
