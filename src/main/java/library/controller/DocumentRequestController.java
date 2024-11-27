package library.controller;

import library.helper.DatabaseHelper;
import library.entity.Document;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
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

public class DocumentRequestController extends Controller {
    @FXML
    private TableView<Document> requestTable;
    @FXML
    private TableColumn<Document, Integer> documentIDColumn;
    @FXML
    private TableColumn<Document, String> documentNameColumn;
    @FXML
    private TableColumn<Document, String> categoryColumn;
    @FXML
    private TableColumn<Document, String> authorColumn;
    @FXML
    private TableColumn<Document, String> statusColumn;
    @FXML
    private ComboBox<String> statusChoice;
    @FXML
    private AnchorPane contentPane;

    private ObservableList<Document> documentList = FXCollections.observableArrayList();

    public static Document selectedDocument;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up cell value factories
        documentIDColumn.setCellValueFactory(new PropertyValueFactory<>("documentID"));
        documentNameColumn.setCellValueFactory(new PropertyValueFactory<>("documentName"));

        //  categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryID"));

        authorColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Load data from database
        loadRequestData();
    }

    private void loadRequestData() {
        documentList.clear();

        String query = "SELECT * FROM documents" +
                " WHERE status = 'Pending'";
        DatabaseHelper.connectToDatabase();
        try (Connection connection = DatabaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Document document = new Document(
                        resultSet.getInt("documentID"),
                        resultSet.getString("documentName"),
                        resultSet.getString("authors"),
                        resultSet.getString("fileName"),
                        resultSet.getString("status")
                );
                documentList.add(document);
            }
            requestTable.setItems(documentList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
   @FXML
    private void openSelectedRequest() {
        selectedDocument = requestTable.getSelectionModel().getSelectedItem();
        if (selectedDocument != null) {
            try {
                loadFXMLtoAnchorPane("RequestDetails", contentPane);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "There was an error while trying to open the request details.");
            }
        }

    }
}
