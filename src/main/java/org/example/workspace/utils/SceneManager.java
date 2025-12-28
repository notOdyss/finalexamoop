package org.example.workspace.utils;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;

import javafx.stage.Screen;
import javafx.stage.Stage;


import java.net.URL;
import java.util.function.Consumer;

public class SceneManager {

    private static Stage stage;

    public static void setStage(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void switchScene(String fxml) {
        try {
            URL url = SceneManager.class.getResource("/" + fxml);
            if (url == null) url = SceneManager.class.getResource(fxml);
            if (url == null) url = SceneManager.class.getClassLoader().getResource(fxml);
            if (url == null) url = SceneManager.class.getClassLoader().getResource("org.example.workspace/" + fxml);

            if (url == null) {
                throw new RuntimeException("FXML not found: tried several locations for " + fxml);
            }

            FXMLLoader loader = new FXMLLoader(url);
            Scene scene = new Scene(loader.load());

            URL cssUrl = SceneManager.class.getResource("/main.css");
            if (cssUrl == null) cssUrl = SceneManager.class.getClassLoader().getResource("main.css");
            if (cssUrl == null) cssUrl = SceneManager.class.getClassLoader().getResource("org.example.workspace/main.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }
            stage.setScene(scene);
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> void switchSceneWithData(String fxml, Consumer<T> controllerInitializer) {
        try {
            URL url = SceneManager.class.getResource("/" + fxml);
            if (url == null) url = SceneManager.class.getResource(fxml);
            if (url == null) url = SceneManager.class.getClassLoader().getResource(fxml);
            if (url == null) url = SceneManager.class.getClassLoader().getResource("org.example.workspace/" + fxml);

            if (url == null) {
                throw new RuntimeException("FXML not found: tried several locations for " + fxml);
            }

            FXMLLoader loader = new FXMLLoader(url);
            Scene scene = new Scene(loader.load());

            T controller = loader.getController();
            if (controller != null && controllerInitializer != null) {
                controllerInitializer.accept(controller);
            }

            URL cssUrl = SceneManager.class.getResource("/main.css");
            if (cssUrl == null) cssUrl = SceneManager.class.getClassLoader().getResource("main.css");
            if (cssUrl == null) cssUrl = SceneManager.class.getClassLoader().getResource("org.example.workspace/main.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            stage.setScene(scene);
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
