module org.example.workspace {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.kordamp.ikonli.javafx;
    requires java.desktop;

    opens org.example.workspace to javafx.fxml;
    exports org.example.workspace;
    exports org.example.workspace.controllers;
    opens org.example.workspace.controllers to javafx.fxml;
    exports org.example.workspace.db;
    opens org.example.workspace.db to javafx.fxml;
}