package org.example.workspace.utils;

public class Constants {

    public static class UI {
        public static final double WINDOW_MIN_WIDTH = 800.0;
        public static final double WINDOW_MIN_HEIGHT = 600.0;
        public static final double DIALOG_WIDTH = 600.0;
        public static final double DIALOG_HEIGHT = 400.0;
        public static final int MAX_TASK_TRAILS_PER_DAY = 3;
        public static final double TASK_TRAIL_HEIGHT = 4.0;
    }

    public static class Validation {
        public static final int MIN_USERNAME_LENGTH = 3;
        public static final int MIN_PASSWORD_LENGTH = 6;
        public static final int MAX_TITLE_LENGTH = 200;
        public static final int MAX_DESCRIPTION_LENGTH = 1000;
    }

    public static class Database {
        public static final int CONNECTION_POOL_SIZE = 10;
        public static final int MIN_IDLE_CONNECTIONS = 2;
        public static final int CONNECTION_TIMEOUT_MS = 30000;
        public static final int IDLE_TIMEOUT_MS = 600000;
        public static final int MAX_LIFETIME_MS = 1800000;
    }

    public static class Messages {
        public static final String LOGIN_SUCCESS = "Welcome back!";
        public static final String REGISTER_SUCCESS = "Account created successfully!";
        public static final String TASK_CREATED = "Task created";
        public static final String TASK_UPDATED = "Task updated";
        public static final String TASK_DELETED = "Task deleted";
        public static final String ERROR_TITLE = "Error";
        public static final String SUCCESS_TITLE = "Success";
    }

    public static class Styles {
        public static final String PRIORITY_HIGH = "-fx-background-color: #ff6b6b;";
        public static final String PRIORITY_MEDIUM = "-fx-background-color: #feca57;";
        public static final String PRIORITY_LOW = "-fx-background-color: #48dbfb;";
        public static final String CURRENT_DAY = "-fx-border-color: #4834df; -fx-border-width: 2px;";
        public static final String TASK_CARD = "-fx-background-color: white; -fx-background-radius: 8px; "
                + "-fx-padding: 15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);";
    }
}
