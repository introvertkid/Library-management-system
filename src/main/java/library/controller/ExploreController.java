package library.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import library.entity.Book;
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
    private TableView<Book> bookTable;

    @FXML
    private TableColumn<Book, Image> thumbnailColumn;

    @FXML
    private TableColumn<Book, String> authorColumn;

    @FXML
    private TableColumn<Book, String> titleColumn;

    private ObservableList<Book> bookList = FXCollections.observableArrayList();

    @FXML
    private TableColumn<Book, Image> qrCodeColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        thumbnailColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getThumbnail()));
        thumbnailColumn.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(150);
                imageView.setFitHeight(200);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
            }

            @Override
            protected void updateItem(Image image, boolean empty) {
                super.updateItem(image, empty);
                if (empty || image == null) {
                    setGraphic(null);
                } else {
                    imageView.setImage(image);
                    setGraphic(imageView);
                }
            }
        });

        //cau hinh qr
        qrCodeColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getQrCode()));
        qrCodeColumn.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(180);
                imageView.setFitHeight(180);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(Image image, boolean empty) {
                super.updateItem(image, empty);
                if (empty || image == null) {
                    setGraphic(null);
                } else {
                    imageView.setImage(image);
                    setGraphic(imageView);
                }
            }
        });

        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        authorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthors()));

        bookTable.setItems(bookList);
    }

    public void handleFindBook() throws Exception {
        String query = parseQuery(searchField.getText().trim());
        JsonObject jsonObject = APIHelper.fetchBookData(query);

        if (jsonObject != null) {
            bookList.clear();
            JsonArray items = jsonObject.getAsJsonArray("items");
            for (int i = 0; i < items.size(); i++) {
                JsonObject bookJson = items.get(i).getAsJsonObject().getAsJsonObject("volumeInfo");
                String title = getJsonPrimitive(bookJson, "title");
                String authors = getJsonPrimitive(bookJson, "authors");
                String thumbnailUrl = bookJson.has("imageLinks")
                        ? bookJson.getAsJsonObject("imageLinks").get("thumbnail").getAsString()
                        : null;
                String bookLink = bookJson.has("infoLink") ? bookJson.get("infoLink").getAsString() : "";

                // tao thumbnail
                Image thumbnail = thumbnailUrl != null ? new Image(thumbnailUrl, 150, 200, true, true) : null;

                // tao qr
                Image qrCode = QRCodeGenerator.generateQRCode(bookLink, 100, 100);
                String thumbnailLink = getJsonPrimitive(items.get(0), "thumbnail");
                String title1 = getJsonPrimitive(items.get(0), "title");
                String authors1 = getJsonPrimitive(items.get(0), "authors");
                System.out.println(JsonHelper.decodeURL(JsonHelper.parsePrettyJson((JsonObject) items.get(0))));
                System.out.println("thumbnail: " + thumbnailLink);
                System.out.println("title: " + title1);
                System.out.println("authors: " + authors1);
                System.out.println("END");

                // add book
                bookList.add(new Book(thumbnail, title, authors, qrCode));
            }
        } else {
            System.out.println("JSON object is null");
        }
    }

    //todo: hotfix only !!!
    public String getJsonPrimitive(JsonElement curNode, String target) {
        if (curNode instanceof JsonObject) {
            JsonObject obj = curNode.getAsJsonObject();
            for (String key : obj.keySet()) {
                if (key.equals(target)) {
                    //neu la mang thi duyet.
                    JsonElement element = obj.get(key);
                    if (element.isJsonArray()) {
                        JsonArray array = element.getAsJsonArray();
                        StringBuilder result = new StringBuilder();
                        for (JsonElement item : array) {
                            String itemString = item.getAsString().trim();
                            if (!itemString.isEmpty()) {
                                if (result.length() > 0) {
                                    result.append(", ");
                                }
                                result.append(itemString);
                            }
                        }
                        return result.toString();
                    } else {
                        return element.getAsString();
                    }
                }
                String ans = getJsonPrimitive(obj.get(key), target);
                if (ans != null) {
                    return ans;
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
        } else {
            System.out.println(curNode.getClass().getName() + " is not handled");
        }
        return null;
    }

    public String parseQuery(String query) {
        return query.replace(" ", "+");
    }
}
