package cz.cuni.mff.danekji.calendar.client.cli;

import cz.cuni.mff.danekji.calendar.client.cli.ui.CLIUserInterface;
import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.commands.Command;
import cz.cuni.mff.danekji.calendar.core.responses.Response;
import cz.cuni.mff.danekji.calendar.core.client.Client;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * CLIClient is a command-line interface client for the calendar application.
 * It connects to a server, sends commands, and handles responses.
 */
public final class CLIClient implements Client {
    private static final Logger LOGGER = LogManager.getLogger(CLIClient.class);

    private final NetworkHandler networkHandler;
    private final UserInterface ui;
    private ClientSession session;

    /**
     * Constructor for CLIClient.
     * Initializes the user interface and network handler.
     *
     * @param ui The user interface to be used by the client
     * @param networkHandler The network handler for managing connections
     */
    public CLIClient(UserInterface ui, NetworkHandler networkHandler) {
        this.ui = ui;
        this.networkHandler = networkHandler;
    }

    /**
     * Connects to the server at the specified host and port.
     * Initializes a new client session upon successful connection.
     *
     * @param host The host name or IP address of the server
     * @param port The port number of the server
     */
    @Override
    public void connect(String host, int port) {
        try {
            int sessionId = networkHandler.connect(host, port);
            session = new ClientSession(sessionId, new InetSocketAddress(host, port));
            LOGGER.info("Connected to '{}:{}' with sessionId '{}'", host, port, sessionId);
        } catch (IOException e) {
            LOGGER.fatal("Failed to connect to '{}:{}' - {}", host, port, e.getMessage());
        } catch (ClassNotFoundException e) {
            LOGGER.fatal("Failed to process session ID from '{}:{}'", host, port, e);
        }
    }

    /**
     * Sends a command to the server and returns the response.
     *
     * @param command The command to be sent
     * @return The response from the server
     * @throws IOException if an I/O error occurs while sending the command
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     */
    @Override
    public Response sendCommand(Command command) throws IOException, ClassNotFoundException {
        LOGGER.debug("Sending command '{}'", command);
        return networkHandler.sendCommand(command);
    }

    /**
     * Checks if the connection to the server is open.
     *
     * @return true if the connection is open, false otherwise
     */
    @Override
    public boolean isConnectionOpen() {
        return networkHandler.isConnected();
    }

    /**
     * Gets the current client session.
     *
     * @return The current client session
     */
    @Override
    public ClientSession getCurrentSession() {
        assert session != null;
        return session;
    }

    /**
     * Starts the user interface for the client.
     * This method will block until the user interface is closed or the connection is lost.
     */
    @Override
    public void start() {
        if (!isConnectionOpen()) {
            LOGGER.fatal("Connection to server is not open. Please connect to the server first.");
            return;
        }
        ui.start(this);
    }

    /**
     * Disconnects the client from the server.
     * This method will close the network connection and clean up resources.
     */
    public void disconnect() {
        try {
            networkHandler.disconnect();
        } catch (IOException e) {
            LOGGER.error("Failed to disconnect", e);
        }
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
        client.connect(address, port);
        client.start();
    }
}