package main;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage primaryStage;
    private static double savedWidth = 1200;
    private static double savedHeight = 665;

    @Override
    public void start(Stage primaryStage) {
        DatabaseHelper.connectToDatabase();
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Library Management System");
        loadScene("/FXML/LoginScene.fxml");

        primaryStage.show();
    }

    public void loadScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(this.getClass().getResource(fxmlPath));
            if (primaryStage.getScene() != null) {
                savedWidth = primaryStage.getWidth();
                savedHeight = primaryStage.getHeight();
            }

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setWidth(savedWidth);
            primaryStage.setHeight(savedHeight);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
