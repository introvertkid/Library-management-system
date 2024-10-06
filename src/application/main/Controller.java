package main;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable
{
    private static Parent root;
    private static Scene scene;
    private static Stage primaryStage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    public void loadNewScene(String name, ActionEvent actionEvent)
    {
        String url = "/FXML/" + name + ".fxml";
        try {
            Object obj = null;
            try {
                obj = FXMLLoader.load(this.getClass().getResource(url));
            } catch (IOException e) {
                System.out.println("Get resource from " + url + " is null");
                e.printStackTrace();
            }

            root = (Parent) obj;
            primaryStage = loadCurrentStage(actionEvent);
            scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.out.println("Can not load FXML from: " + url);
            e.printStackTrace();
        }
    }

    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static Stage loadCurrentStage(ActionEvent mouseEvent) {
        return (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    }
}
