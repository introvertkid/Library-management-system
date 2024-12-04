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
    private Button borrowButton;

    @FXML
    private Button openDocumentButton;

    @FXML
    private ComboBox<String> statusChoice;

    @FXML
    private Button unBorrowButton;

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

    public void openDocument() {
        Document selectedDocument = documentTable.getSelectionModel().getSelectedItem();
        if (selectedDocument == null) {
            showAlert("Error", "No document selected. Please select a document to open.");
            return;
        }
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
        TextField tagField = new TextField(String.valueOf(selectedBook.getTagName()));
        TextField quantityField = new TextField(String.valueOf(selectedBook.getQuantity()));
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(new javafx.scene.control.Label("Title:"), titleField, new javafx.scene.control.Label("Author:"), authorField,
                new javafx.scene.control.Label("Tag:"), tagField, new Label("Quantity:"), quantityField);
        dialog.getDialogPane().setContent(vbox);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == editButtonType) {
                if (titleField.getText().isEmpty() || authorField.getText().isEmpty() ||
                        tagField.getText().isEmpty() || quantityField.getText().isEmpty()) {
                    showAlert("Input Error", "Please fill in all fields.");
                    return null;
                }
                return new Document(titleField.getText(), authorField.getText(),
                        tagField.getText(), Integer.parseInt(quantityField.getText()), selectedBook.getDocumentID());
            }
            return null;
        });
        Optional<Document> result = dialog.showAndWait();
        result.ifPresent(this::updateBookInDatabase);
    }

    private void updateBookInDatabase(Document updatedBook) {
        String updateBookQuery = "UPDATE documents SET documentName = ?, authors = ?, quantity = ?, tagID = ? WHERE documentID = ?";
        String getTagIdQuery = "SELECT tagID FROM tags WHERE tagName = ?";
        int tagID = -1;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmtGetTagId = conn.prepareStatement(getTagIdQuery)) {
            pstmtGetTagId.setString(1, updatedBook.getTagName());
            ResultSet rs = pstmtGetTagId.executeQuery();
            if (rs.next()) {
                tagID = rs.getInt("tagID");
            } else {
                String insertTagQuery = "INSERT INTO tags (tagName) VALUES (?)";
                try (PreparedStatement pstmtInsertTag = conn.prepareStatement(insertTagQuery, Statement.RETURN_GENERATED_KEYS)) {
                    pstmtInsertTag.setString(1, updatedBook.getTagName());
                    pstmtInsertTag.executeUpdate();
                    ResultSet rsInsert = pstmtInsertTag.getGeneratedKeys();
                    if (rsInsert.next()) {
                        tagID = rsInsert.getInt(1);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (tagID != -1) {
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(updateBookQuery)) {
                pstmt.setString(1, updatedBook.getDocumentName());
                pstmt.setString(2, updatedBook.getAuthors());
                pstmt.setInt(3, updatedBook.getQuantity());
                pstmt.setInt(4, tagID);
                pstmt.setInt(5, updatedBook.getDocumentID());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        loadDocumentData();
        updateTable(currentPage);
        updatePage();
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
        String deleteQuery = "DELETE FROM documents WHERE documentID = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, documentID);
            pstmt.executeUpdate();
            loadDocumentData();
            updateTable(currentPage);
            updatePage();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    private void handleBorrowButton() {
         selectedBook = documentTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null && selectedBook.getQuantity() > 0) {
            String query = "UPDATE documents\n"
                    + "SET quantity = quantity - 1\n"
                    + "WHERE documentID = ? "
                    + "and status = 'Available'";

            DatabaseHelper.connectToDatabase();
            try (Connection conn = DatabaseHelper.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement(query);

                stmt.setInt(1, selectedBook.getDocumentID());
                if(updateBorrowingsTable() == true) {
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        borrowButton.setVisible(false);
                        openDocumentButton.setVisible(true);
                        selectedBook.setQuantity(selectedBook.getQuantity() - 1);
                        updateDocTable();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

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
                      if (count > 0) {
                          borrowButton.setVisible(false);
                          openDocumentButton.setVisible(true);
                      } else {
                          borrowButton.setVisible(true);
                          openDocumentButton.setVisible(false);
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
            stmt.setString(2, selectedBook.getDocumentName());
            int newRowsAffected = stmt.executeUpdate();

            if (newRowsAffected > 0) {
                showAlert("Alert", "Borrow document successfully!");
                updateDocTable();
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
    private void updateDocTable() {
        documentList.clear();

        if (" All".equals(statusChoice.getValue())) {
            unBorrowButton.setVisible(false);
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
                    unBorrowButton.setVisible(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (" Available".equals(statusChoice.getValue())) {
            unBorrowButton.setVisible(false);
            borrowButton.setVisible(true);
            openDocumentButton.setVisible(false);
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

    @FXML
    private void handleUnborrowed() {
        selectedBook = documentTable.getSelectionModel().getSelectedItem();

        if (selectedBook != null && selectedBook.getQuantity() >= 0) {
            String queryUpdateDocuments = "UPDATE documents "
                    + "SET quantity = quantity + 1 "
                    + "WHERE documentID = ?";

            String queryDeleteBorrowings = "update borrowings "
                    + "set returned = true "
                    + "WHERE documentName = ? AND userName = ?";

            DatabaseHelper.connectToDatabase();
            try (Connection conn = DatabaseHelper.getConnection()) {
                try (PreparedStatement stmtDelete = conn.prepareStatement(queryDeleteBorrowings)) {
                    stmtDelete.setString(1, selectedBook.getDocumentName());
                    stmtDelete.setString(2, User.getUsername());

                    int rowsAffected = stmtDelete.executeUpdate();

                    if (rowsAffected > 0) {
                        try (PreparedStatement stmtUpdate = conn.prepareStatement(queryUpdateDocuments)) {
                            stmtUpdate.setInt(1, selectedBook.getDocumentID());

                            int rowsUpdated = stmtUpdate.executeUpdate();
                            if (rowsUpdated > 0) {

                                selectedBook.setQuantity(selectedBook.getQuantity() + 1);
                               updateDocTable();
                            }
                        }
                    }
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