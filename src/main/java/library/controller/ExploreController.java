package library.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import library.helper.APIHelper;
import library.helper.JsonHelper;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ExploreController extends Controller {
    @FXML
    public TextField searchField;

    @FXML
    public Button findBookButton;

    @FXML
    public AnchorPane contentPane;

    @FXML
    public ImageView bookCover;

    @FXML
    private Label titleField;

    @FXML
    private Label authorsField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void handleFindBook() {
        String query = parseQuery(searchField.getText().trim());
        JsonObject jsonObject = APIHelper.fetchBookData(query);

        if (jsonObject != null) {
//            String prettyJson = JsonHelper.parsePrettyJson(jsonObject);
//            String decodedPrettyJson = JsonHelper.decodeURL(prettyJson);
            List<JsonObject> itemsList = new ArrayList<>();
            JsonArray items = jsonObject.getAsJsonArray("items");
            for (int i = 0; i < items.size(); i++) {
                itemsList.add((JsonObject) items.get(i));
                System.out.println(JsonHelper.decodeURL(JsonHelper.parsePrettyJson(itemsList.get(i))));
            }
            String thumbnailLink = getJsonPrimitive(itemsList.get(0), "thumbnail");
            String title = getJsonPrimitive(itemsList.get(0), "title");
            String authors = getJsonPrimitive(itemsList.get(0), "authors");
            System.out.println(JsonHelper.decodeURL(JsonHelper.parsePrettyJson(itemsList.get(0))));
            System.out.println("thumbnail: " + thumbnailLink);
            System.out.println("title: "+ title);
            System.out.println("authors: "+ authors);
            System.out.println("END");
            titleField.setText(title);
            authorsField.setText(authors);
            bookCover.setImage(new Image(thumbnailLink));
        } else {
            System.out.println("JSON object is null");
        }
    }

    //todo: hotfix only !!!
    public String getJsonPrimitive(JsonElement curNode, String target) {
        if (curNode instanceof JsonObject) {
            JsonObject obj = curNode.getAsJsonObject();
            for (String key : obj.keySet()) {
                if (key.equals(target)) { // && obj.get(key).isJsonPrimitive()) {
                    return obj.get(key).getAsString();
                }
                String ans = getJsonPrimitive(obj.get(key), target);
                if (ans != null) {
                    return ans;
                }
            }
        }
//        else if (curNode instanceof JsonArray) {
//            JsonArray arr = curNode.getAsJsonArray();
//            return arr.toString();
//            for (JsonElement element : arr) {
//                if(element instanceof JsonPrimitive)
//                {
//                    return (JsonPrimitive) element;
//                }
//                JsonPrimitive ans = getJsonPrimitive(element, target);
//                if (ans != null) {
//                    return ans;
//                }
//            }
//        }
//        else if (curNode instanceof JsonPrimitive)
//            return (JsonPrimitive) curNode;
        else {
            System.out.println(curNode.getClass().getName() + " is not handled");
        }
        return null;
    }

    public String parseQuery(String query) {
        return query.replace(" ", "+");
    }
}
