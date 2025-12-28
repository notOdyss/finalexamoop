package org.example.workspace.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.workspace.dao.UserDAO;
import org.example.workspace.models.User;
import org.example.workspace.utils.AlertUtil;
import org.example.workspace.utils.SceneManager;
import org.example.workspace.utils.Session;

import java.time.format.DateTimeFormatter;

public class ProfileController {

    @FXML private Label usernameDisplayLabel;
    @FXML private Label emailDisplayLabel;
    @FXML private Label memberSinceLabel;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    private UserDAO userDAO;
    private User currentUser;

    @FXML
    private void initialize() {
        userDAO = new UserDAO();
        currentUser = Session.getInstance().getCurrentUser();

        if (currentUser != null) {
            loadUserProfile();
        }
    }

    private void loadUserProfile() {
        usernameDisplayLabel.setText(currentUser.getUsername());
        emailDisplayLabel.setText(currentUser.getEmail());

        if (currentUser.getCreatedAt() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            memberSinceLabel.setText("Member since " + currentUser.getCreatedAt().format(formatter));
        }

        usernameField.setText(currentUser.getUsername());
        emailField.setText(currentUser.getEmail());
    }

    @FXML
    private void handleUpdateProfile() {
        String newUsername = usernameField.getText().trim();
        String newEmail = emailField.getText().trim();

        if (newUsername.isEmpty() || newEmail.isEmpty()) {
            AlertUtil.showError("Validation Error", "Username and email cannot be empty");
            return;
        }

        if (newUsername.length() < 3) {
            AlertUtil.showError("Validation Error", "Username must be at least 3 characters long");
            return;
        }

        if (!newEmail.contains("@") || !newEmail.contains(".")) {
            AlertUtil.showError("Validation Error", "Please enter a valid email address");
            return;
        }

        if (!newUsername.equals(currentUser.getUsername())) {
            if (userDAO.findByUsername(newUsername).isPresent()) {
                AlertUtil.showError("Error", "Username already taken");
                return;
            }
        }

        if (!newEmail.equals(currentUser.getEmail())) {
            if (userDAO.findByEmail(newEmail).isPresent()) {
                AlertUtil.showError("Error", "Email already registered");
                return;
            }
        }

        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);

        boolean success = userDAO.updateUser(currentUser);

        if (success) {
            Session.getInstance().setCurrentUser(currentUser);
            loadUserProfile();
            AlertUtil.showSuccess("Success", "Profile updated successfully!");
        } else {
            AlertUtil.showError("Error", "Failed to update profile");
        }
    }

    @FXML
    private void handleChangePassword() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            AlertUtil.showError("Validation Error", "Please fill in all password fields");
            return;
        }

        if (newPassword.length() < 6) {
            AlertUtil.showError("Validation Error", "New password must be at least 6 characters long");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            AlertUtil.showError("Validation Error", "New passwords do not match");
            return;
        }

        if (!userDAO.authenticate(currentUser.getUsername(), currentPassword).isPresent()) {
            AlertUtil.showError("Error", "Current password is incorrect");
            currentPasswordField.clear();
            return;
        }

        boolean success = userDAO.updatePassword(currentUser.getId(), newPassword);

        if (success) {
            AlertUtil.showSuccess("Success", "Password changed successfully!");
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
        } else {
            AlertUtil.showError("Error", "Failed to change password");
        }
    }

    @FXML
    private void handleBack() {
        SceneManager.switchScene("CalendarView.fxml");
    }

    @FXML
    private void handleBackToDashboard() {
        SceneManager.switchScene("Dashboard.fxml");
    }

    @FXML
    private void handleViewStatistics() {
        SceneManager.switchScene("Statistics.fxml");
    }

    @FXML
    private void handleLogout() {
        Session.getInstance().clearSession();
        SceneManager.switchScene("WelcomePage.fxml");
    }

    @FXML
    private void handleDeleteAccount() {
        boolean confirm = AlertUtil.showConfirmation("Delete Account",
                "Are you sure you want to delete your account?\n\nThis action cannot be undone and all your tasks will be deleted.");

        if (confirm) {
            boolean success = userDAO.deleteUser(currentUser.getId());
            if (success) {
                AlertUtil.showInfo("Account Deleted", "Your account has been deleted");
                Session.getInstance().clearSession();
                SceneManager.switchScene("WelcomePage.fxml");
            } else {
                AlertUtil.showError("Error", "Failed to delete account");
            }
        }
    }
}
