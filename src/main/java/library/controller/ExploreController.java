package library.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import library.entity.Book;
import library.helper.APIHelper;
import library.helper.QRCodeGenerator;

import java.net.URL;
import java.util.ResourceBundle;

public class ExploreController extends Controller {
    @FXML
    public TextField searchField;

    @FXML
    public Button findBookButton;

    @FXML
    public AnchorPane contentPane;

    public ProgressIndicator loadingIndicator;

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

        // Cấu hình QR Code
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

        // Cấu hình title
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        titleColumn.setCellFactory(column -> new TableCell<>() {
            private final Text text = new Text();

            {
                text.wrappingWidthProperty().bind(titleColumn.widthProperty().subtract(10));
                text.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    text.setText(item);
                    setGraphic(text);
                    setAlignment(javafx.geometry.Pos.CENTER); // Căn giữa toàn bộ TableCell
                }
            }
        });

        // Cấu hình author
        authorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthors()));
        authorColumn.setCellFactory(column -> new TableCell<>() {
            private final Text text = new Text();

            {
                text.wrappingWidthProperty().bind(authorColumn.widthProperty().subtract(10));
                text.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    text.setText(item);
                    setGraphic(text);
                    setAlignment(javafx.geometry.Pos.CENTER);
                }
            }
        });

        bookTable.setItems(bookList);

        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                findBookButton.fire();
            }
        });
        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setLayoutX(220);
        loadingIndicator.setLayoutY(10);
        loadingIndicator.setVisible(false);
        loadingIndicator.setStyle("-fx-pref-width: 30px; -fx-pref-height: 30px;");
        contentPane.getChildren().add(loadingIndicator);
    }

    public void handleFindBook() {
        String query = APIHelper.parseQuery(searchField.getText().trim());

        loadingIndicator.setVisible(true);

        // Tạo Task để load dữ liệu trên background thread
        Task<ObservableList<Book>> loadBooksTask = new Task<>() {
            @Override
            protected ObservableList<Book> call() throws Exception {
                ObservableList<Book> books = FXCollections.observableArrayList();
                JsonObject jsonObject = APIHelper.fetchBookData(query);

                if (jsonObject != null) {
                    JsonArray items = jsonObject.getAsJsonArray("items");
                    for (int i = 0; i < items.size(); i++) {
                        JsonObject bookJson = items.get(i).getAsJsonObject().getAsJsonObject("volumeInfo");
                        String title = getJsonPrimitive(bookJson, "title");
                        String authors = getJsonPrimitive(bookJson, "authors");
                        String thumbnailUrl = bookJson.has("imageLinks")
                                ? bookJson.getAsJsonObject("imageLinks").get("thumbnail").getAsString()
                                : null;
                        String bookLink = bookJson.has("infoLink") ? bookJson.get("infoLink").getAsString() : "";

                        Image thumbnail = thumbnailUrl != null ? new Image(thumbnailUrl, 150, 200, true, true) : null;
                        Image qrCode = QRCodeGenerator.generateQRCode(bookLink, 100, 100);

                        books.add(new Book(thumbnail, title, authors, qrCode));
                    }
                }

                return books;
            }
        };

        //sau khi tải xong
        loadBooksTask.setOnSucceeded(event -> {
            bookList.setAll(loadBooksTask.getValue());
            loadingIndicator.setVisible(false);
        });
        loadBooksTask.setOnFailed(event -> {
            loadingIndicator.setVisible(false);
            System.err.println("Error loading books: " + loadBooksTask.getException());
        });
        new Thread(loadBooksTask).start();
    }

    //todo: hotfix only !!!
    public static String getJsonPrimitive(JsonElement curNode, String target) {
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
//        else {
//            System.out.println(curNode.getClass().getName() + " is not handled");
//        }
        return null;
    }

    public void openBookDetail() {
        loadFXMLtoAnchorPane("BookDetail", contentPane);
    }
}
