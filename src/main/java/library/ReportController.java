package library;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class ReportController extends Controller {

    @FXML
    private TextArea textArea;

    @FXML
    private ComboBox<String> choice;

    private boolean isChoiceSelected = false;

    @FXML
    public void initialize() {
        textArea.setDisable(true);
        String[] choices = { "Bug", "Document", "User" };
        choice.setItems(FXCollections.observableArrayList(choices));

        choice.setOnAction(event -> {
            String selectedItem = choice.getValue();
            textArea.setDisable(false);
            isChoiceSelected = true;

            switch (selectedItem) {
                case "Bug":
                    System.out.println("Creating bug report...");
                    break;
                case "Document":
                    System.out.println("Creating document report...");
                    break;
                case "User":
                    System.out.println("Creating user report...");
                    break;
                default:
                    System.out.println("Invalid selection");
                    break;
            }
        });

       
    }

    @FXML
    protected void sendReport() {
        if (!isChoiceSelected || textArea.getText().trim().isEmpty()) {
            showAlert("Can't create report!!!","Please select an option first");
            return;
        }

        System.out.println("Report sent");
    }


}
