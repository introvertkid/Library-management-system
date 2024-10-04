package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordFieldHidden;

    @FXML
    private Button togglePasswordButton;

    private boolean isPasswordVisible = false;
    @FXML
    private ImageView imageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

//        imageView.fitWidthProperty().bind(Dashboard.widthProperty());
//        imageView.fitHeightProperty().bind(Dashboard.heightProperty());

        passwordField.setPromptText("Password");
        passwordField.setText("");
        passwordFieldHidden.setVisible(false);

        // Thêm hiệu ứng cho các nút
//        addHoverEffect(loginButton);
//        addHoverEffect(forgotpass);
//        addHoverEffect(create);
    }

    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = isPasswordVisible ? passwordField.getText() : passwordFieldHidden.getText();
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username and password cannot be empty.");
            return;
        }

        if (authenticate(username, password)) {
            showAlert("Success", "Login successful!");
            // Chuyển sang màn hình hoặc chức năng tiếp theo
        } else {
            showAlert("Error", "Invalid username or password.");
        }
    }

    private boolean authenticate(String username, String password) {
        return "admin".equals(username) && "password".equals(password);
    }
    @FXML
    private void togglePasswordVisibility(ActionEvent event) {
        boolean isHiddenVisible = passwordFieldHidden.isVisible();

        if (isHiddenVisible) {
            passwordField.setVisible(true);
            passwordFieldHidden.setVisible(false);
            togglePasswordButton.setText("\uD83D\uDC41");
        } else {
            passwordFieldHidden.setText(passwordField.getText());
            passwordField.setVisible(false);
            passwordFieldHidden.setVisible(true);
            togglePasswordButton.setText("\uD83D\uDC41");
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

//    private void addHoverEffect(Button button) {
//        DropShadow shadow = new DropShadow();
//        shadow.setColor(Color.TRANSPARENT);
//
//        button.setOnMouseEntered((MouseEvent e) -> {
////            button.setStyle("-fx-text-fill: #87CEFA;");
//            button.setEffect(shadow);
//        });
//
//        button.setOnMouseExited((MouseEvent e) -> {
//            button.setStyle("");
//            button.setEffect(null);
//        });
//    }

    public Object loadFXML(String name) {
        String url = "/FXML/" + name + ".fxml";
        Object obj = null;
        try {
            obj = FXMLLoader.load(this.getClass().getResource(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static Stage loadCurrentStage(ActionEvent mouseEvent) {
        return (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    }

    @FXML
    public void handleForgotPassword(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Quên mật khẩu");
        dialog.setHeaderText("Nhập địa chỉ email của bạn");
        dialog.setContentText("Email:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(email -> {
            if (email.isEmpty()) {
                showAlert("Error", "Email cannot be empty.");
            } else {
                showAlert("Success", "Password reset link sent to: " + email);
            }
        });
    }
    @FXML
    public void handleCreateAccount(ActionEvent actionEvent) {
        try {
            // Tải màn hình tạo tài khoản
            Parent root = (Parent) loadFXML("CreateAccount");
            Stage stage = loadCurrentStage(actionEvent);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Tạo Tài Khoản");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Create Account screen.");
        }
    }

}
