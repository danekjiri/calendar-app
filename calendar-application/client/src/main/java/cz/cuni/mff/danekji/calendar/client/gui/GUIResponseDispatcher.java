package cz.cuni.mff.danekji.calendar.client.gui;

import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.responses.ResponseVisitor;
import cz.cuni.mff.danekji.calendar.core.responses.error.ErrorResponse;
import cz.cuni.mff.danekji.calendar.core.responses.success.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Handles server responses by updating the GUI on the JavaFX Application Thread.
 */
public class GUIResponseDispatcher implements ResponseVisitor<Void, ClientSession> {
    private static final Logger LOGGER = LogManager.getLogger(GUIResponseDispatcher.class);

    private final SceneManager sceneManager;

    /**
     * Constructor for GUIResponseDispatcher.
     * It initializes the dispatcher with a SceneManager to handle scene transitions.
     *
     * @param sceneManager The SceneManager instance to manage GUI scenes.
     */
    public GUIResponseDispatcher(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * Handles a generic success response by showing an alert and refreshing the event list.
     *
     * @param response The success response to handle.
     * @param session  The current client session.
     * @return null
     */
    @Override
    public Void visit(SuccessResponse response, ClientSession session) {
        showAlert(Alert.AlertType.INFORMATION, "Success", response.message());
        if (sceneManager.getMainController() != null) {
            Platform.runLater(() -> sceneManager.getMainController().handleShowAllEvents());
        }
        return null;
    }

    /**
     * Handles a successful login response by updating the current user and showing the main screen.
     *
     * @param response The success login response to handle.
     * @param session  The current client session.
     * @return null
     */
    @Override
    public Void visit(SuccessLoginResponse response, ClientSession session) {
        session.setCurrentUser(response.user());
        Platform.runLater(() -> {
            try {
                sceneManager.showMainScreen(response.user());
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "UI Error", "Failed to load the main application screen.");
                LOGGER.error("Failed to load the main application screen.", e);
            }
        });
        return null;
    }

    /**
     * Handles a successful logout response by clearing the current user and showing the login screen.
     *
     * @param response The success logout response to handle.
     * @param session  The current client session.
     * @return null
     */
    @Override
    public Void visit(SuccessLogoutResponse response, ClientSession session) {
        session.unsetCurrentUser();
        Platform.runLater(() -> {
            try {
                sceneManager.showLoginScreen();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "UI Error", "Failed to return to the login screen.");
                LOGGER.error("Failed to return to the login screen.", e);
            }
        });
        return null;
    }

    /**
     * Handles a successful event list response by updating the event table in the main controller.
     *
     * @param response The success event list response to handle.
     * @param session  The current client session.
     * @return null
     */
    @Override
    public Void visit(SuccessEventListResponse response, ClientSession session) {
        if (sceneManager.getMainController() != null) {
            Platform.runLater(() -> sceneManager.getMainController().updateEventTable(response.events()));
        }
        return null;
    }

    /**
     * Handles a successful event creation response by showing an alert and refreshing the event list.
     *
     * @param response The success event creation response to handle.
     * @param session  The current client session.
     * @return null
     */
    @Override
    public Void visit(SuccessDeleteUserResponse response, ClientSession session) {
        session.unsetCurrentUser();
        showAlert(Alert.AlertType.INFORMATION, "Account Deleted", response.message());
        Platform.runLater(() -> {
            try {
                sceneManager.showLoginScreen();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "UI Error", "Failed to return to the login screen.");
                LOGGER.error("Failed to return to the login screen.", e);
            }
        });
        return null;
    }

    /**
     * Handles a successful quit response by terminating the session and exiting the application.
     *
     * @param response The success quit response to handle.
     * @param session  The current client session.
     * @return null
     */
    @Override
    public Void visit(SuccessQuit response, ClientSession session) {
        session.terminate();
        Platform.runLater(() -> {
            Platform.exit();
            System.exit(0);
        });
        return null;
    }

    /**
     * Handles an error response by showing an alert with the error message.
     *
     * @param response The error response to handle.
     * @param session  The current client session.
     * @return null
     */
    @Override
    public Void visit(ErrorResponse response, ClientSession session) {
        showAlert(Alert.AlertType.ERROR, "Error", response.errorMessage());
        return null;
    }
}