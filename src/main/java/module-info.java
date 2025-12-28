module org.example.workspace {
    // JavaFX modules
    requires javafx.controls;
    requires javafx.fxml;

    // Database modules
    requires java.sql;
    requires com.zaxxer.hikari;

    // Utilities
    requires org.kordamp.ikonli.javafx;
    requires jbcrypt;
    requires java.desktop;

    // Open packages for JavaFX reflection
    opens org.example.workspace to javafx.fxml;
    opens org.example.workspace.controllers to javafx.fxml;
    opens org.example.workspace.models to javafx.base;

    // Export packages
    exports org.example.workspace;
    exports org.example.workspace.controllers;
    exports org.example.workspace.models;
    exports org.example.workspace.dao;
    exports org.example.workspace.database;
    exports org.example.workspace.utils;
}
