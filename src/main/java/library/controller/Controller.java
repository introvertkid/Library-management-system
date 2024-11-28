package library;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable
{
    @FXML
    private Button selectedButton;
    private static Parent root;
    private static Scene scene;
    private static Stage primaryStage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

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
            scene = new Scene(root);
            primaryStage = loadCurrentStage(actionEvent);
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

    public static Stage loadCurrentStage(ActionEvent actionEvent)
    {
        Object source = actionEvent.getSource();

        if(source instanceof Node)
        {
            return (Stage) ((Node) source).getScene().getWindow();
        }
        else if(source instanceof MenuItem)
        {
            return (Stage) ((MenuItem) source).getParentPopup().getOwnerWindow();
        }
        return null;
    }

    public void loadFXMLtoAnchorPane(String fxml, AnchorPane pane)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/" + fxml + ".fxml"));
            Node newContent = loader.load();
            pane.getChildren().clear();
            pane.getChildren().add(newContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setStyleForSelectedButton(Button button) {
        if (selectedButton != null) {
            selectedButton.setStyle("");
        }
        selectedButton = button;
        selectedButton.setStyle(
                "    -fx-background-color: #4c70ba;\n" +
                        "    -fx-font-family: 'Montserrat';\n" +
                        "    -fx-text-fill: white;\n" +
                        "    -fx-background-radius: 5;\n" +
                        "    -fx-font-weight: bold;\n" +
                        "    -fx-cursor: hand;");
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage=primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
