package library;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class AddDocumentController extends Controller
{
    private final String DEFAULT_FILE_NAME="No file chosen";
    private final String DEFAULT_PATH="src\\main\\resources\\Document\\";

    ObservableList<String> suggestions = FXCollections.observableArrayList();
    ObservableList<String> selectedTags = FXCollections.observableArrayList();

    @FXML
    ListView<String> suggestionList = new ListView<>();

    @FXML
    public HBox selectedTagsBox;

    @FXML
    public Text chosenFileName;

    @FXML
    public TextField bookNameField, tagField, authorField;

    File selectedFile;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        suggestionList.setVisible(false);
        suggestions = getAllTagsFromDB();

        //
        tagField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                suggestionList.setVisible(false);
                return;
            }

            ObservableList<String> filteredSuggestions = suggestions.filtered(
                    tag -> tag.toLowerCase().contains(newValue.toLowerCase())
            );

            suggestionList.setItems(filteredSuggestions);
            suggestionList.setVisible(!filteredSuggestions.isEmpty());
        });

        //add tag by pressing enter in tagField
        tagField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER && !tagField.getText().isEmpty()) {
                addTag(tagField.getText(), selectedTags, selectedTagsBox);
                tagField.clear();
                suggestionList.setVisible(false);
            }
        });

        //add tag by clicking on suggestion list
        suggestionList.setOnMouseClicked(mouseEvent -> {
            String selectedTag = suggestionList.getSelectionModel().getSelectedItem();
            if (selectedTag != null) {
                addTag(selectedTag, selectedTags, selectedTagsBox);
                tagField.clear();
                suggestionList.setVisible(false);
            }
        });
    }

    private ObservableList<String> getAllTagsFromDB()
    {
        DatabaseHelper.connectToDatabase();
        ObservableList<String>ans = FXCollections.observableArrayList();
        String query="select tagName from tags";
        try(PreparedStatement statement=DatabaseHelper.getConnection().prepareStatement(query))
        {
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next())
            {
                String tmp=resultSet.getString("tagName");
                ans.add(tmp);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return ans;
    }

    public void addDocument() {
        FileChooser fc = new FileChooser();
        selectedFile = fc.showOpenDialog(null);
//        if(selectedFile!=null)
//        {
        chosenFileName.setText(selectedFile.getName());
//        }
    }

    private void addTag(String tag, ObservableList<String> selectedTags, HBox selectedTagsBox) {
        if (selectedTags.contains(tag)) {
            return;
        }

        selectedTags.add(tag);

        Label tagLabel = new Label(tag);

        tagLabel.setStyle("-fx-background-color: lightblue; -fx-padding: 5; -fx-border-radius: 5; -fx-background-radius: 5;");
        Label removeButton = new Label("X");
        removeButton.setStyle("-fx-text-fill: red; -fx-cursor: hand; -fx-padding: 0 5;");
        HBox tagBox = new HBox(tagLabel, removeButton);
        tagBox.setSpacing(5);

        removeButton.setOnMouseClicked(event -> {
            selectedTags.remove(tag);
            selectedTagsBox.getChildren().remove(tagBox);
        });

        selectedTagsBox.getChildren().add(tagBox);
    }

    //todo: reset all fields after submit
    public void submit() {
        if (bookNameField.getText().isEmpty()) {
            showAlert("Error", "Book's name can't be null");
        }
        else if(selectedFile==null)
        {
            showAlert("Error", "Selected file can't be null");
        }
        else
        {
            File dest = new File(DEFAULT_PATH + selectedFile.getName());
            
            try {
                FileUtils.copyFile(selectedFile.getAbsoluteFile(), dest);
                System.out.println("File copied successfully");
            } catch (IOException e) {
                e.printStackTrace();
            }

            insertDocumentIntoDB();
        }
    }

    private void insertDocumentIntoDB()
    {
        String query = "INSERT INTO documents (documentName, authors, fileName) " +
                "VALUES (?, ?, ?)";
        DatabaseHelper.connectToDatabase();
        try (PreparedStatement stmt = DatabaseHelper.getConnection().prepareStatement(query)) {
            stmt.setString(1, bookNameField.getText());
            stmt.setString(2, authorField.getText());
            stmt.setString(3, selectedFile.getName());
            stmt.executeUpdate();
            showAlert("Success", "Document added successfully");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add document");
        }
    }
}
