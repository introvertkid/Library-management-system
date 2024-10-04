package main;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        DatabaseHelper.connectToDatabase();

        try {

            Parent root = FXMLLoader.load(this.getClass().getResource("/FXML/LoginScene.fxml"));
            Scene scene = new Scene(root);

            primaryStage.setTitle("Library Management System");
            primaryStage.setScene(scene);

                //full screen - taskbar
//            primaryStage.setX(visualBounds.getMinX());
//            primaryStage.setY(visualBounds.getMinY());
//            primaryStage.setWidth(visualBounds.getWidth());
//            primaryStage.setHeight(visualBounds.getHeight());
                //full screen
//            primaryStage.setFullScreen(true);
//            primaryStage.setFullScreenExitHint("");
              primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
