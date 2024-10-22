package library;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProfileController extends Controller {
    @FXML
    private Circle avatar;

    @FXML
    private Label userId;

    @FXML
    private Button setAvatar, backButton, changeProfileButton, resetPasswordButton;

    @FXML
    private TextField usernameField, fullNameField, dateOfBirthField, emailField, phoneField, roleField;

    private final String DEFAULT_AVATAR = "src/resources/image/UserAvatar/userAvatar.png";
    private boolean isEditing = false;

    private int getCurrentUserId() {
        return 1000; // Replace with actual user session management
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        try {
            DatabaseHelper.connectToDatabase();
            // Load user information from the database, including the avatar
            Connection conn = DatabaseHelper.getConnection();
            int userId = getCurrentUserId();
            String query = "SELECT * FROM users WHERE userID = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);
            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                // Set field with data from the database
                usernameField.setText(resultSet.getString("username"));
                fullNameField.setText(resultSet.getString("userFullName"));
                roleField.setText(resultSet.getString("role"));
                emailField.setText(resultSet.getString("gmail"));
                phoneField.setText(resultSet.getString("phoneNumber"));
                dateOfBirthField.setText(resultSet.getDate("dateOfBirth").toString());
                this.userId.setText(String.valueOf(resultSet.getInt("userID")));

                String avatarName = resultSet.getString("avatar");
                String avatarPath;

                // If no custom avatar is set, load the default avatar
                if (avatarName == null || avatarName.isEmpty() || avatarName.equals("userAvatar")) {
                    avatarPath = DEFAULT_AVATAR;
                } else {
                    avatarPath = "src/resources/image/UserAvatar/" + avatarName;
                }

                // Set the avatar image
                File avatarFile = new File(avatarPath);
                if (avatarFile.exists()) {
                    avatar.setFill(new ImagePattern(new Image(avatarFile.toURI().toString())));
                } else {
                    avatar.setFill(new ImagePattern(new Image(DEFAULT_AVATAR)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleBack(ActionEvent actionEvent) {
        loadNewScene("LoginScene", actionEvent);
    }

    // Method to handle setting the avatar
    @FXML
    public void handleSetAvatar(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(setAvatar.getScene().getWindow());

        if (selectedFile != null) {
            try {
                // Define the directory to store user avatars
                String destinationPath = "src/resources/image/UserAvatar/";

                // Create destination directory if it doesn't exist
                File directory = new File(destinationPath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Create a new file in the avatar directory
                String newAvatarName = String.valueOf(getCurrentUserId()) + "_" + selectedFile.getName();
                File destinationFile = new File(directory, newAvatarName);

                // Copy the selected file to the destination
                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Update the avatar in the database
                DatabaseHelper.connectToDatabase();
                Connection conn = DatabaseHelper.getConnection();
                String query = "UPDATE users SET avatar = ? WHERE userID = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, newAvatarName);
                pstmt.setInt(2, getCurrentUserId());
                pstmt.executeUpdate();

                // Set the new avatar in the Circle
                avatar.setFill(new ImagePattern(new Image(destinationFile.toURI().toString())));

            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleChangeProfile(ActionEvent actionEvent) {
        if (!isEditing) {
            // Enable editing of the fields
            fullNameField.setEditable(true);
            phoneField.setEditable(true);
            emailField.setEditable(true);
            dateOfBirthField.setEditable(true);
            changeProfileButton.setText("Save"); // Change button text to 'Save'
            isEditing = true;
        } else {
            // Save the updated fields to the database
            saveProfileChanges();
            changeProfileButton.setText("Change profile"); // Change back to 'Change profile'
            isEditing = false;

            // Disable fields again after saving
            fullNameField.setEditable(false);
            phoneField.setEditable(false);
            emailField.setEditable(false);
            dateOfBirthField.setEditable(false);
        }
    }

    private void saveProfileChanges() {
        DatabaseHelper.connectToDatabase();
        try (Connection conn = DatabaseHelper.getConnection()) {
            int userId = getCurrentUserId(); // Retrieve this from the session or login logic
            String updateQuery = "UPDATE users SET userFullName = ?, phoneNumber = ?, gmail = ?, dateOfBirth = ? WHERE userID = ?";

            PreparedStatement statement = conn.prepareStatement(updateQuery);
            statement.setString(1, fullNameField.getText());
            statement.setString(2, phoneField.getText());
            statement.setString(3, emailField.getText());
            statement.setDate(4, Date.valueOf(dateOfBirthField.getText())); // Ensure the date format is correct
            statement.setInt(5, userId);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleResetPassword() {
        // Step 1: Create custom dialog for username and current password input
        Dialog<Pair<String, String>> loginDialog = new Dialog<>();
        loginDialog.setTitle("Reset Password");
        loginDialog.setHeaderText("Enter your username and current password");

        // Create username and password fields
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Current Password");

        // Layout the fields in a GridPane
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        gridPane.add(new Label("Username:"), 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(new Label("Current Password:"), 0, 1);
        gridPane.add(currentPasswordField, 1, 1);

        loginDialog.getDialogPane().setContent(gridPane);

        // Add OK and Cancel buttons
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        loginDialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Handle button click
        loginDialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return new Pair<>(usernameField.getText(), currentPasswordField.getText());
            }
            return null;
        });

        // Show the dialog and get result
        Optional<Pair<String, String>> loginResult = loginDialog.showAndWait();

        if (loginResult.isPresent()) {
            String username = loginResult.get().getKey();
            String currentPassword = loginResult.get().getValue();

            // Step 2: Check if username and password match
            try (Connection connection = DatabaseHelper.getConnection()) {
                String query = "SELECT * FROM users WHERE username = ? AND hashedPassword = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, username);
                statement.setString(2, currentPassword);

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    // Step 3: Show dialog for new password and confirm password
                    Dialog<Pair<String, String>> passwordDialog = new Dialog<>();
                    passwordDialog.setTitle("New Password");
                    passwordDialog.setHeaderText("Enter new password and confirm password");

                    // Create new password and confirm password fields
                    PasswordField newPasswordField = new PasswordField();
                    newPasswordField.setPromptText("New Password");
                    PasswordField confirmPasswordField = new PasswordField();
                    confirmPasswordField.setPromptText("Confirm Password");

                    // Layout the fields in a GridPane
                    GridPane passwordGrid = new GridPane();
                    passwordGrid.setHgap(10);
                    passwordGrid.setVgap(10);
                    passwordGrid.setPadding(new Insets(20, 150, 10, 10));

                    passwordGrid.add(new Label("New Password:"), 0, 0);
                    passwordGrid.add(newPasswordField, 1, 0);
                    passwordGrid.add(new Label("Confirm Password:"), 0, 1);
                    passwordGrid.add(confirmPasswordField, 1, 1);

                    passwordDialog.getDialogPane().setContent(passwordGrid);

                    // Add OK and Cancel buttons
                    ButtonType passwordOkButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                    passwordDialog.getDialogPane().getButtonTypes().addAll(passwordOkButton, ButtonType.CANCEL);

                    // Handle button click
                    passwordDialog.setResultConverter(dialogButton -> {
                        if (dialogButton == passwordOkButton) {
                            return new Pair<>(newPasswordField.getText(), confirmPasswordField.getText());
                        }
                        return null;
                    });

                    // Show the dialog and get result
                    Optional<Pair<String, String>> passwordResult = passwordDialog.showAndWait();

                    if (passwordResult.isPresent()) {
                        String newPassword = passwordResult.get().getKey();
                        String confirmPassword = passwordResult.get().getValue();

                        // Step 4: Validate new password and confirm password
                        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                            showAlert(Alert.AlertType.ERROR, "Error", "Password fields cannot be empty!");
                        } else if (!newPassword.equals(confirmPassword)) {
                            showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match!");
                        } else {
                            // Step 5: Update the password in the database
                            String updateQuery = "UPDATE users SET hashedPassword = ? WHERE username = ?";
                            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                            updateStatement.setString(1, newPassword);
                            updateStatement.setString(2, username);

                            int rowsUpdated = updateStatement.executeUpdate();

                            if (rowsUpdated > 0) {
                                showAlert(Alert.AlertType.INFORMATION, "Success", "Password reset successfully!");
                            } else {
                                showAlert(Alert.AlertType.ERROR, "Error", "Failed to reset password!");
                            }
                        }
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid username or password!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to reset password!");
            }
        }
    }

    // Show alert dialog
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
