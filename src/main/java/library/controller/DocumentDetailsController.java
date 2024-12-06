package library.controller;

import library.entity.Document;
import library.helper.DatabaseHelper;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DocumentDetailsController extends Controller{
    @FXML
    private TextField bookNameField;

    @FXML
    private TextField authorField;

    @FXML
    private TextField tagField;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private Text chosenFileName;

    private int documentID = DocumentRequestController.selectedDocument.getDocumentID();
    private String bookName = DocumentRequestController.selectedDocument.getDocumentName();
    private String author = DocumentRequestController.selectedDocument.getAuthors();
    private String tag = Document.getTagsByDocumentID(documentID);
    private String fileName = DocumentRequestController.selectedDocument.getFileName();

    public void initialize(URL url, ResourceBundle resourceBundle) {
        bookNameField.setText(bookName);
        authorField.setText(author);
        tagField.setText(tag);
        chosenFileName.setText(fileName);
    }

    @FXML
    private void openFile() {
        try {
            Desktop.getDesktop().open(new File("src/main/resources/Document/" + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void rejectRequest() {
        String query = "DELETE FROM documents WHERE documentID = ?";

        try (Connection connection = DatabaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, documentID);
            statement.executeUpdate();
            showAlert("Update", "Delete document successfully!");
            bookNameField.clear();
            authorField.clear();
            tagField.clear();
            chosenFileName.setText("");
            File file = new File("src/main/resources/Document/" + fileName);
            file.delete();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void acceptRequest() {
        String query = "Update documents set status = 'Available' WHERE documentID = ?";
        try (Connection connection = DatabaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, documentID);
            statement.executeUpdate();
            showAlert("Update", "Add document successfully!");
            loadFXMLtoAnchorPane("DocumentRequest", contentPane);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
