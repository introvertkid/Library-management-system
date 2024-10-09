package main;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{
    private static double savedWidth = 1200;
    private static double savedHeight = 665;

    @Override
    public void start(Stage primaryStage) {
        DatabaseHelper.connectToDatabase();

        try {
            Parent root = FXMLLoader.load(this.getClass().getResource("/FXML/LoginScene.fxml"));
            Scene scene = new Scene(root);

            primaryStage.setTitle("Library Management System");
            primaryStage.setScene(scene);
            primaryStage.setWidth(savedWidth);
            primaryStage.setHeight(savedHeight);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
