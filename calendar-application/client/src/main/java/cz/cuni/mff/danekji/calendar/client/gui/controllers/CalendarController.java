package cz.cuni.mff.danekji.calendar.client.gui.controllers;

import cz.cuni.mff.danekji.calendar.client.gui.GUIResponseDispatcher;
import cz.cuni.mff.danekji.calendar.client.gui.CalendarGUIApplication;
import cz.cuni.mff.danekji.calendar.client.gui.SceneManager;
import cz.cuni.mff.danekji.calendar.core.commands.Command;
import cz.cuni.mff.danekji.calendar.core.responses.Response;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

import java.util.concurrent.ExecutorService;

/**
 * Abstract base class for all controllers in the Calendar GUI application.
 * Provides common functionality for sending commands and showing alerts.
 */
public abstract class CalendarController {

    SceneManager sceneManager;
    private ExecutorService executorService;
    private GUIResponseDispatcher responseDispatcher;

    /**
     * Initializes the controller with the SceneManager and ExecutorService.
     * This method should be called by the specific controller after it is created.
     *
     * @param sceneManager The SceneManager instance to manage GUI scenes.
     * @param executorService The ExecutorService for running tasks in the background.
     */
    public void initManager(SceneManager sceneManager, ExecutorService executorService) {
        this.sceneManager = sceneManager;
        this.responseDispatcher = new GUIResponseDispatcher(sceneManager);
        this.executorService = executorService;
    }

    /**
     * Sends a command to the server using the client instance.
     * This method runs the command in a background task to avoid blocking the JavaFX application thread.
     */
    protected void sendCommand(Command command) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Response response = CalendarGUIApplication.getClient().sendCommand(command);
                response.accept(responseDispatcher, CalendarGUIApplication.getClient().getCurrentSession());
                return null;
            }
        };
        executorService.submit(task);
    }

    /**
     * Shows an alert with the specified title and message.
     * This method is used to display information or error messages to the user.
     *
     * @param title The title of the alert.
     * @param message The message content of the alert.
     */
    protected void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}