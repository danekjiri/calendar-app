package cz.cuni.mff.danekji.calendar.client.gui;

import cz.cuni.mff.danekji.calendar.client.gui.controllers.LoginController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * Manages scene transitions within the JavaFX application.
 */
public class SceneManager {
    private static final Logger LOGGER = LogManager.getLogger(SceneManager.class);

    private final Stage primaryStage;
    private final ExecutorService executorService;

    public SceneManager(Stage primaryStage, ExecutorService executorService) {
        this.primaryStage = primaryStage;
        this.executorService = executorService;
    }

    /**
     * Loads and displays the login screen.
     *
     * @throws IOException if the FXML file cannot be loaded.
     */
    public void showLoginScreen() throws IOException {
        FXMLLoader loader = createLoader("/views/LoginView.fxml");
        Parent root = loader.load();
        LoginController controller = loader.getController();
        controller.initManager(this, executorService);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Calendar - Login");
    }

    private FXMLLoader createLoader(String fxmlPath) {
        URL resourceUrl = getClass().getResource(fxmlPath);
        if (resourceUrl == null) {
            throw new IllegalArgumentException("Cannot find FXML file at: " + fxmlPath);
        }
        return new FXMLLoader(resourceUrl);
    }
}