package org.example.workspace.utils;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public static boolean isValidUsername(String username) {
        return username != null &&
               !username.trim().isEmpty() &&
               username.length() >= Constants.Validation.MIN_USERNAME_LENGTH;
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null &&
               password.length() >= Constants.Validation.MIN_PASSWORD_LENGTH;
    }

    public static boolean isValidTitle(String title) {
        return title != null &&
               !title.trim().isEmpty() &&
               title.length() <= Constants.Validation.MAX_TITLE_LENGTH;
    }

    public static boolean isValidDescription(String description) {
        return description != null &&
               !description.trim().isEmpty() &&
               description.length() <= Constants.Validation.MAX_DESCRIPTION_LENGTH;
    }

    public static boolean isValidDeadline(LocalDate deadline) {
        return deadline != null && !deadline.isBefore(LocalDate.now());
    }

    public static String getUsernameError(String username) {
        if (username == null || username.trim().isEmpty()) {
            return "Username cannot be empty";
        }
        if (username.length() < Constants.Validation.MIN_USERNAME_LENGTH) {
            return "Username must be at least " + Constants.Validation.MIN_USERNAME_LENGTH + " characters";
        }
        return null;
    }

    public static String getEmailError(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email cannot be empty";
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "Please enter a valid email address";
        }
        return null;
    }

    public static String getPasswordError(String password) {
        if (password == null || password.isEmpty()) {
            return "Password cannot be empty";
        }
        if (password.length() < Constants.Validation.MIN_PASSWORD_LENGTH) {
            return "Password must be at least " + Constants.Validation.MIN_PASSWORD_LENGTH + " characters";
        }
        return null;
    }
}
