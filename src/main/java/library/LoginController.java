package library;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginController extends Controller {

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

        setupFieldFocusListener(usernameField, "Username");
        setupFieldFocusListener(passwordField, "Password");
        setupFieldFocusListener(passwordFieldHidden, "Password");

    }

    public void setupFieldFocusListener(TextInputControl a, String p) {
        a.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                a.setPromptText("");
                a.setStyle("-fx-border-color: #4c70ba; -fx-border-radius: 5;");
            } else {
                a.setPromptText(p);
                a.setStyle("-fx-border-color: #A0A0A0; -fx-border-radius: 5;");
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
            togglePasswordButton.setText("\uD83D\uDE48");
        } else {
            passwordFieldHidden.setText(passwordField.getText());
            passwordField.setVisible(false);
            passwordFieldHidden.setVisible(true);
            togglePasswordButton.setText("\uD83D\uDE49");
        }
    }

    @FXML
    public void handleLogin(ActionEvent actionEvent) {
        String username = usernameField.getText();
        String password = passwordField.isVisible() ? passwordField.getText() : passwordFieldHidden.getText();

        if (login(username, password)) {
            loadNewScene("BaseScene", actionEvent);
        }
    }

    @FXML
    public void handleForgotPassword(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Forgot password");
        dialog.setHeaderText("Please enter your email!");
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

    public boolean login(String username, String password) {
        DatabaseHelper.connectToDatabase();
        try (Connection conn = DatabaseHelper.getConnection()) {
            String query = "SELECT * FROM users WHERE username = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String storedPassword = resultSet.getString("hashedPassword");

                if (password.equals(storedPassword)) {
                    System.out.println("Login Successful!");
                    User.loadUserData(resultSet);
                    return true;
                } else {
                    showAlert("Error", "Incorrect username or password");
                    return false;
                }

            } else if (username.isEmpty() && password.isEmpty()) {
                showAlert("Error", "Please enter your username and password!");
                return false;
            } else if (username.isEmpty()) {
                showAlert("Error", "Please enter your username!");
                return false;
            } else if (password.isEmpty()) {
                showAlert("Error", "Please enter your password!");
                return false;
            } else {
                showAlert("Error", "User not found");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    public void handleCreateAccount(ActionEvent actionEvent) {
        loadNewScene("SignUpScene", actionEvent);
    }
}

