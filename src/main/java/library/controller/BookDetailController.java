package library.controller;

import library.entity.Document;
import library.entity.Comment;
import library.helper.DatabaseHelper;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BookDetailController extends Controller {

    @FXML
    private TextFlow details;

    @FXML
    private ImageView bookImage;

    @FXML
    private TextArea commentArea;

    @FXML
    private VBox commentList;

    @FXML
    private ScrollPane commentScroll;

//    private final Document selectedDocument = DocumentController.selectedDocument;
//    private final int documentID = selectedDocument.getDocumentID();
//    List<Comment> cmtList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        testBookDetail();
        commentArea.setWrapText(true);
        commentList = new VBox();
        commentList.setStyle("-fx-background-color: #FAFAFA;");
        commentScroll.setContent(commentList);
        commentScroll.setStyle("-fx-background-color: #FAFAFA; -fx-border-color: transparent;");

        commentScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);


        Platform.runLater(() -> {
            Node verticalScrollBar = commentScroll.lookup(".scroll-bar:vertical");
            if (verticalScrollBar != null) {
                verticalScrollBar.setStyle("-fx-opacity: 0; -fx-background-color: transparent;");
            }
        });
        List<Comment> cmtList = new ArrayList<>();
     cmtList.add( new Comment("Lê Sỹ Thái Sơn", "This is a great product!", "/image/UserAvatar/userAvatar.png"));
        cmtList.add( new Comment("Thạch Minh Quân", "Very helpful and easy to use.", "/image/UserAvatar/userAvatar.png"));
        cmtList.add(new Comment("Phan Đăng Nhật", "Highly recommend this to everyone.", "/image/UserAvatar/userAvatar.png"));
      cmtList.add(new Comment("Vũ Cao Phong", "Amazing! Will buy again.", "/image/UserAvatar/userAvatar.png"));
       cmtList.add(new Comment(
                "Vũ Nguyễn Trường Minh",
                "This book provided incredible insights into human behavior, relationships, and the challenges people face when dealing with complex situations. I highly recommend this to anyone interested in understanding more about psychological intricacies.",
                "/image/UserAvatar/userAvatar.png"));
        commentList.getChildren().addAll(cmtList);
    }

//    private void loadBookDetails() {
//        String query = """
//                SELECT d.documentName, d.authors, d.quantity, d.status
//                FROM documents d
//                WHERE d.documentID = ?;
//                """;
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement statement = conn.prepareStatement(query)) {
//
//            statement.setInt(1, documentID);
//
//            try (ResultSet resultSet = statement.executeQuery()) {
//                if (resultSet.next()) {
//                    String documentName = resultSet.getString("documentName");
//                    String authors = resultSet.getString("authors");
//                    String tagName = Document.getTagsByDocumentID(documentID);
//                    int quantity = resultSet.getInt("quantity");
//                    String status = resultSet.getString("status");
//
//                    displayBookDetails(documentName, authors, tagName, quantity, status);
//                } else {
//                    displayError("No details found for this book.");
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            displayError("Failed to load book details.");
//        }
//    }

    private void displayBookDetails(String name, String author, String tagName, int quantity, String status) {
        // Cover image
        String imagePath = "/image/the-swallows-673x1024.jpg";
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        bookImage.setImage(image);
        bookImage.setFitWidth(200);
        bookImage.setPreserveRatio(true);

        Text nameText = new Text("Name: " + name + "\n\n");
        nameText.setFont(new Font("Arial", 24));
        nameText.setStyle("-fx-font-weight: bold;");

        Text authorText = new Text("Author: " + author + "\n");
        authorText.setFont(new Font("Arial", 14));

        Text tagText = new Text("Tag: " + tagName + "\n");
        tagText.setFont(new Font("Arial", 14));

        Text quantityText = new Text("Quantity: " + quantity + "\n");
        quantityText.setFont(new Font("Arial", 14));

        Text statusText = new Text("Status: " + status + "\n");
        statusText.setFont(new Font("Arial", 14));

        details.getChildren().clear();
        details.getChildren().addAll(nameText, authorText, tagText, quantityText, statusText);
    }

    private void displayError(String errorMessage) {
        details.getChildren().clear();
        Text errorText = new Text(errorMessage);
        errorText.setStyle("-fx-fill: red; -fx-font-size: 14;");
        details.getChildren().add(errorText);
    }

    public void testBookDetail() {
        String title = "The Swallows";
        String author = "Author: Lisa Lutz";
        String genre = "Genre: Mystery, Thriller";
        String summary = "Summary: A story of intrigue and betrayal unfolds at a private school, "
                + "where secrets are the norm, and students are not as innocent as they appear.";

        String imagePath = "/image/the-swallows-673x1024.jpg";
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        bookImage.setImage(image);
        bookImage.setFitWidth(200);
        bookImage.setPreserveRatio(true);

        Text titleText = new Text(title + "\n\n");
        titleText.setFont(new Font("Arial", 24));
        titleText.setStyle("-fx-font-weight: bold;");

        Text authorText = new Text(author + "\n");
        authorText.setFont(new Font("Arial", 14));

        Text genreText = new Text(genre + "\n");
        genreText.setFont(new Font("Arial", 14));

        Text summaryText = new Text(summary);
        summaryText.setFont(new Font("Arial", 14));

        details.getChildren().clear();
        details.getChildren().addAll(titleText, authorText, genreText, summaryText);
    }

}


