package library;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
        if (bookNameField.getText().isEmpty())
        {
            showAlert("Error", "Book's name can't be null");
        }
        else if (selectedFile == null)
        {
            showAlert("Error", "Selected file can't be null");
        }
        else
        {
            String queryCheck = "SELECT COUNT(*) FROM documents WHERE fileName = ?";
            String queryInsert = "INSERT INTO documents (documentName, authors, categoryID, fileName) VALUES (?, ?, ?, ?)";
            String queryCategoryCheck = "SELECT categoryID FROM categories WHERE categoryName = ?";
            String queryCategoryInsert = "INSERT INTO categories (categoryName) VALUES (?)";

            DatabaseHelper.connectToDatabase();

            try (PreparedStatement stmtCheck = DatabaseHelper.getConnection().prepareStatement(queryCheck))
            {
                stmtCheck.setString(1, selectedFile.getName());
                ResultSet rs = stmtCheck.executeQuery();
                rs.next();
                int count = rs.getInt(1);

                if (count > 0)
                {
                    showAlert("Error", "The file already exists in the database");
                    return;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                showAlert("Error", "Failed to check existing file");
                return;
            }

            File dest = new File(DEFAULT_PATH + selectedFile.getName());
            try {
                FileUtils.copyFile(selectedFile.getAbsoluteFile(), dest);
                System.out.println("File copied successfully");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to copy file");
                return;
            }

            int categoryID = -1;
            try (PreparedStatement stmtCategoryCheck = DatabaseHelper.getConnection().prepareStatement(queryCategoryCheck))
            {
                stmtCategoryCheck.setString(1, categoryField.getText());
                ResultSet rs = stmtCategoryCheck.executeQuery();

                if (rs.next()) {
                    categoryID = rs.getInt("categoryID");
                } else {
                    try (PreparedStatement stmtCategoryInsert = DatabaseHelper.getConnection().prepareStatement(queryCategoryInsert, PreparedStatement.RETURN_GENERATED_KEYS)) {
                        stmtCategoryInsert.setString(1, categoryField.getText());
                        stmtCategoryInsert.executeUpdate();

                        ResultSet generatedKeys = stmtCategoryInsert.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            categoryID = generatedKeys.getInt(1);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                showAlert("Error", "Failed to check or insert category");
                return;
            }

            try (PreparedStatement stmtInsert = DatabaseHelper.getConnection().prepareStatement(queryInsert))
            {
                stmtInsert.setString(1, bookNameField.getText());
                stmtInsert.setString(2, authorField.getText());
                stmtInsert.setInt(3, categoryID);
                stmtInsert.setString(4, selectedFile.getName());
                stmtInsert.executeUpdate();
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
