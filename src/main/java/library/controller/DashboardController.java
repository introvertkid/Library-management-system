package library.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import library.entity.User;
import library.helper.APIHelper;
import library.helper.DatabaseHelper;
import library.helper.TagBookCount;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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

    @FXML
    private ImageView firstBookCover, secondBookCover;

    @FXML
    private Label firstBookName, secondBookName, firstBookAuthor, secondBookAuthor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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

    private void loadRecommendation() {
        List<String> suggestions = List.of("java", "javafx", "mysql", "physics", "math",
                "science", "chemistry", "biology", "history", "geography", "algorithm",
                "data structure", "NLP", "computer vision", "AI", "machine learning");

        Random random = new Random();
        int hjhj = random.nextInt(suggestions.size());
        String query = APIHelper.parseQuery(suggestions.get(hjhj)) + "&limit=2";
        System.out.println("query: " + query);

        JsonObject jsonObject = APIHelper.fetchBookData(query);
        assert jsonObject != null;
        JsonArray items = jsonObject.getAsJsonArray("items");
        System.out.println("items's size: " + items.size());

        JsonObject firstBook = (JsonObject) items.get(random.nextInt(items.size()));
        JsonObject secondBook = (JsonObject) items.get(random.nextInt(items.size()));

        // Assign their details to the labels
        firstBookName.setText(ExploreController.getJsonPrimitive(firstBook, "title"));
        firstBookAuthor.setText(ExploreController.getJsonPrimitive(firstBook, "authors"));
        firstBookCover.setImage(new Image(Objects.requireNonNull
                (ExploreController.getJsonPrimitive(firstBook, "thumbnail"))));

        secondBookName.setText(ExploreController.getJsonPrimitive(secondBook, "title"));
        secondBookAuthor.setText(ExploreController.getJsonPrimitive(secondBook, "authors"));
        secondBookCover.setImage(new Image(Objects.requireNonNull
                (ExploreController.getJsonPrimitive(secondBook, "thumbnail"))));
    }
}
