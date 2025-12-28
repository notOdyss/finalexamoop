package org.example.workspace.utils;

import org.example.workspace.models.User;

public class Session {
    private static Session instance;
    private User currentUser;

    private Session() {
    }

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void clearSession() {
        currentUser = null;
    }

    public Integer getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }

    public String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : "Guest";
    }
}
