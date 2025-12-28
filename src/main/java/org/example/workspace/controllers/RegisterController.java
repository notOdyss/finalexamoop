package org.example.workspace.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.workspace.dao.UserDAO;
import org.example.workspace.models.User;
import org.example.workspace.utils.AlertUtil;
import org.example.workspace.utils.SceneManager;
import org.example.workspace.utils.ValidationUtil;

import java.util.Optional;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    private UserDAO userDAO;

    @FXML
    private void initialize() {
        userDAO = new UserDAO();
    }

    @FXML
    public void onRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        String usernameError = ValidationUtil.getUsernameError(username);
        if (usernameError != null) {
            AlertUtil.showError("Registration Error", usernameError);
            return;
        }

        String emailError = ValidationUtil.getEmailError(email);
        if (emailError != null) {
            AlertUtil.showError("Registration Error", emailError);
            return;
        }

        String passwordError = ValidationUtil.getPasswordError(password);
        if (passwordError != null) {
            AlertUtil.showError("Registration Error", passwordError);
            return;
        }

        if (!password.equals(confirmPassword)) {
            AlertUtil.showError("Registration Error", "Passwords do not match");
            confirmPasswordField.clear();
            return;
        }

        Optional<User> existingUser = userDAO.findByUsername(username);
        if (existingUser.isPresent()) {
            AlertUtil.showError("Registration Error", "Username already exists");
            return;
        }

        Optional<User> existingEmail = userDAO.findByEmail(email);
        if (existingEmail.isPresent()) {
            AlertUtil.showError("Registration Error", "Email already registered");
            return;
        }

        User newUser = new User(username, email, password);
        boolean success = userDAO.createUser(newUser);

        if (success) {
            AlertUtil.showSuccess("Registration Successful",
                    "Your account has been created successfully!\nPlease login to continue.");
            SceneManager.switchScene("LoginPage.fxml");
        } else {
            AlertUtil.showError("Registration Error", "Failed to create account. Please try again.");
        }
    }

    @FXML
    public void onBack() {
        SceneManager.switchScene("WelcomePage.fxml");
    }

    @FXML
    public void onLogin() {
        SceneManager.switchScene("LoginPage.fxml");
    }
}
