package library.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import library.entity.Comment;
import library.entity.Document;
import library.entity.User;
import library.helper.DatabaseHelper;

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
    private Button borrowButton;

    @FXML
    private TextArea commentArea;

    @FXML
    private VBox commentList;

    @FXML
    private ScrollPane commentScroll;

    private final Document selectedDocument = DocumentController.selectedDocument;
    private final int documentID = selectedDocument.getDocumentID();
    List<Comment> cmtList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadBookDetails();
        loadComments(documentID);

        commentArea.setWrapText(true);
//        commentList = new VBox();
//        commentList.setStyle("-fx-background-color: #FAFAFA;");
//        commentScroll.setContent(commentList);
//        commentScroll.setStyle("-fx-background-color: #FAFAFA; -fx-border-color: transparent;");

        commentScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Platform.runLater(() -> {
            Node verticalScrollBar = commentScroll.lookup(".scroll-bar:vertical");
            if (verticalScrollBar != null) {
                verticalScrollBar.setStyle("-fx-opacity: 0; -fx-background-color: transparent;");
            }
        });
    }

    private void loadBookDetails() {
        String query = """
                SELECT d.documentName, d.authors, t.tagName, d.quantity, d.status
                FROM documents d
                JOIN tags t ON d.tagID = t.tagID
                WHERE d.documentID = ?;
                """;

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setInt(1, documentID);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String documentName = resultSet.getString("documentName");
                    String authors = resultSet.getString("authors");
                    String tagName = resultSet.getString("tagName");
                    int quantity = resultSet.getInt("quantity");
                    String status = resultSet.getString("status");

                    displayBookDetails(documentName, authors, tagName, quantity, status);
                } else {
                    displayError("No details found for this book.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            displayError("Failed to load book details.");
        }
    }

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


    public void loadComments(int documentID) {
        commentList = new VBox();
        commentList.setStyle("-fx-background-color: #FAFAFA;");
        commentScroll.setContent(commentList);
        commentScroll.setStyle("-fx-background-color: #FAFAFA; -fx-border-color: transparent;");
        cmtList = fetchComments(documentID);
        commentList.getChildren().clear();
        commentList.getChildren().addAll(cmtList);
    }


    private List<Comment> fetchComments(int documentID) {
        List<Comment> comments = new ArrayList<>();

        String query = """
                SELECT c.commentID, c.content, u.userFullName, u.avatar
                FROM comments c
                JOIN users u ON c.userID = u.userID
                WHERE c.documentID = ?
                ORDER BY c.commentID ASC
                """;

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, documentID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String userName = rs.getString("userFullName");
                String comment = rs.getString("content");

                String avatarPath;
                String avatarName = rs.getString("avatar");
                // If no custom avatar is set, load the default avatar
                if (avatarName == null || avatarName.isEmpty() || avatarName.equals("userAvatar.png")) {
                    avatarPath = "/image/UserAvatar/userAvatar.png";
                } else {
                    avatarPath = "/image/UserAvatar/" + avatarName;
                }

                Comment newComment = new Comment(userName, comment, avatarPath);

                comments.add(newComment);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return comments;
    }

    @FXML
    void handleAddCommentButton() {
        String newCommentContent = commentArea.getText().trim();

        if (newCommentContent.isEmpty()) {
            System.out.println("Comment cannot be empty.");
            return;
        }

        addCommentToDatabase(newCommentContent);
        commentArea.clear();
        loadComments(documentID); // Refresh comments
    }

    private void addCommentToDatabase(String content) {
        String query = "INSERT INTO comments (documentID, userID, content) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            int userID = User.getID();
            stmt.setInt(1, documentID);
            stmt.setInt(2, userID);
            stmt.setString(3, content);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBorrowButton() {
        if (DocumentController.selectedDocument.getQuantity() > 0) {
            String query = "UPDATE documents\n"
                    + "SET quantity = quantity - 1\n"
                    + "WHERE documentID = ? "
                    + "and status = 'Available'";

            try (Connection conn = DatabaseHelper.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement(query);

                stmt.setInt(1, documentID);
                if(updateBorrowingsTable()) {
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        borrowButton.setVisible(false);
                        loadBookDetails();
                        selectedDocument.setQuantity(selectedDocument.getQuantity() - 1);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean updateBorrowingsTable() {
        String query = "INSERT INTO borrowings (userName, documentName) VALUES (?, ?)";
        DatabaseHelper.connectToDatabase();
        try (Connection conn = DatabaseHelper.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, User.getUsername());
            stmt.setString(2, selectedDocument.getDocumentName());
            int newRowsAffected = stmt.executeUpdate();

            if (newRowsAffected > 0) {
                showAlert("Alert", "Borrow document successfully!");
                return true;
            } else {
                showAlert("Alert", "failed to borrow document!");
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}


