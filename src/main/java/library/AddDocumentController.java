package library;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;

public class AddDocumentController extends Controller
{
    private final String DEFAULT_FILE_NAME="No file chosen";
    private final String DEFAULT_PATH="src\\main\\resources\\Document\\";

    @FXML
    public Text chosenFileName;

    @FXML
    public TextField bookNameField, categoryField, authorField;

    File selectedFile;

    public void addDocument()
    {
        FileChooser fc = new FileChooser();
        selectedFile = fc.showOpenDialog(null);
//        if(selectedFile!=null)
//        {
            chosenFileName.setText(selectedFile.getName());
//        }
    }

    public void submit()
    {
        if(bookNameField.getText().isEmpty())
        {
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

            String query = "INSERT INTO documents (documentName, authors, fileName) " +
                            "VALUES (?, ?, ?)";
            DatabaseHelper.connectToDatabase();
            try(PreparedStatement stmt = DatabaseHelper.getConnection().prepareStatement(query))
            {
                stmt.setString(1, bookNameField.getText());
                stmt.setString(2, authorField.getText());
                stmt.setString(3, selectedFile.getName());
                stmt.executeUpdate();
                showAlert("Success", "Document added successfully");
            }
            catch (Exception e)
            {
                e.printStackTrace();
                showAlert("Error", "Failed to add document");
            }
        }
    }
}
