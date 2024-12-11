package library.controller;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
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
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.*;
import java.awt.image.BufferedImage;
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

public class BookDetailController extends Controller {

    @FXML
    public TextFlow descriptionTextFlow;

    @FXML
    public Text noThumbnailText;

    @FXML
    private TextFlow details;

    @FXML
    private ImageView bookImage;

    @FXML
    private Button borrowButton, returnButton, openDocumentButton;

    @FXML
    private TextArea commentArea;

    @FXML
    private VBox commentList;

    private Book book;

    @FXML
    private ScrollPane commentScroll;

    private final Document selectedDocument = DocumentController.selectedDocument;
    private final int documentID = selectedDocument.getDocumentID();
    List<Comment> cmtList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadBookDetails();
        loadComments(documentID);
        viewBorrowOrReturnButton();

        commentArea.setWrapText(true);

        commentScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        commentScroll.getStylesheets().add(getClass().getResource("/CSS/ScrollPane.css").toExternalForm());
    }

    public void setBookDetails(Book book) {
        this.book = book;

        bookImage.setImage(book.getThumbnail());
        Text titleText = new Text(book.getTitle());
        titleText.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Text authorText = new Text("\n" + book.getAuthors());
        authorText.setStyle("-fx-font-size: 16px;");

        details.getChildren().clear();
        details.getChildren().addAll(titleText, authorText);
    }

    private void loadBookDetails() {
        String query = """
                SELECT d.documentName, d.authors, d.quantity, d.status, d.description, d.fileName
                FROM documents d
                WHERE d.documentID = ?;
                """;

        try (PreparedStatement statement = DatabaseHelper.getConnection().prepareStatement(query)) {
            statement.setInt(1, documentID);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String documentName = resultSet.getString("documentName");
                    String authors = resultSet.getString("authors");
                    String tagName = Document.getTagsByDocumentID(documentID);
                    int quantity = resultSet.getInt("quantity");
                    String status = resultSet.getString("status");
                    String description = resultSet.getString("description");
                    String fileName = resultSet.getString("fileName");

                    displayBookDetails(documentName, authors, tagName,
                            quantity, status, description, fileName);
                } else {
                    if (book != null) {
                        setBookDetails(book);
                    } else {
                        showAlert("Error", "No details were found for this book.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load book details.");
        }
    }

    //todo: refactor
    private void displayBookDetails(String name, String author, String tagName,
                                    int quantity, String status, String description, String fileName) {
        // Cover image
        String imagePath = "src/main/resources/Document/" + fileName;
        Image image = getFirstPageImage(imagePath);
        if (image != null) {
            bookImage.setImage(image);
            bookImage.setFitWidth(200);
            bookImage.setPreserveRatio(true);
        }


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

        Text descriptionText = new Text(description);
        descriptionText.setFont(new Font("Arial", 14));

        descriptionTextFlow.getChildren().clear();
        descriptionTextFlow.getChildren().addAll(descriptionText);

        details.getChildren().clear();
        details.getChildren().addAll(nameText, authorText, tagText, quantityText, statusText);
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
            String query = """
                    UPDATE documents
                    SET quantity = quantity - 1
                    WHERE documentID = ?
                    and status = 'Available'""";

            try (Connection conn = DatabaseHelper.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement(query);

                stmt.setInt(1, documentID);
                if (updateBorrowingsTable()) {
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        borrowButton.setVisible(false);
                        returnButton.setVisible(true);
                        openDocumentButton.setVisible(true);
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

    @FXML
    private void handleReturnButton() {
        String queryUpdateDocuments = "UPDATE documents "
                + "SET quantity = quantity + 1 "
                + "WHERE documentID = ?";

        String queryDeleteBorrowings = "update borrowings "
                + "set returned = true "
                + "WHERE documentName = ? AND userName = ?";

        try (Connection conn = DatabaseHelper.getConnection()) {
            try (PreparedStatement stmtDelete = conn.prepareStatement(queryDeleteBorrowings)) {
                stmtDelete.setString(1, selectedDocument.getDocumentName());
                stmtDelete.setString(2, User.getUsername());

                int rowsAffected = stmtDelete.executeUpdate();

                if (rowsAffected > 0) {
                    try (PreparedStatement stmtUpdate = conn.prepareStatement(queryUpdateDocuments)) {
                        stmtUpdate.setInt(1, selectedDocument.getDocumentID());

                        int rowsUpdated = stmtUpdate.executeUpdate();
                        if (rowsUpdated > 0) {
                            borrowButton.setVisible(true);
                            returnButton.setVisible(false);
                            openDocumentButton.setVisible(false);
                            loadBookDetails();
                            selectedDocument.setQuantity(selectedDocument.getQuantity() + 1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void viewBorrowOrReturnButton() {
        String query = "Select count(*) from borrowings\n"
                + "Where userName = ? and documentName = ? and returned = false";

        try (Connection conn = DatabaseHelper.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, User.getUsername());
            stmt.setString(2, selectedDocument.getDocumentName());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                borrowButton.setVisible(count <= 0);
                returnButton.setVisible(count > 0);
                openDocumentButton.setVisible(count > 0);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void load1(String fileName) {
        try {
            Desktop.getDesktop().open(new File("src/main/resources/Document/" + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFileNameFromDatabase(String fileName) {
        String fileNamee = "";
        String query = "SELECT fileName FROM documents WHERE documentName = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, fileName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                fileNamee = rs.getString("fileName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(fileNamee);
        return fileNamee;
    }

    @FXML
    private void openDocument() {
        String documentName = selectedDocument.getDocumentName();
        if (documentName != null) {
            String fileName = getFileNameFromDatabase(documentName);

            if (fileName != null) {
                Alert loadingAlert = new Alert(Alert.AlertType.INFORMATION);
                loadingAlert.setTitle("Loading");
                loadingAlert.setHeaderText("Please wait...");
                loadingAlert.setContentText("Opening document...");
                loadingAlert.show();
                new Thread(() -> {
                    try {
                        load1(fileName);
                        Platform.runLater(() -> {
                            loadingAlert.setTitle("Success");
                            loadingAlert.setHeaderText("Document Opened");
                            loadingAlert.setContentText("The document has been opened.");
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            loadingAlert.close();
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Platform.runLater(() -> {
                            loadingAlert.setTitle("Error");
                            loadingAlert.setHeaderText("Failed to open document");
                            loadingAlert.setContentText("An error occurred while opening the document.");
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                            loadingAlert.close();
                        });
                    }
                }).start();
            } else {
                System.out.println("File not found.");
                showAlert("Error", "File not found.");
            }
        } else {
            showAlert("Error", "File not found.");
        }
    }

    public Image getFirstPageImage(String pdfPath) {
        String ex = getFileExtension(pdfPath);
        if (ex.equals("pdf")) {
            try (PDDocument document = PDDocument.load(new File(pdfPath))) {
                PDFRenderer pdfRenderer = new PDFRenderer(document);
                // Render the first page (0-indexed)
                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 150); // 150 DPI for quality
                return SwingFXUtils.toFXImage(bufferedImage, null);
            } catch (IOException e) {
                e.printStackTrace();
                return null; // Return null if something goes wrong
            }
        } else {
            noThumbnailText.setVisible(true);
        }
        return null;
    }

    private String getFileExtension(String path) {
        //excluded "."
        int i = path.lastIndexOf(".");
        return path.substring(i + 1);
    }
}


