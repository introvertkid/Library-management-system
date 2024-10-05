package main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignUpController {
    @FXML
    private HBox signUpHBox;

    @FXML
    private CheckBox checkBox;

    @FXML
    private TextField nameField, phoneField, emailField;

    @FXML
    private PasswordField passwordField, confirmPasswordField;

    @FXML
    private TextField passwordTextField, confirmPasswordTextField;

    @FXML
    private Text nameError, passwordError, checkboxError;

    @FXML
    private Button signUpButton, showPassword, showConfirmPassword;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @FXML
    public void initialize() {
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
    public void handleShowPassword() {
        if (!isPasswordVisible) {
            // Show the password (use TextField)
            passwordTextField.setManaged(true);
            passwordTextField.setVisible(true);
            passwordField.setManaged(false);
            passwordField.setVisible(false);

            // Change flag
            isPasswordVisible = true;
        } else {
            // Hide the password (use PasswordField)
            passwordField.setManaged(true);
            passwordField.setVisible(true);
            passwordTextField.setManaged(false);
            passwordTextField.setVisible(false);

            // Change flag
            isPasswordVisible = false;
        }
    }

    public void handleShowConfirmPassword() {
        if (!isConfirmPasswordVisible) {
            // Show the password (use TextField)
            confirmPasswordTextField.setManaged(true);
            confirmPasswordTextField.setVisible(true);
            confirmPasswordField.setManaged(false);
            confirmPasswordField.setVisible(false);

            // Change flag
            isConfirmPasswordVisible = true;
        } else {
            // Hide the password (use PasswordField)
            confirmPasswordField.setManaged(true);
            confirmPasswordField.setVisible(true);
            confirmPasswordTextField.setManaged(false);
            confirmPasswordTextField.setVisible(false);

            // Change flag
            isConfirmPasswordVisible = false;
        }
    }

    public boolean checkIfUserExists(String username, String phoneNumber, String email) {
        DatabaseHelper.connectToDatabase(); // Ensure the database connection is established
        String query = "SELECT COUNT(*) FROM users WHERE username = ? OR phoneNumber = ? OR gmail = ?";

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

    void loadHomePage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/LoginScene.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) signUpHBox.getScene().getWindow();
            stage.setTitle("Library management system");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleSignUp() {
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
            loadHomePage();
        });

        VBox successLayout = new VBox(10);
        successLayout.setAlignment(Pos.CENTER);
        successLayout.getChildren().addAll(successMessage, returnButton);

        signUpHBox.getChildren().add(successLayout);
        signUpHBox.setAlignment(Pos.CENTER);
    }
}
