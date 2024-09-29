package main;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable
{
    private Parent root;
    private Scene scene;
    private Stage primaryStage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {

    }

    public Object loadFXML(String name)
    {
        String url = "/FXML/" + name + ".fxml";
        Object obj = new Object();
        try {
            obj = FXMLLoader.load(this.getClass().getResource(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static Stage loadCurrentStage(MouseEvent mouseEvent)
    {
        return (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    }
}
