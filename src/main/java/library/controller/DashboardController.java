package library.controller;

import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import library.entity.Document;
import library.entity.User;
import library.helper.DatabaseHelper;
import library.helper.TagBookCount;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class DashboardController extends Controller {
    @FXML
    private Label labelTotalBooks;

    @FXML
    private AnchorPane content;

    @FXML
    private Label labelTotalUsers;

    @FXML
    private Label labelBorrowedBooks;

    @FXML
    private Label labelTotalBorrow;

    @FXML
    private TableView<TagBookCount> tagTable;

    @FXML
    private TableColumn<TagBookCount, String> tagColumn;

    @FXML
    private TableColumn<TagBookCount, Integer> totalBooksColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DatabaseHelper.connectToDatabase();
        tagColumn.setCellValueFactory(new PropertyValueFactory<>("tagName"));
        totalBooksColumn.setCellValueFactory(new PropertyValueFactory<>("totalBooks"));
        loadTotalBooksAndUsers();
        loadCategoryBookCounts();
        loadBorrowingBookCounts(User.getUsername());
        getTotalBorrowedAndUnreturnedBooks(User.getUsername());

        loadRecommendation();
    }

    private void loadTotalBooksAndUsers() {
        try (Connection connection = DatabaseHelper.getConnection()) {
            String bookQuery = "SELECT COUNT(*) FROM documents";
            String userQuery = "SELECT COUNT(*) FROM users";

            try (PreparedStatement bookStatement = connection.prepareStatement(bookQuery);
                 ResultSet bookResult = bookStatement.executeQuery()) {
                if (bookResult.next()) {
                    int numOfBooks = bookResult.getInt(1);
                    labelTotalBooks.setText(String.valueOf(numOfBooks));
                }
            }

            try (PreparedStatement userStatement = connection.prepareStatement(userQuery);
                 ResultSet userResult = userStatement.executeQuery()) {
                if (userResult.next()) {
                    int numOfUsers = userResult.getInt(1);
                    labelTotalUsers.setText(String.valueOf(numOfUsers));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            labelTotalBooks.setText("Error fetching data");
        }
    }

    //todo: join documents - document_tag - tags
    private void loadCategoryBookCounts() {
        String tagBookCountQuery = "SELECT c.tagName, COUNT(d.documentID) AS totalBooks " +
                "FROM tags c LEFT JOIN documents d ON c.tagID = d.tagID " +
                "GROUP BY c.tagName";

        try (Connection connection = DatabaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(tagBookCountQuery);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String tagName = resultSet.getString("tagName");
                int totalBooks = resultSet.getInt("totalBooks");
                tagTable.getItems().add(new TagBookCount(tagName, totalBooks));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadBorrowingBookCounts(String username) {
        String borrowQuery = "SELECT COUNT(*) " +
                "FROM borrowings b " +
                "INNER JOIN users u ON b.userName = u.username " +
                "WHERE u.username = ? and returned = false";

        try (Connection connection = DatabaseHelper.getConnection();
             PreparedStatement bookStatement = connection.prepareStatement(borrowQuery)) {

            bookStatement.setString(1, username);

            try (ResultSet res = bookStatement.executeQuery()) {
                if (res.next()) {
                    int borrowedCount = res.getInt(1);
                    labelBorrowedBooks.setText(String.valueOf(borrowedCount));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            labelBorrowedBooks.setText("Error fetching data");
        }
    }

    private void getTotalBorrowedAndUnreturnedBooks(String username) {
        String query = "SELECT COUNT(*) " +
                "FROM borrowings b " +
                "INNER JOIN users u ON b.userName = u.username " +
                "WHERE u.username = ?";
        int x = 0;
        try (Connection connection = DatabaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    x = resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        labelTotalBorrow.setText(String.valueOf(x));
    }

    public void document() {
        loadFXMLtoAnchorPane("DocumentScene", content);
    }

    @FXML
    private ImageView firstBookCover, secondBookCover;

    @FXML
    private Label recommend;

    @FXML
    private Label firstBookName, secondBookName, firstBookAuthor, secondBookAuthor;

    private void loadRecommendation() {
        ObservableList<Document> documentList = DocumentController.getDocumentList();
        if (documentList == null || documentList.size() < 2) {
            System.out.println("Not enough documents to load recommendations.");
            return;
        }

        // Generate two distinct random indices
        Random random = new Random();
        int firstIndex = random.nextInt(documentList.size());
        int secondIndex;
        do {
            secondIndex = random.nextInt(documentList.size());
        } while (secondIndex == firstIndex); // Ensure the indices are not the same

        // Get the documents using the random indices
        Document firstDocument = documentList.get(firstIndex);
        Document secondDocument = documentList.get(secondIndex);

        // Assign their details to the labels
        firstBookName.setText(firstDocument.getDocumentName());
        firstBookAuthor.setText(firstDocument.getAuthors());

        secondBookName.setText(secondDocument.getDocumentName());
        secondBookAuthor.setText(secondDocument.getAuthors());

        loadCoverImage(firstDocument, secondDocument);
    }

    public void loadCoverImage(Document firstDocument, Document secondDocument) {
        // Load custom font
        Font customFont = Font.loadFont(getClass().getResourceAsStream("C:/Windows/FONTS/Arial"), 14);

        // Set font for the label
        if (customFont != null) {
            recommend.setFont(customFont);
        }
        // Path to the PDF file in your project
        String pdfPath = "src/main/resources/Document/CUTE.pdf"; // Adjust the path as needed

        // Get the first page image
        Image firstBookImage = getFirstPageImage(pdfPath); //Replace pdfPath by firstDocument.getFileName()
        Image secondBookImage = getFirstPageImage(pdfPath); //Replace pdfPath by secondDocument.getFileName()

        // Set the image in the ImageView
        if (firstBookImage != null) {
            firstBookCover.setImage(firstBookImage);
            secondBookCover.setImage(secondBookImage);
        } else {
            System.out.println("Failed to load the first page of the PDF.");
        }
    }

    public static Image getFirstPageImage(String pdfPath) {
        try (PDDocument document = PDDocument.load(new File(pdfPath))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            // Render the first page (0-indexed)
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 150); // 150 DPI for quality
            return SwingFXUtils.toFXImage(bufferedImage, null);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return null if something goes wrong
        }
    }
}
