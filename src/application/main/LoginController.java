//logincontroller
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

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        passwordField.setPromptText("Password");
        passwordField.setText("");
        passwordFieldHidden.setVisible(false);

        // Thêm sự kiện focus cho usernameField
        usernameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                usernameField.setPromptText("");
                usernameField.setStyle("-fx-border-color: #4c70ba; -fx-border-radius: 5;");
            } else {
                usernameField.setPromptText("Username");
                usernameField.setStyle("-fx-border-color: #A0A0A0; -fx-border-radius: 5;");
            }
        });

        // Thêm sự kiện focus cho passwordField
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                passwordField.setPromptText("");
                passwordField.setStyle("-fx-border-color: #4c70ba; -fx-border-radius: 5;");
            } else {
                passwordField.setPromptText("Password");
                passwordField.setStyle("-fx-border-color: #A0A0A0; -fx-border-radius: 5;");
            }
        });

        // Thêm sự kiện focus cho passwordFieldHidden (trường hiển thị mật khẩu)
        passwordFieldHidden.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                passwordFieldHidden.setPromptText("");
                passwordFieldHidden.setStyle("-fx-border-color: #4c70ba; -fx-border-radius: 5;");
            } else {
                passwordFieldHidden.setPromptText("Password");
                passwordFieldHidden.setStyle("-fx-border-color: #A0A0A0; -fx-border-radius: 5;");
            }
        });
    }

    @FXML
    private void togglePasswordVisibility(ActionEvent event) {
        boolean isHiddenVisible = passwordFieldHidden.isVisible();
        if (isHiddenVisible) {
            passwordField.setText(passwordFieldHidden.getText());
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
            loadNewScene("BaseScene", actionEvent);
        } else {
            showAlert("Error", "Invalid username or password.");
        }
    }

    private boolean authenticate(String username, String password) {
        return "".equals(username) && "".equals(password);
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
        loadNewScene("SignUpScene", actionEvent);
    }
}

