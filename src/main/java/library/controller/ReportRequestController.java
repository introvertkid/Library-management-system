package library.controller;

import library.entity.Report;
import library.helper.DatabaseHelper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ReportRequestController extends Controller {
    @FXML
    private AnchorPane contentPane;
    @FXML
    private TableView<Report> reportTable;
    @FXML
    private TableColumn<Report, Integer> reportIDColumn;
    @FXML
    private TableColumn<Report, Integer> userIDColumn;
    @FXML
    private TableColumn<Report, String> reportTypeColumn;
    @FXML
    private TableColumn<Report, String> titleColumn;
    @FXML
    private TableColumn<Report, String> statusColumn;
    @FXML
    private ComboBox<String> statusChoice;

    public static Report selectedReport;

    private ObservableList<Report> reportList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up cell value factories
        reportIDColumn.setCellValueFactory(new PropertyValueFactory<>("reportID"));
        userIDColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));
        reportTypeColumn.setCellValueFactory(new PropertyValueFactory<>("reportType"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Load data from database
        loadReportData(null);

        // Add listener to statusChoice combo box
        statusChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
            loadReportData(newValue); // Filter reports based on selected status
        });
    }

    private void loadReportData(String status) {
        reportList.clear();

        String query = "SELECT reportID, userID, reportType, title, content, status FROM reports";
        if (status != null && !status.equalsIgnoreCase("All")) {
            query += " WHERE status = ?";
        }

        DatabaseHelper.connectToDatabase();
        try (Connection connection = DatabaseHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            if (status != null && !status.equalsIgnoreCase("All")) {
                statement.setString(1, status);
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Report report = new Report(
                        resultSet.getInt("reportID"),
                        resultSet.getInt("userID"),
                        resultSet.getString("reportType"),
                        resultSet.getString("title"),
                        resultSet.getString("content"),
                        resultSet.getString("status")
                );
                reportList.add(report);
            }

            reportTable.setItems(reportList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openReport(ActionEvent event) {
        // Get the selected report
        selectedReport = reportTable.getSelectionModel().getSelectedItem();

        if (selectedReport != null) {
            // Pass the report ID or other details to the ReportDetailController
            try {contentPane.getStylesheets().remove(getClass().getResource("/CSS/ShowReportScene.css").toExternalForm());
                loadFXMLtoAnchorPane("ReportDetailsScene", contentPane);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "There was an error while trying to open the report details.");
            }
        } else {
            // Alert the user if no report is selected
            showAlert("No selection", "Please select a report!");
        }
    }
}
