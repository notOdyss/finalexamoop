package org.example.workspace.utils;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;

import javafx.stage.Screen;
import javafx.stage.Stage;


import java.net.URL;

public class SceneManager {

    private static Stage stage;

    public static void setStage(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void switchScene(String fxml) {
        try {
            System.out.println("LOADING FXML: " + fxml);

            // Попробуем несколько стратегий поиска ресурса
            URL url = SceneManager.class.getResource("/" + fxml); // корень resources
            if (url == null) url = SceneManager.class.getResource(fxml); // относительный пакет utils
            if (url == null) url = SceneManager.class.getClassLoader().getResource(fxml); // classloader root
            if (url == null) url = SceneManager.class.getClassLoader().getResource("org.example.workspace/" + fxml); // твоя папка org.example.workspace
            System.out.println("FXML URL = " + url);

            if (url == null) {
                throw new RuntimeException("FXML not found: tried several locations for " + fxml);
            }

            FXMLLoader loader = new FXMLLoader(url);
            Scene scene = new Scene(loader.load());

            // Аналогично ищем CSS
            URL cssUrl = SceneManager.class.getResource("/main.css");
            if (cssUrl == null) cssUrl = SceneManager.class.getClassLoader().getResource("main.css");
            if (cssUrl == null) cssUrl = SceneManager.class.getClassLoader().getResource("org.example.workspace/main.css");
            System.out.println("CSS URL = " + cssUrl);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }
            stage.setScene(scene);
            //stage.setMaximized(true);
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
