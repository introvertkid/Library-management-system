package library.controller;

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
import java.util.ResourceBundle;

public class DashboardController extends Controller {
    @FXML
    private Label labelTotalBooks;

    @FXML
    private AnchorPane content;

    @FXML
    private Label labelTotalUsers;

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
    }

    private void loadTotalBooksAndUsers() {
        try (Connection connection = DatabaseHelper.getConnection()) {
            String bookQuery = "SELECT COUNT(*) FROM documents";
            String userQuery = "SELECT COUNT(*) FROM users";

            try (PreparedStatement bookStatement = connection.prepareStatement(bookQuery);
                 ResultSet bookResult = bookStatement.executeQuery()) {
                if (bookResult.next()) {
                    int numOfBooks = bookResult.getInt(1);
                    labelTotalBooks.setText("Total books: " + numOfBooks);
                }
            }

            try (PreparedStatement userStatement = connection.prepareStatement(userQuery);
                 ResultSet userResult = userStatement.executeQuery()) {
                if (userResult.next()) {
                    int numOfUsers = userResult.getInt(1);
                    labelTotalUsers.setText("Total users: " + numOfUsers);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            labelTotalBooks.setText("Error fetching data");
        }
    }

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

    public void document() {
        loadFXMLtoAnchorPane("DocumentScene", content);
    }
}
