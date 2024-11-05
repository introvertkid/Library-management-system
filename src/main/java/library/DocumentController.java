package library;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class DocumentController extends Controller
{
    @FXML
    private TableView<Document> documentTable;

    @FXML
    private TableColumn documentIDColumn;

    @FXML
    private TableColumn documentNameColumn;

    @FXML
    private TableColumn categoryIDColumn;

    @FXML
    private TableColumn authorsColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        documentIDColumn.setCellValueFactory(new PropertyValueFactory<>("documentID"));
        documentNameColumn.setCellValueFactory(new PropertyValueFactory<>("documentName"));
        categoryIDColumn.setCellValueFactory(new PropertyValueFactory<>("categoryID"));
        authorsColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));

        loadDocumentData();
    }

    private void loadDocumentData() {
        ObservableList<Document> documentList = FXCollections.observableArrayList();

        DatabaseHelper.connectToDatabase();
        try (Connection connection = DatabaseHelper.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM documents")) {

            while (resultSet.next()) {
                int documentID = resultSet.getInt("documentID");
                String documentName = resultSet.getString("documentName");
                int categoryID = resultSet.getInt("categoryID");
                String authors = resultSet.getString("authors");

                Document document = new Document(documentID, documentName, categoryID, authors);
                System.out.println(documentID+" "+documentName+" "+categoryID+" "+authors);
                documentList.add(document);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        documentTable.setItems(documentList);
    }

    //this approach will use default PDF viewer in desktop to open file
    public void load1(String fileName)
    {
        try {
            Desktop.getDesktop().open(new File("src/main/resources/Document/" + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void openDocument()
    {
        load1("CUTE.pdf");
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
