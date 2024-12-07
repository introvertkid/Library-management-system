package library.controller.request;

import library.controller.Controller;
import library.helper.DatabaseHelper;
import library.entity.Document;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DocumentRequestController extends Controller implements IRequestController {
    @FXML
    private TableView<Document> requestTable;
    @FXML
    private TableColumn<Document, Integer> documentIDColumn;
    @FXML
    private TableColumn<Document, String> documentNameColumn;
    @FXML
    private TableColumn<Document, String> tagColumn;
    @FXML
    private TableColumn<Document, String> authorColumn;
    @FXML
    private TableColumn<Document, String> statusColumn;
    @FXML
    private AnchorPane contentPane;

    private ObservableList<Document> documentList = FXCollections.observableArrayList();

    public static Document selectedDocument;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up cell value factories
        documentIDColumn.setCellValueFactory(new PropertyValueFactory<>("documentID"));
        documentNameColumn.setCellValueFactory(new PropertyValueFactory<>("documentName"));
        tagColumn.setCellValueFactory(new PropertyValueFactory<>("tagName"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        // Load data from database
        loadRequestData();
    }

    @Override
    public void loadRequestData() {
        documentList.clear();
        String query = "SELECT d.* FROM documents d " +
                "WHERE d.status = 'Pending'";

        try (PreparedStatement statement = DatabaseHelper.getConnection().prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Document document = new Document(
                        resultSet.getInt("documentID"),
                        resultSet.getString("documentName"),
                        resultSet.getString("authors"),
                        resultSet.getString("fileName"),
                        resultSet.getString("status"),
                        Document.getTagsByDocumentID(resultSet.getInt("documentID"))
                );
                documentList.add(document);
            }
            requestTable.setItems(documentList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    @Override
    public void openSelectedRequest() {
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

    @FXML
    private void previewDocument() {
        selectedDocument = requestTable.getSelectionModel().getSelectedItem();
        try {
            String fileName = selectedDocument.getFileName();
            Desktop.getDesktop().open(new File("src/main/resources/Document/" + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
