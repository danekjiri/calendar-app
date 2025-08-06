package cz.cuni.mff.danekji.calendar.client.cli;

import cz.cuni.mff.danekji.calendar.client.AbstractClient;
import cz.cuni.mff.danekji.calendar.client.cli.ui.CLIUserInterface;
import cz.cuni.mff.danekji.calendar.client.network.NetworkHandler;
import cz.cuni.mff.danekji.calendar.client.network.SocketNetworkHandler;
import cz.cuni.mff.danekji.calendar.core.client.Client;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * CLIClient is a command-line interface client for the calendar application.
 * It connects to a server, sends commands, and handles responses via a text-based UI.
 */
public final class CLIClient extends AbstractClient {
    private static final Logger LOGGER = LogManager.getLogger(CLIClient.class);

    private final UserInterface ui;

    /**
     * Constructor for CLIClient.
     *
     * @param ui The user interface to be used by the client.
     * @param networkHandler The network handler for managing connections.
     */
    public CLIClient(UserInterface ui, NetworkHandler networkHandler) {
        super(networkHandler);
        this.ui = ui;
    }

    /**
     * Starts the command-line user interface for the client.
     * This method will block until the user quits, then disconnect.
     */
    @Override
    public void start() {
        ui.start(this);
        disconnect();
    }

    /**
     * The main method to run the CLI client.
     * It accepts server address and port as command-line arguments.
     *
     * @param args Command-line arguments: [server-address] [port]
     */
    public static void main(String[] args) {
        if (args.length > 2 || args.length == 1) {
            LOGGER.error("Usage: mvn exec:java -Dexec.args=\"<server-address> <port>\"");
            return;
        }

        final String address = args.length == 0 ? "127.0.0.1" : args[0];
        final int port = args.length == 0 ? 8080 : Integer.parseInt(args[1]);

        UserInterface ui = new CLIUserInterface(System.in, System.out);
        Client client = new CLIClient(ui, new SocketNetworkHandler());
        if (!client.connect(address, port)) {
            LOGGER.fatal("Connection to server is not open. Please connect to the server first.");
            return;
        }
        client.start();
    }
}