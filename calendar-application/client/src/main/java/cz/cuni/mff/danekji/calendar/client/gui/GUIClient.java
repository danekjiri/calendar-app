package cz.cuni.mff.danekji.calendar.client.gui;

import cz.cuni.mff.danekji.calendar.client.AbstractClient;
import cz.cuni.mff.danekji.calendar.client.network.NetworkHandler;
import cz.cuni.mff.danekji.calendar.client.network.SocketNetworkHandler;
import javafx.application.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * GUIClient is a JavaFX-based client for the calendar application.
 * It connects to a server, sends commands, and handles responses via a graphical user interface.
 */
public class GUIClient extends AbstractClient {
    private static final Logger LOGGER = LogManager.getLogger(GUIClient.class);

    /**
     * Constructor for GUIClient.
     * It initializes the client with a network handler.
     *
     * @param networkHandler The network handler for managing connections.
     */
    public GUIClient(NetworkHandler networkHandler) {
        super(networkHandler);
    }

    /**
     * The main method to run the GUI client.
     * It accepts server address and port as command-line arguments and initializes the JavaFX application.
     *
     * @param args Command-line arguments where the first argument is the server address and the second is the port.
     */
    public static void main(String[] args) {
        if (args.length > 2 || args.length == 1) {
            LOGGER.error("Usage: mvn javafx:run -Djavafx.args=\"<server-address> <port>\"");
            return;
        }

        final String address = args.length == 0 ? "127.0.0.1" : args[0];
        final int port = args.length == 0 ? 8080 : Integer.parseInt(args[1]);

        GUIClient client = new GUIClient(new SocketNetworkHandler());

        if (!client.connect(address, port)) {
            LOGGER.fatal("Failed to connect to the server. The application will now close.");
            return;
        }

        CalendarGUIApplication.setClient(client);
        Application.launch(CalendarGUIApplication.class, args);
    }

    /**
     * No-op start method.
     * This method is overridden to comply with the AbstractClient contract,
     * but it does not perform any actions as the GUI client is started through JavaFX.
     */
    @Override
    public void start() { }
}