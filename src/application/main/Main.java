package main;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{
    @Override
    public void start(Stage primaryStage)
    {
        DatabaseHelper.connectToDatabase();

        try {
            Parent root = FXMLLoader.load(this.getClass().getResource("/FXML/BaseScene.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("Library management system");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
