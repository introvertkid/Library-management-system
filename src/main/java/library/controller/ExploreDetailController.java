package library.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import library.entity.Book;
import java.net.URL;
import java.util.ResourceBundle;
import java.awt.Desktop;
import java.net.URI;

public class ExploreDetailController extends Controller {

    @FXML
    private TextFlow details;

    @FXML
    private ImageView bookImage;

    @FXML
    private ImageView qrCode;

    @FXML
    private TextFlow descriptionText;

    private final Book selectedDocument = ExploreController.selectedBook;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadBookDetails();
        displayBookDetails();
    }

    private void loadBookDetails() {
        System.out.println(selectedDocument.getTitle());
        System.out.println(selectedDocument.getAuthors());
//        System.out.println(selectedDocument.getThumbnail());
        System.out.println(selectedDocument.getCategories());
        System.out.println(selectedDocument.getAverageRating());
        System.out.println(selectedDocument.getPublishedDate());
        System.out.println(selectedDocument.getDescription());
    }

    private void displayBookDetails() {
        if (selectedDocument.getThumbnail() != null) {
            bookImage.setImage(selectedDocument.getThumbnail());
        }
        bookImage.setFitWidth(200);
        bookImage.setPreserveRatio(true);

        Text nameText = new Text("Name: " + (selectedDocument.getTitle() != null ? selectedDocument.getTitle() : "") + "\n\n");
        nameText.setFont(new Font("Arial", 24));
        nameText.setStyle("-fx-font-weight: bold;");

        Text authorText = new Text("Author: " + (selectedDocument.getAuthors() != null ? selectedDocument.getAuthors() : "") + "\n");
        authorText.setFont(new Font("Arial", 14));

        Text categories = new Text("Categories: " + (selectedDocument.getCategories() != null ? selectedDocument.getCategories() : "") + "\n");
        categories.setFont(new Font("Arial", 14));

        Text publishedDate = new Text("Published Date: " + (selectedDocument.getPublishedDate() != null ? selectedDocument.getPublishedDate() : "") + "\n");
        publishedDate.setFont(new Font("Arial", 14));

        Text averageRating = new Text("Average Rating: " + (selectedDocument.getAverageRating() != null ? selectedDocument.getAverageRating() : "") + "\n");
        averageRating.setFont(new Font("Arial", 14));

        details.getChildren().clear();
        details.getChildren().addAll(nameText, authorText, categories, publishedDate, averageRating);

        if (selectedDocument.getQrCode() != null) {
            qrCode.setImage(selectedDocument.getQrCode());
        }
        qrCode.setFitWidth(250);
        qrCode.setFitHeight(250);
        qrCode.setPreserveRatio(true);

        String descriptionContent = selectedDocument.getDescription();
        if (descriptionContent == null || descriptionContent.isEmpty()) {
            descriptionContent = "";
        }
        Text description = new Text(descriptionContent);
        description.setFont(new Font("Arial", 14));
        description.setWrappingWidth(400);

        descriptionText.getChildren().clear();
        descriptionText.getChildren().add(description);
    }

    public void openBook(ActionEvent actionEvent) {
        String bookLink = selectedDocument.getBookLink(); // Lấy link sách từ đối tượng selectedDocument

        if (bookLink != null && !bookLink.isEmpty()) {
            try {
                // Tạo URI từ liên kết
                URI uri = new URI(bookLink);

                // Kiểm tra và mở trình duyệt mặc định trên máy
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(uri);
                } else {
                    System.out.println("system does not support opening the browser");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("can't open: " + bookLink);
            }
        } else {
            System.out.println("link is null.");
        }
    }
}


