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
    private Button loginButton;

    @FXML
    private Button forgotPasswordButton;

    @FXML
    private Button togglePasswordButton;

    @FXML
    private Button createAccountButton;

    public void handleMouseEnterForgotPassword() {
        forgotPasswordButton.setStyle("-fx-background-color: transparent;" +
                " -fx-text-fill: white;" +
                " -fx-font-family: 'Montserrat';" +
                " -fx-font-weight: bold; -fx-cursor: hand;");
    }

    public void handleMouseExitForgotPassword() {
        forgotPasswordButton.setStyle("-fx-background-color: transparent;" +
                " -fx-text-fill: black;" +
                " -fx-font-family: 'Montserrat'; " +
                "-fx-font-weight: bold; -fx-cursor: hand;");
    }

    public void handleMouseEnterCreateAccount() {
        createAccountButton.setStyle("-fx-background-color: transparent;" +
                " -fx-text-fill: white;" +
                " -fx-font-family: 'Montserrat';" +
                " -fx-font-weight: bold; -fx-cursor: hand;");
    }

    public void handleMouseExitCreateAccount() {
        createAccountButton.setStyle("-fx-background-color: transparent;" +
                " -fx-text-fill: black;" +
                " -fx-font-family: 'Montserrat';" +
                " -fx-font-weight: bold; -fx-cursor: hand;");
    }

    @FXML
    public void handleMouseEnter() {
        loginButton.setScaleX(1.05);
        loginButton.setScaleY(1.05);
        loginButton.setStyle("-fx-background-color: #4c70ba; " +
                "-fx-font-family: 'Montserrat'; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 5; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 15, 0, 0, 4);" +
                "-fx-cursor: hand;");
    }

    @FXML
    public void handleMouseExit() {
        loginButton.setScaleX(1.0);
        loginButton.setScaleY(1.0);
        loginButton.setStyle("-fx-background-color: #3b5998; " +
                "-fx-font-family: 'Montserrat'; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        passwordField.setPromptText("Password");
        passwordField.setText("");
        passwordFieldHidden.setVisible(false);
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
