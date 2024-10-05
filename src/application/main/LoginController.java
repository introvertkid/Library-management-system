package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginController extends Controller
{
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordFieldHidden;

    @FXML
    private Button togglePasswordButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
//        imageView.fitWidthProperty().bind(Dashboard.widthProperty());
//        imageView.fitHeightProperty().bind(Dashboard.heightProperty());

        passwordField.setPromptText("Password");
        passwordField.setText("");
        passwordFieldHidden.setVisible(false);

        //todo:
//        addHoverEffect(loginButton);
//        addHoverEffect(forgotpass);
//        addHoverEffect(create);
    }

    @FXML
    public void handleLogin(ActionEvent actionEvent) {
        String username = usernameField.getText();
        String password = passwordField.isVisible() ? passwordField.getText() : passwordFieldHidden.getText();
//        if (username.isEmpty() || password.isEmpty()) {
//            showAlert("Error", "Username and password cannot be empty.");
//            return;
//        }

        if (authenticate(username, password)) {
//            showAlert("Success", "Login successful!");
            loadNewRoot("BaseScene", actionEvent);
        } else {
            showAlert("Error", "Invalid username or password.");
        }
    }

    private boolean authenticate(String username, String password) {
        return "".equals(username) && "".equals(password);
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
        loadNewRoot("SignUpScene", actionEvent);
    }
}
