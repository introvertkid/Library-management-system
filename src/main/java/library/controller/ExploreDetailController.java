package library.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import library.entity.Book;
import library.entity.Comment;
import library.entity.Document;
import library.entity.User;
import library.helper.DatabaseHelper;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ExploreDetailController extends Controller {

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

    private final Book selectedDocument = ExploreController.selectedBook;
    List<Comment> cmtList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadBookDetails();
//        loadComments(documentID);

        commentArea.setWrapText(true);

        commentScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);


        Platform.runLater(() -> {
            Node verticalScrollBar = commentScroll.lookup(".scroll-bar:vertical");
            if (verticalScrollBar != null) {
                verticalScrollBar.setStyle("-fx-opacity: 0; -fx-background-color: transparent;");
            }
        });

        displayBookDetails();
    }

    private void loadBookDetails() {
        System.out.println(selectedDocument.getTitle());
        System.out.println(selectedDocument.getAuthors());
//        System.out.println(selectedDocument.getThumbnail());
        System.out.println(selectedDocument.getDescription());
    }

    private void displayBookDetails() {
        // Cover image
        bookImage.setImage(selectedDocument.getThumbnail());
        bookImage.setFitWidth(200);
        bookImage.setPreserveRatio(true);

        Text nameText = new Text("Name: " + selectedDocument.getTitle() + "\n\n");
        nameText.setFont(new Font("Arial", 24));
        nameText.setStyle("-fx-font-weight: bold;");

        Text authorText = new Text("Author: " + selectedDocument.getAuthors() + "\n");
        authorText.setFont(new Font("Arial", 14));

        details.getChildren().clear();
        details.getChildren().addAll(nameText, authorText);
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

//    @FXML
//    void handleAddCommentButton() {
//        String newCommentContent = commentArea.getText().trim();
//
//        if (newCommentContent.isEmpty()) {
//            System.out.println("Comment cannot be empty.");
//            return;
//        }
//
//        addCommentToDatabase(newCommentContent);
//        commentArea.clear();
//        loadComments(documentID); // Refresh comments
//    }
//
//    private void addCommentToDatabase(String content) {
//        String query = "INSERT INTO comments (documentID, userID, content) VALUES (?, ?, ?)";
//
//        try (Connection conn = DatabaseHelper.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//
//            int userID = User.getID();
//            stmt.setInt(1, documentID);
//            stmt.setInt(2, userID);
//            stmt.setString(3, content);
//
//            stmt.executeUpdate();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

//    @FXML
//    private void handleBorrowButton() {
//        if (DocumentController.selectedDocument.getQuantity() > 0) {
//            String query = "UPDATE documents\n"
//                    + "SET quantity = quantity - 1\n"
//                    + "WHERE documentID = ? "
//                    + "and status = 'Available'";
//
//            try (Connection conn = DatabaseHelper.getConnection()) {
//                PreparedStatement stmt = conn.prepareStatement(query);
//
//                stmt.setInt(1, documentID);
//                if(updateBorrowingsTable()) {
//                    int rowsAffected = stmt.executeUpdate();
//                    if (rowsAffected > 0) {
//                        borrowButton.setVisible(false);
//                        returnButton.setVisible(true);
//                        openDocumentButton.setVisible(true);
//                        loadBookDetails();
//                        selectedDocument.setQuantity(selectedDocument.getQuantity() - 1);
//                    }
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}


