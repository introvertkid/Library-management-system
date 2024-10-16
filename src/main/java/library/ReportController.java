package library;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import javafx.stage.FileChooser;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ReportController extends Controller {

    @FXML
    private ComboBox<String> choice;

    @FXML
    private ListView<String> selectedFile;

    private Map<String, String> fileDirectoryMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectedFile.setVisible(false);
        String[] choices = { "Bug", "Document", "User" };
        choice.setItems(FXCollections.observableArrayList(choices));
        selectedFile.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<>() {
                    private Hyperlink hyperlink = new Hyperlink();

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            hyperlink.setText(item);
                            hyperlink.setOnAction(event -> openFile(item));
                            setGraphic(hyperlink);
                        }
                    }
                };
            }
        });

        selectedFile.setOnKeyPressed(this::removeSelectedFile);
    }

    @FXML
    private void sendReport() {
        if (choice.getValue() == null) {
            showAlert("Can't create report!!!", "Please select an option first");
            return;
        }
        System.out.println("Report sent");
    }

    @FXML
    public void setFileChooser() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        List<File> selectedFiles = fc.showOpenMultipleDialog(null);
        if (selectedFiles != null) {
            for (File file : selectedFiles) {
                String fileName = file.getName().trim(); 
                String filePath = file.getParent();
                if (!fileName.isEmpty() && !selectedFile.getItems().contains(fileName)) {
                    this.selectedFile.getItems().add(fileName);
                    fileDirectoryMap.put(fileName, filePath);
                }
            }
            selectedFile.setVisible(!selectedFile.getItems().isEmpty());
        }
    }

    @FXML
    private void removeSelectedFile(KeyEvent event) {
        if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE) {
            SelectionModel<String> selectionModel = selectedFile.getSelectionModel();
            String selectedFileName = selectionModel.getSelectedItem();
            if (selectedFileName != null) {
                this.selectedFile.getItems().remove(selectedFileName);
                fileDirectoryMap.remove(selectedFileName);
            }
            selectedFile.setVisible(!selectedFile.getItems().isEmpty());
        }
    }

    private void openFile(String fileName) {
        try {
            String directoryPath = fileDirectoryMap.get(fileName);
            if (directoryPath != null) {
                File file = new File(directoryPath, fileName);
                Desktop.getDesktop().open(file);
            } else {
                System.out.println("File not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
