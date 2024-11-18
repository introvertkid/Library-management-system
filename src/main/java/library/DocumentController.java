package library;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private Button findBookButton;

    @FXML
    private Button deleteBookButton;

    @FXML
    private Button editBookButton;

    @FXML
    private TextField searchField;

    private ObservableList<Document> documentList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDocumentData();
        TableColumn<Document, Integer> documentIDColumn = new TableColumn<>("ID");
        documentIDColumn.setCellValueFactory(new PropertyValueFactory<>("documentID"));

        TableColumn<Document, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("documentName"));

        TableColumn<Document, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Document, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Document, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));

        documentTable.getColumns().addAll(documentIDColumn, titleColumn, authorColumn, categoryColumn, quantityColumn);
        findBookButton.setOnAction(event -> handleFindBook());
        editBookButton.setOnAction(event -> handleEditBook());
        deleteBookButton.setOnAction(event -> handleDeleteBook());
    }

    private void loadDocumentData() {
        documentList = FXCollections.observableArrayList();

        DatabaseHelper.connectToDatabase();
        try (Connection connection = DatabaseHelper.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT d.documentID, d.documentName, d.authors, c.categoryName, d.quantity " +
                     "FROM documents d LEFT JOIN categories c ON d.categoryID = c.categoryID")) {

            while (resultSet.next()) {
                int documentID = resultSet.getInt("documentID");
                String documentName = resultSet.getString("documentName");
                String category = (resultSet.getString("categoryName"));
                String authors = resultSet.getString("authors");
                int quantity = resultSet.getInt("quantity");

                Document document = new Document(documentID, documentName, authors, category, quantity);
                System.out.println(documentID + " " + documentName + " " + category + " " + authors + " " + quantity);
                documentList.add(document);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        documentTable.setItems(documentList);
    }

    @FXML
    private void handleFindBook() {
        String searchQuery = searchField.getText().trim();
        if (searchQuery.isEmpty()) {
            showAlert("Input Error", "Please enter a search term.");
            documentTable.setItems(documentList);
        } else {
            FilteredList<Document> filteredList = new FilteredList<>(documentList, book ->
                    book.getDocumentName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            book.getAuthors().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            book.getCategory().toLowerCase().contains(searchQuery.toLowerCase())
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

    public void openDocument() {
        load1("CUTE.pdf");
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
        TextField categoryField = new TextField(String.valueOf(selectedBook.getCategory()));
        TextField quantityField = new TextField(String.valueOf(selectedBook.getQuantity()));

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(new javafx.scene.control.Label("Title:"), titleField, new javafx.scene.control.Label("Author:"), authorField,
                new javafx.scene.control.Label("Category:"), categoryField, new Label("Quantity:"), quantityField);
        dialog.getDialogPane().setContent(vbox);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == editButtonType) {
                if (titleField.getText().isEmpty() || authorField.getText().isEmpty() ||
                        categoryField.getText().isEmpty() || quantityField.getText().isEmpty()) {
                    showAlert("Input Error", "Please fill in all fields.");
                    return null;
                }
                return new Document(selectedBook.getDocumentID(), titleField.getText(), authorField.getText(),
                        categoryField.getText(), Integer.parseInt(quantityField.getText()));
            }
            return null;
        });

        Optional<Document> result = dialog.showAndWait();
        result.ifPresent(this::updateBookInDatabase);
    }

    private void updateBookInDatabase(Document updatedBook) {
        String updateQuery = "UPDATE documents SET documentName = ?, authors = ?, categoryID = ?, quantity = ? WHERE documentID = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
            pstmt.setString(1, updatedBook.getDocumentName());
            pstmt.setString(2, updatedBook.getAuthors());
            pstmt.setInt(3, getCategoryIdByName(updatedBook.getCategory()));
            pstmt.setInt(4, updatedBook.getQuantity());
            pstmt.setInt(5, updatedBook.getDocumentID());
            pstmt.executeUpdate();
            loadDocumentData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        alert.setHeaderText("Are you sure you want to delete this book?");
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getCategoryIdByName(String categoryName) {
        String query = "SELECT categoryID FROM categories WHERE categoryName = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, String.valueOf(categoryName));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("categoryID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
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
}