package library.controller;

import library.helper.DatabaseHelper;
import library.entity.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.FileNotFoundException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class BaseSceneController extends Controller {
    @FXML
    private VBox navigationBar;

    @FXML
    private Button addBookButton;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button documentButton;

    @FXML
    private Button reportButton;

    @FXML
    private Button userButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button exploreButton;

    @FXML
    private Text userNameText, role;

    @FXML
    private ImageView userAvatar;

    @FXML
    private final Image defaultAvatar = new Image("/image/UserAvatar/userAvatar.png");

    @FXML
    private Button showReportButton;

    @FXML
    private Button showDocRequests;

    @FXML
    private Accordion userRequest;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCircularAvatar();
        userNameText.setText(User.getUserFullName());
        role.setText(User.getRole());
        Button selectedButton = dashboardButton;
        setStyleForSelectedButton(selectedButton);
        for (Node node : navigationBar.getChildren()) {
            if (node instanceof Button) {
                Button but = (Button) node;
                but.setOnMouseEntered(event -> but.setCursor(Cursor.HAND));
                but.setOnMouseExited(event -> but.setCursor(Cursor.DEFAULT));
            }
        }
        loadFXMLtoAnchorPane("Dashboard", contentPane);

        if (User.getRole().equals("Admin")) {
            showReportButton.setVisible(true);
            showDocRequests.setVisible(true);
            userRequest.setVisible(true);
        }
    }

    public void setCircularAvatar() {
        userAvatar.setImage(getUserAvatar(User.getUsername()));
        double centerX = userAvatar.getFitWidth() / 2;
        double centerY = (userAvatar.getFitHeight() - 1.75) / 2;
        double radius = (Math.min(userAvatar.getFitWidth(), userAvatar.getFitHeight()) - 1) / 2;
        Circle clip = new Circle(centerX, centerY, radius);
        userAvatar.setClip(clip);
    }

    @FXML
    private Image getUserAvatar(String username) {
        String query = "SELECT avatar FROM users WHERE username = ?";
        String avt = "userAvatar.png";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                avt = rs.getString("avatar");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Image avatarImage = defaultAvatar;
        String avatarPath = "/image/UserAvatar/" + avt;

        try {
            URL resource = getClass().getResource(avatarPath);
            if (resource != null) {
                avatarImage = new Image(resource.toExternalForm());
                System.out.println("Avatar loaded from: " + avatarImage.getUrl());
            } else {
                throw new FileNotFoundException("Resource not found: " + avatarPath);
            }
        } catch (Exception e) {
            System.out.println("Failed to get avatar from: " + avatarPath);
            e.printStackTrace();
        }

        return avatarImage;
    }

    @FXML
    private void handleAddDocumentButton() {
        loadFXMLtoAnchorPane("AddDocumentScene", contentPane);
        setStyleForSelectedButton(addBookButton);

    }

    @FXML
    private void handleReportButtonAction() {
        loadFXMLtoAnchorPane("ReportScene", contentPane);
        setStyleForSelectedButton(reportButton);
    }

    @FXML
    private void handleShowReportButtonAction() {
        loadFXMLtoAnchorPane("ShowReportScene", contentPane);
        setStyleForSelectedButton(showReportButton);
    }

    @FXML
    private void handleDashBoardButton() {
        loadFXMLtoAnchorPane("Dashboard", contentPane);
        setStyleForSelectedButton(dashboardButton);
    }

    @FXML
    private void handleDocumentButton() {
        loadFXMLtoAnchorPane("DocumentScene", contentPane);
        setStyleForSelectedButton(documentButton);
    }

    @FXML
    private void handleDocRequestsButton() {
        loadFXMLtoAnchorPane("DocumentRequest", contentPane);
        setStyleForSelectedButton(showDocRequests);
    }

    @FXML
    private void UserProfile() {
        loadFXMLtoAnchorPane("ProfileScene", contentPane);
        setStyleForSelectedButton(userButton);
    }

    public void Logout(ActionEvent actionEvent) {
        loadNewScene("LoginScene", actionEvent);
        setStyleForSelectedButton(logoutButton);
    }

    @FXML
    public void handleExploreButton() {
        loadFXMLtoAnchorPane("ExploreScene", contentPane);
        setStyleForSelectedButton(exploreButton);
    }
}
