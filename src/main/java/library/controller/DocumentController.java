package library.controller;

import javafx.scene.input.KeyCode;
import library.helper.DatabaseHelper;
import library.entity.Document;
import library.entity.User;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

public class DocumentController extends Controller {
    @FXML
    private TableView<Document> documentTable;

    @FXML
    private Button deleteBookButton;

    @FXML
    public Button findBookButton;

    @FXML
    private Button editBookButton;

    @FXML
    private Button changePage;

    @FXML
    private TextField searchField;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private TableColumn<Document, String> titleColumn;

    @FXML
    private TableColumn<Document, String> authorColumn;

    @FXML
    private Label currentPageLabel;

    @FXML
    private TableColumn<Document, String> tagColumn;

    @FXML
    private TableColumn<Document, Integer> quantityColumn;

    @FXML
    private ComboBox<String> statusChoice;

    private Document selectedBook;

    private final ObservableList<Document> documentList = FXCollections.observableArrayList();
    private int currentPage = 0;
    private static final int ROWS_PER_PAGE = 18;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDocumentData();

        currentPage = 0;
        updateTable(currentPage);
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("documentName"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));
        tagColumn.setCellValueFactory(new PropertyValueFactory<>("tagName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        contentPane.setOnMouseClicked(event -> {
            searchField.getParent().requestFocus();
        });
        changePage.setOnAction(event -> handlePageChange());

        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                findBookButton.fire();
            }
        });

        if (User.getRole().equals("Admin")) {
            deleteBookButton.setVisible(true);
            editBookButton.setVisible(true);
        }
        statusChoice.setItems(FXCollections.observableArrayList(" All", " Borrowed", " Available"));
        statusChoice.setValue(" All");
    }

    private void loadDocumentData() {
        if(!documentList.isEmpty()) {
            documentList.clear();
        }

        try (Connection connection = DatabaseHelper.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT d.documentID, d.documentName, d.authors, d.quantity " +
                     "FROM documents d Where status = 'Available' ")) {

            while (resultSet.next()) {
                int documentID = resultSet.getInt("documentID");
                String documentName = resultSet.getString("documentName");
                String tagName = Document.getTagsByDocumentID(documentID);
                String authors = resultSet.getString("authors");
                int quantity = resultSet.getInt("quantity");

                Document document = new Document(documentName, authors, tagName, quantity, documentID);
                documentList.add(document);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int totalPages = (int) Math.ceil((double) documentList.size() / ROWS_PER_PAGE);
        if (currentPage > totalPages) {
            System.out.println(currentPage);
            currentPage = totalPages;
        }
        updateTable(currentPage);
        documentTable.setItems(documentList);
        setupPagination();
        updatePage();
    }

    private void updatePage() {
        int totalPages = (int) Math.ceil((double) documentList.size() / ROWS_PER_PAGE);
        currentPageLabel.setText(currentPage + 1 + "/" + totalPages);
        int fromIndex = currentPage * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, documentList.size());
        ObservableList<Document> paginatedList = FXCollections.observableArrayList(documentList.subList(fromIndex, toIndex));
        documentTable.setItems(paginatedList);
    }

    @FXML
    private void handleNextPage() {
        int totalPages = (int) Math.ceil((double) documentList.size() / ROWS_PER_PAGE);
        if (currentPage < totalPages - 1) {
            currentPage++;
            updatePage();
        }
    }

    @FXML
    private void handlePrevPage() {
        if (currentPage > 0) {
            currentPage--;
            updatePage();
        }
    }

    private void updateTable(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, documentList.size());
        ObservableList<Document> paginatedList;

        if (fromIndex >= documentList.size()) {
            paginatedList = FXCollections.observableArrayList(documentList.subList(1, toIndex));
        } else {
            paginatedList = FXCollections.observableArrayList(documentList.subList(fromIndex, toIndex));
        }
        documentTable.setItems(paginatedList);
    }

    private void setupPagination() {
        String countQuery = "SELECT COUNT(*) AS total FROM documents";
        try (Connection connection = DatabaseHelper.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(countQuery)) {

            if (resultSet.next()) {
                int totalRecords = resultSet.getInt("total");
                if (totalRecords > 0) {
                    updateTable(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFindBook() {
        String searchQuery = searchField.getText().trim();
        if (searchQuery.isEmpty()) {
            showAlert("Input Error", "Please enter a search term.");
            documentTable.setItems(documentList);
            updatePage();
        } else {
            FilteredList<Document> filteredList = new FilteredList<>(documentList, book ->
                    book.getDocumentName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            book.getAuthors().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            book.getTagName().toLowerCase().contains(searchQuery.toLowerCase())
            );
            documentTable.setItems(filteredList);

            if (filteredList.isEmpty()) {
                showAlert("No Results", "No books found matching the search term.");
            }
        }
    }

    //todo: change file of document
    @FXML
    private void handleEditBook() {
        Document selectedBook = documentTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("No Selection", "Please select a book to edit.");
            return;
        }
        javafx.scene.control.Dialog<Document> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Edit Book");
        ButtonType editButtonType = new ButtonType("Edit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(editButtonType, ButtonType.CANCEL);
        TextField titleField = new TextField(selectedBook.getDocumentName());
        TextField authorField = new TextField(selectedBook.getAuthors());
        TextField quantityField = new TextField(String.valueOf(selectedBook.getQuantity()));
        TextField tagField = new TextField(String.valueOf(selectedBook.getTagName()));

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(
                new javafx.scene.control.Label("Title:"), titleField,
                new javafx.scene.control.Label("Author:"), authorField,
                new javafx.scene.control.Label("Quantity:"), quantityField,
                new javafx.scene.control.Label("Tags (comma-separated):"), tagField
        );
        dialog.getDialogPane().setContent(vbox);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == editButtonType) {
                if (titleField.getText().isEmpty() || authorField.getText().isEmpty() ||
                        quantityField.getText().isEmpty() || tagField.getText().isEmpty()) {
                    showAlert("Input Error", "Please fill in all fields.");
                    return null;
                }
                return new Document(
                        selectedBook.getDocumentID(),
                        titleField.getText(),
                        authorField.getText(),
                        Integer.parseInt(quantityField.getText()),
                        tagField.getText()
                );
            }
            return null;
        });

        Optional<Document> result = dialog.showAndWait();
        result.ifPresent(this::updateBookInDatabase);
    }


    private void updateBookInDatabase(Document updatedBook) {
        String updateDocumentQuery = "UPDATE documents SET documentName = ?, authors = ?, quantity = ? WHERE documentID = ?";
        String getTagIdQuery = "SELECT tagID FROM tags WHERE tagName = ?";
        String insertTagQuery = "INSERT INTO tags (tagName) VALUES (?)";
        String insertDocumentTagQuery = "INSERT INTO document_tag (documentID, tagID) VALUES (?, ?)";
        String deleteDocumentTagsQuery = "DELETE FROM document_tag WHERE documentID = ?";

        try (Connection conn = DatabaseHelper.getConnection()) {
            // Update document information
            try (PreparedStatement pstmt = conn.prepareStatement(updateDocumentQuery)) {
                pstmt.setString(1, updatedBook.getDocumentName());
                pstmt.setString(2, updatedBook.getAuthors());
                pstmt.setInt(3, updatedBook.getQuantity());
                pstmt.setInt(4, updatedBook.getDocumentID());
                pstmt.executeUpdate();
            }

            // Remove existing tags for the document
            try (PreparedStatement pstmtDelete = conn.prepareStatement(deleteDocumentTagsQuery)) {
                pstmtDelete.setInt(1, updatedBook.getDocumentID());
                pstmtDelete.executeUpdate();
            }

            // Add new tags and update document_tag
            String[] tags = updatedBook.getTagName().split(",");
            for (String tag : tags) {
                tag = tag.trim();
                int tagID = -1;

                // Check if tag exists
                try (PreparedStatement pstmtGetTag = conn.prepareStatement(getTagIdQuery)) {
                    pstmtGetTag.setString(1, tag);
                    ResultSet rs = pstmtGetTag.executeQuery();
                    if (rs.next()) {
                        tagID = rs.getInt("tagID");
                    } else {
                        // Insert new tag
                        try (PreparedStatement pstmtInsertTag = conn.prepareStatement(insertTagQuery, Statement.RETURN_GENERATED_KEYS)) {
                            pstmtInsertTag.setString(1, tag);
                            pstmtInsertTag.executeUpdate();
                            ResultSet rsInsert = pstmtInsertTag.getGeneratedKeys();
                            if (rsInsert.next()) {
                                tagID = rsInsert.getInt(1);
                            }
                        }
                    }
                }

                // Add to document_tag
                if (tagID != -1) {
                    try (PreparedStatement pstmtInsertDocTag = conn.prepareStatement(insertDocumentTagQuery)) {
                        pstmtInsertDocTag.setInt(1, updatedBook.getDocumentID());
                        pstmtInsertDocTag.setInt(2, tagID);
                        pstmtInsertDocTag.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        loadDocumentData();
    }

    @FXML
    private void handleDeleteBook() {
        Document selectedBook = documentTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("No Selection", "Please select a book to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Delete this book?");
        alert.setContentText(selectedBook.getDocumentName());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteBookFromDatabase(selectedBook.getDocumentID());
        }
    }

    private void deleteBookFromDatabase(int documentID) {
        String deleteDocumentTagsQuery = "DELETE FROM document_tag WHERE documentID = ?";
        String deleteDocumentOwnersQuery = "DELETE FROM documentOwner WHERE documentID = ?";
        String deleteCommentsQuery = "DELETE FROM comments WHERE documentID = ?";
        String deleteDocumentQuery = "DELETE FROM documents WHERE documentID = ?";

        try (Connection conn = DatabaseHelper.getConnection()) {
            conn.setAutoCommit(false); // all query will be executed as one

            // Xóa các liên kết trong bảng document_tag
            try (PreparedStatement pstmtTags = conn.prepareStatement(deleteDocumentTagsQuery)) {
                pstmtTags.setInt(1, documentID);
                pstmtTags.executeUpdate();
            }

            // Xóa các liên kết trong bảng documentOwner
            try (PreparedStatement pstmtOwners = conn.prepareStatement(deleteDocumentOwnersQuery)) {
                pstmtOwners.setInt(1, documentID);
                pstmtOwners.executeUpdate();
            }

            // Xóa các bình luận trong bảng comments
            try (PreparedStatement pstmtComments = conn.prepareStatement(deleteCommentsQuery)) {
                pstmtComments.setInt(1, documentID);
                pstmtComments.executeUpdate();
            }

            // Xóa tài liệu trong bảng documents
            try (PreparedStatement pstmtDocument = conn.prepareStatement(deleteDocumentQuery)) {
                pstmtDocument.setInt(1, documentID);
                pstmtDocument.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                // Rollback nếu có lỗi
                DatabaseHelper.getConnection().rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }

        // Cập nhật giao diện
        loadDocumentData();
    }

    @FXML
    public void handlePageChange() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change Page");
        dialog.setHeaderText("Go to Page");
        dialog.setContentText("Enter the page number:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(pageNumber -> {
            try {
                int page = Integer.parseInt(pageNumber) - 1;
                int totalPages = (int) Math.ceil((double) documentList.size() / ROWS_PER_PAGE);

                if (page >= 0 && page < totalPages) {
                    currentPage = page;
                    updatePage();
                } else {
                    showAlert("Invalid Page", "The page number is out of range. Please enter a valid page number.");
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid number.");
            }
        });
    }

    //this approach is using pdf.js
//    public void load2()
//    {
//        WebView webView = new WebView();
//        WebEngine webEngine = webView.getEngine();
//
//        try {
//            String url = getClass().getResource("/pdfjs/web/viewer.html").toURI().toString();
//            webEngine.setUserStyleSheetLocation(
//                    getClass().getResource("/pdfjs/web/viewer.css").toURI().toString());
//            webEngine.setJavaScriptEnabled(true);
//            webEngine.load(url);
//
//            webEngine.getLoadWorker().stateProperty()
//                    .addListener((observableValue, oldValue, newValue) -> {
//                        if (Worker.State.SUCCEEDED == newValue) {
//                            InputStream stream = null;
//                            try {
//                                InputStream inputStream = getClass().getResourceAsStream("/Document/CUTE.pdf");
//                                byte[] bytes = IOUtils.toByteArray(inputStream);
//                                //Base64 from java.util
//                                String base64 = Base64.getEncoder().encodeToString(bytes);
//                                //This must be ran on FXApplicationThread
//                                webEngine.executeScript("openFileFromBase64('" + base64 + "')");
//                            } catch (IOException e) {
//                                throw new RuntimeException(e);
//                            } finally {
//                                if(stream!=null)
//                                {
//                                    try {
//                                        stream.close();
//                                    } catch (IOException e) {
//                                        throw new RuntimeException(e);
//                                    }
//                                }
//                            }
//                        }
//                    });
//
//            Stage primaryStage=getPrimaryStage();
//            primaryStage.setScene(new Scene(webView));
//            setPrimaryStage(primaryStage);
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @FXML
    private void handleDocumentStatus() {
         selectedBook = documentTable.getSelectionModel().getSelectedItem();
       if (selectedBook != null) {
           String query = "Select count(*) from borrowings\n"
                   + "Where userName = ? and documentName = ? and returned = false";
           DatabaseHelper.connectToDatabase();
           try (Connection conn = DatabaseHelper.getConnection()) {
               PreparedStatement stmt = conn.prepareStatement(query);

               stmt.setString(1, User.getUsername());
               stmt.setString(2, selectedBook.getDocumentName());

               ResultSet rs = stmt.executeQuery();
                  if (rs.next()) {
                       int count = rs.getInt(1);
                  }

           } catch (SQLException e) {
               e.printStackTrace();
           }
       }
    }

    @FXML
    private void updateDocTable() {
        documentList.clear();

        if (" All".equals(statusChoice.getValue())) {
            loadDocumentData();
        } else if (" Borrowed".equals(statusChoice.getValue())) {
            String query = "SELECT d.documentID, d.documentName, d.authors, d.fileName, d.status "
                    + "FROM borrowings b "
                    + "JOIN documents d ON b.documentName = d.documentName "
                    + "WHERE b.userName = ? and b.returned = false";

            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, User.getUsername());

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Document document = new Document(
                                rs.getInt("documentID"),
                                rs.getString("documentName"),
                                rs.getString("authors"),
                                rs.getString("fileName"),
                                rs.getString("status"),
                                Document.getTagsByDocumentID(rs.getInt("documentID"))
                        );
                        documentList.add(document);
                    }
                    documentTable.setItems(documentList);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (" Available".equals(statusChoice.getValue())) {
            String query = "SELECT d.documentID, d.documentName, d.authors, d.fileName, d.status, d.quantity "
                    + "FROM documents d "
                    + "LEFT JOIN borrowings b ON d.documentName = b.documentName AND b.userName = ? "
                    + "WHERE d.status = 'available' AND b.documentName IS NULL";

            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, User.getUsername());

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Document document = new Document(
                                rs.getInt("documentID"),
                                rs.getString("documentName"),
                                rs.getString("authors"),
                                rs.getString("fileName"),
                                rs.getString("status"),
                                Document.getTagsByDocumentID(rs.getInt("documentID")),
                                rs.getInt("quantity")
                        );
                        documentList.add(document);
                    }
                    documentTable.setItems(documentList);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static Document selectedDocument;

    @FXML
    public void handleDetailButton() {
        selectedDocument = documentTable.getSelectionModel().getSelectedItem();

        if (selectedDocument != null) {
            // Pass the document ID or other details to the BookDetailController
            try {
                contentPane.getStylesheets().remove(getClass().getResource("/CSS/Document.css").toExternalForm());
                loadFXMLtoAnchorPane("BookDetail", contentPane);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "There was an error while trying to open the document details.");
            }
        } else {
            // Alert the user if no report is selected
            showAlert("No selection", "Please select a document!");
        }
    }
}