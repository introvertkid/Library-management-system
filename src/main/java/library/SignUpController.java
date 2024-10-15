package library;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.net.URL;

public class SignUpController extends Controller {
    @FXML
    private HBox signUpHBox;

    @FXML
    private CheckBox checkBox;

    @FXML
    private Button showPassword;

    @FXML
    private Button showConfirmPassword;

    @FXML
    private TextField nameField, phoneField, emailField;

    @FXML
    private PasswordField passwordField, confirmPasswordField;

    @FXML
    private TextField passwordTextField, confirmPasswordTextField;

    @FXML
    private Text nameError, passwordError, checkboxError;

    @FXML
    private Button signUpButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initially, hide the text fields and only show the password fields
        passwordTextField.setManaged(false);
        passwordTextField.setVisible(false);
        confirmPasswordTextField.setManaged(false);
        confirmPasswordTextField.setVisible(false);

        // Sync the text fields with password fields
        passwordField.textProperty().bindBidirectional(passwordTextField.textProperty());
        confirmPasswordField.textProperty().bindBidirectional(confirmPasswordTextField.textProperty());
        // Disable the sign-up button by default
        signUpButton.setDisable(true);

        // Validate input fields to enable the sign-up button
        addInputValidation();
    }

    // Method to validate input fields and enable the sign-up button
    private void addInputValidation() {
        nameField.textProperty().addListener((observable, oldValue, newValue) -> checkInputs());
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> checkInputs());
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> checkInputs());
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> checkInputs());
    }

    private void checkInputs() {
        boolean isNameEmpty = nameField.getText().trim().isEmpty();
        boolean isPasswordEmpty = passwordField.getText().trim().isEmpty();
        boolean isConfirmPasswordEmpty = confirmPasswordField.getText().trim().isEmpty();
        boolean isCheckBoxSelected = checkBox.isSelected();

        if (isNameEmpty) {
            nameError.setText("Please enter a name.");
        } else {
            nameError.setText("");
        }

        if (isPasswordEmpty || isConfirmPasswordEmpty || !passwordField.getText().equals(confirmPasswordField.getText())) {
            passwordError.setText("Passwords do not match.");
        } else {
            passwordError.setText("");
        }

        if (!isCheckBoxSelected) {
            checkboxError.setText("You must agree to the terms.");
        } else {
            checkboxError.setText("");
        }

        // Enable the sign-up button only when all inputs are valid
        signUpButton.setDisable(isNameEmpty || isPasswordEmpty || isConfirmPasswordEmpty || !isCheckBoxSelected || !passwordField.getText().equals(confirmPasswordField.getText()));
    }

    @FXML
    private void handleShowPassword(ActionEvent actionEvent) {
        boolean isPasswordVisible = passwordField.isVisible();
        if (!isPasswordVisible) {
            passwordField.setText(passwordTextField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);
            showPassword.setText("\uD83D\uDE48");
        } else {
            passwordField.setText(passwordTextField.getText());
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);
            showPassword.setText("\uD83D\uDE49");
        }
    }

    @FXML
    public void handleShowConfirmPassword(ActionEvent actionEvent) {
        boolean isConfirmPasswordVisible = confirmPasswordField.isVisible();
        if (!isConfirmPasswordVisible) {
            confirmPasswordField.setText(confirmPasswordTextField.getText());
            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);
            confirmPasswordTextField.setVisible(false);
            confirmPasswordTextField.setManaged(false);
            showConfirmPassword.setText("\uD83D\uDE48");
        } else {
            confirmPasswordField.setText(confirmPasswordTextField.getText());
            confirmPasswordField.setVisible(false);
            confirmPasswordField.setManaged(false);
            confirmPasswordTextField.setVisible(true);
            confirmPasswordTextField.setManaged(true);
            showConfirmPassword.setText("\uD83D\uDE49");
        }
    }

    public boolean checkIfUserExists(String username, String phoneNumber, String email) {
        String query = "SELECT COUNT(*) FROM users WHERE userName = ? OR phoneNumber = ? OR gmail = ?";

        try (PreparedStatement stmt = DatabaseHelper.getConnection().prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, phoneNumber);
            stmt.setString(3, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0; // If count is more than 0, the user exists
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if there's an error or no matching user found
    }

    @FXML
    void handleBack(ActionEvent actionEvent) {
        loadNewScene("LoginScene",actionEvent);
    }

    public void handleSigUp() {
        // Get the user input
        String username = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        // Check if user exists in the database
        if (checkIfUserExists(username, phone, email)) {
            nameError.setText("Username, phone, or email already exists!");
            return;
        }

        signUpHBox.getChildren().clear();

        Text successMessage = new Text("Sign up successfully");
        successMessage.setFont(Font.font("System", FontWeight.BOLD, 20));
        successMessage.setFill(Color.GREEN);

        Button returnButton = new Button("Return to Login Page");
        returnButton.setStyle("-fx-background-color: blue; -fx-text-fill: white;");
        returnButton.setFont(Font.font("System Bold", 18));


        returnButton.setOnMouseClicked(e -> {
            loadNewScene("LoginScene", new ActionEvent());
        });

        VBox successLayout = new VBox(10);
        successLayout.setAlignment(Pos.CENTER);
        successLayout.getChildren().addAll(successMessage, returnButton);

        signUpHBox.getChildren().add(successLayout);
        signUpHBox.setAlignment(Pos.CENTER);
    }
}
