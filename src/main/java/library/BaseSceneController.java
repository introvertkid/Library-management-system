package library;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

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
    private Text userNameText;

    @FXML
    private ImageView userAvatar;

    @FXML
    private Button selectedButton;

    @FXML
    private Button addBookButton;

    @FXML
    private final Image defaultAvatar = new Image("/image/UserAvatar/userAvatar.png");

    @FXML
    private Button showReportButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DatabaseHelper.connectToDatabase();
        setCircularAvatar();
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
        }
    }

    public void setCircularAvatar() {
        String userFullName = getUserNameFromDatabase(LoginController.UserSession.currentUser);
        userNameText.setText(userFullName);
        userAvatar.setImage(changeAvatar(LoginController.UserSession.currentUser));
        double centerX = userAvatar.getFitWidth() / 2;
        double centerY = userAvatar.getFitHeight() / 2;
        double radius = Math.min(userAvatar.getFitWidth(), userAvatar.getFitHeight()) / 2;
        Circle clip = new Circle(centerX, centerY, radius);
        userAvatar.setClip(clip);
    }


    @FXML
    private Image changeAvatar(String username) {
        String query = "SELECT avatar FROM users WHERE username = ?";
        String avt = "";
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
        if (avt != null && !avt.isEmpty()) {
            String avatarPath = "/image/UserAvatar/" + avt;
            if (!avatarPath.equals(defaultAvatar.getUrl())) {
                try {
                    avatarImage = new Image(avatarPath);
                    System.out.println(avatarImage.getUrl());
                } catch (Exception e) {
                    System.out.println("Avatar path: " + avatarPath);
                    e.printStackTrace();
                }
            } else {
                avatarImage = defaultAvatar;
            }
        }
        return avatarImage;
    }


    private String getUserNameFromDatabase(String username) {
        String userFullName = "";
        String query = "SELECT userFullName FROM users WHERE username = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                userFullName = rs.getString("userFullName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(userFullName);
        return userFullName;
    }

    private void setStyleForSelectedButton(Button button) {
        if (selectedButton != null) {
            selectedButton.setStyle("");
        }
        selectedButton = button;
        selectedButton.setStyle(
                "    -fx-background-color: #4c70ba;\n" +
                        "    -fx-font-family: 'Montserrat';\n" +
                        "    -fx-text-fill: white;\n" +
                        "    -fx-background-radius: 5;\n" +
                        "    -fx-font-weight: bold;\n" +
                        "    -fx-cursor: hand;");
    }

    @FXML
    private void handleAddDocumentButton() {
        loadFXMLtoAnchorPane("AddDocumentScene", contentPane);
    }

    @FXML
    private void handleReportButtonAction() {
        loadFXMLtoAnchorPane("ReportScene", contentPane);
        setStyleForSelectedButton(reportButton);
    }

    @FXML
    private void handleShowReportButtonAction() {
        loadFXMLtoAnchorPane("ShowReportScene", contentPane);
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
    private void UserProfile() {
        loadFXMLtoAnchorPane("ProfileScene", contentPane);
        setStyleForSelectedButton(userButton);
    }

    public void Logout(ActionEvent actionEvent) {
        loadNewScene("LoginScene", actionEvent);
        setStyleForSelectedButton(logoutButton);
    }
}
