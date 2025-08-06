package cz.cuni.mff.danekji.calendar.client.gui;

import cz.cuni.mff.danekji.calendar.core.client.Client;
import cz.cuni.mff.danekji.calendar.core.commands.QuitCommand;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The main entry point for the JavaFX application.
 * This class initializes the primary stage and the scene manager.
 */
public class CalendarGUIApplication extends Application {
    private static final Logger LOGGER = LogManager.getLogger(CalendarGUIApplication.class);

    /**
     * Shared client instance for the entire GUI application.
     * This instance is set before launching the application using {@link #setClient(GUIClient)}
     * and is used to communicate with the server.
     */
    private static GUIClient client;

    /**
     * Executor service for running tasks in the background.
     * This is used to handle shutdown tasks without blocking the JavaFX application thread.
     */
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    /**
     * Sets the shared client instance for the entire GUI application.
     * !! This must be called before launching the application.
     * 
     * @param client The connected client instance.
     */
    public static void setClient(GUIClient client) {
        CalendarGUIApplication.client = client;
    }

    /**
     * Gets the shared client instance.
     * @return The client instance.
     */
    public static Client getClient() {
        return client;
    }

    /**
     * This method is called by the JavaFX runtime to start the application.
     * It initializes the primary stage and sets up the scene manager.
     * It sets the title of the primary stage, handles the close request,
     *
     * @param primaryStage the primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        SceneManager sceneManager = new SceneManager(primaryStage, executorService);
        primaryStage.setTitle("Calendar Application");
        primaryStage.initStyle(StageStyle.UNDECORATED); // Hide the window decorations

        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            handleShutdown();
        });

        try {
            sceneManager.showLoginScreen();
            primaryStage.show();
        } catch (IOException e) {
            LOGGER.fatal("Failed to load the initial scene: {}", e.getMessage(), e);
            Platform.exit();
            System.exit(1);
        }
    }

    /**
     * Handles the application shutdown gracefully.
     * It attempts to send a QuitCommand to the server and then forces a shutdown.
     */
    private void handleShutdown() {
        Task<Void> shutdownTask = new Task<>() {
            @Override
            protected Void call() {
                if (client != null && client.isConnectionOpen()) {
                    try {
                        client.sendCommand(new QuitCommand());
                    } catch (Exception e) {
                        LOGGER.warn("Failed to send QuitCommand, likely server is down. Forcing shutdown.", e);
                        forceShutdown();
                    }
                } else {
                    // If not connected, just shutdown.
                    forceShutdown();
                }
                return null;
            }
        };
        executorService.submit(shutdownTask);
    }

    /**
     * Forces the application to shut down by disconnecting the client,
     * shutting down the executor, and exiting the platform.
     */
    private void forceShutdown() {
        if (client != null) {
            client.disconnect();
        }

        executorService.shutdownNow();
        Platform.runLater(Platform::exit);
        System.exit(0);
    }
}