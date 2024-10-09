package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class BaseSceneController extends Controller
{
    @FXML
    private VBox navigationBar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        for (Node node : navigationBar.getChildren()) {
            if (node instanceof Button) {
                Button but = (Button) node;
                but.setOnMouseEntered(event -> but.setCursor(Cursor.HAND));
                but.setOnMouseExited(event -> but.setCursor(Cursor.DEFAULT));
            }
        }
    }

    public void Logout(ActionEvent actionEvent) {
        loadNewScene("LoginScene", actionEvent);
    }
}
