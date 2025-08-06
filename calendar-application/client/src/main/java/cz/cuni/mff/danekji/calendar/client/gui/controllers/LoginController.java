package cz.cuni.mff.danekji.calendar.client.gui.controllers;

import cz.cuni.mff.danekji.calendar.core.commands.CreateAccountCommand;
import cz.cuni.mff.danekji.calendar.core.commands.LoginCommand;
import cz.cuni.mff.danekji.calendar.core.commands.QuitCommand;
import cz.cuni.mff.danekji.calendar.core.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller for the LoginView.fxml screen.
 */
public class LoginController extends CalendarController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    /**
     * Default constructor for LoginController.
     * Needed for JavaFX FXML loading.
     */
    public LoginController() {}

    /**
     * Handles the login action when the login button is clicked.
     * It retrieves the user information from the input fields and sends a LoginCommand.
     */
    @FXML
    private void handleLogin() {
        User user = getUserFromFields();
        if (user != null) {
            sendCommand(new LoginCommand(user));
        } else {
            showAlert("Login Error", "Username and password cannot be empty.");
        }
    }

    /**
     * Handles the account creation action when the create account button is clicked.
     * It retrieves the user information from the input fields and sends a CreateAccountCommand.
     */
    @FXML
    private void handleCreateAccount() {
        User user = getUserFromFields();
        if (user != null) {
            sendCommand(new CreateAccountCommand(user));
        } else {
            showAlert("Account Creation Error", "Username and password cannot be empty.");
        }
    }

    /**
     * Handles the quit action when the quit button is clicked.
     * It sends a QuitCommand to terminate the application.
     */
    @FXML
    private void handleQuit() {
        sendCommand(new QuitCommand());
    }

    /**
     * Retrieves the user information from the input fields.
     * If either field is empty, it returns null.
     */
    private User getUserFromFields() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            return null;
        }
        return new User(username, password.hashCode());
    }
}