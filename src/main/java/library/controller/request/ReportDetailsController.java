package library.controller.request;

import library.controller.Controller;
import library.helper.DatabaseHelper;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ReportDetailsController extends Controller {
    @FXML
    private Button handledOrPendingButton;

    @FXML
    private Label typeLabel, statusLabel;

    @FXML
    private TextField titleField;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private TextArea contentField;

    private int reportID = ReportRequestController.selectedReport.getReportID();
    private String reportType = ReportRequestController.selectedReport.getReportType();
    private String title = ReportRequestController.selectedReport.getTitle();
    private String content = ReportRequestController.selectedReport.getContent();
    private String status = ReportRequestController.selectedReport.getStatus();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeLabel.setText("Type: " + reportType);
        statusLabel.setText("Status: " + status);
        titleField.setText(title);
        contentField.setText(content);

        updateButtonText();
    }

    private void updateButtonText() {
        if (status.equals("Pending")) {
            handledOrPendingButton.setText("Mark as Handled");
        } else {
            handledOrPendingButton.setText("Mark as Pending");
        }
    }

    @FXML
    public void handledOrPending() {
        // Toggle status
        status = status.equals("Pending") ? "Handled" : "Pending";

        // Update the status in the database
        if (updateReportStatusInDatabase()) {
            // Show a confirmation dialog
            showAlert("Status Updated", "Report status has been updated to " + status + ".");
            loadFXMLtoAnchorPane("ShowReportScene", contentPane);

            // Update the button text
            updateButtonText();
        } else {
            // Show an error dialog if the update fails
            showAlert("Update Failed", "There was an error updating the report status. Please try again.");
        }
    }

    private boolean updateReportStatusInDatabase() {
        String query = "UPDATE reports SET status = ? WHERE reportID = ?";
        DatabaseHelper.connectToDatabase();
        try (Connection connection = DatabaseHelper.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, reportID);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // Return true if the update was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
