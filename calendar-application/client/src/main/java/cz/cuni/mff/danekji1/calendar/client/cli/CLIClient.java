package cz.cuni.mff.danekji1.calendar.client.cli;

import cz.cuni.mff.danekji1.calendar.client.cli.ui.CLIUserInterface;
import cz.cuni.mff.danekji1.calendar.core.session.ClientSession;
import cz.cuni.mff.danekji1.calendar.core.commands.Command;
import cz.cuni.mff.danekji1.calendar.core.responses.Response;
import cz.cuni.mff.danekji1.calendar.core.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import cz.cuni.mff.danekji1.calendar.core.ui.UserInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Command Line Interface (CLI) client for the calendar application.
 * This class handles the connection to the server and user network interaction.
 */
public final class CLIClient implements Client {
    private static final Logger LOGGER = LogManager.getLogger(CLIClient.class);

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final UserInterface ui;
    private ClientSession session;

    /**
     * Constructor for CLIClient.
     * Initializes the user interface.
     *
     * @param ui User interface for the client (e.g., {@link CLIUserInterface}).
     */
    public CLIClient(UserInterface ui) {
        this.ui = ui;
    }

    /**
     * Checks if the connection to the server is open.
     *
     * @return true if the connection is open, false otherwise.
     */
    public boolean isConnectionOpen() {
        return socket != null &&  socket.isConnected() && !socket.isClosed();
    }

    /**
     * Gets the current session associated with this client.
     *
     * @return The current {@link ClientSession}.
     */
    @Override
    public ClientSession getCurrentSession() {
        assert session != null;
        return session;
    }

    /**
     * Connects to the server at the specified host and port.
     * This method creates a socket connection and initializes input/output streams.
     * It also retrieves the session ID from the server and store it in current session.
     *
     * @param host The hostname or IP address of the server.
     * @param port The port number on which the server is listening.
     */
    public void connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            int sessionId = (int) in.readObject();
            session = new ClientSession(sessionId, new InetSocketAddress(host, port));
            LOGGER.info("Connected to '{}:{}' with sessionId '{}'", host, port, sessionId);
        } catch (Exception e) {
            LOGGER.fatal("Failed to connect to '{}:{}'", host, port, e);
        }
    }

    /**
     * Sends a command to the server and receives the response.
     * This method serializes the command object and sends it over the network.
     *
     * @param command The command to be sent to the server.
     * @throws ClassNotFoundException If the class of a serialized object cannot be found.
     * @throws IOException If an I/O error occurs during the communication.
     */
    public Response sendCommand(Command command) throws ClassNotFoundException, IOException {
        LOGGER.debug("Sending command '{}'", command);

        out.writeObject(command);
        out.flush();

        return (Response) in.readObject();
    }

    /**
     * Starts the client user interface.
     * This method initializes the user interface and starts listening for user commands.
     * It also checks if the connection to the server is open before starting the UI.
     */
    public void start() {
        if (!isConnectionOpen()) {
            LOGGER.fatal("Connection to server is not open. Please connect to the server first.");
            return;
        }

        ui.start(this);
    }

    /**
     * Main method to run the client.
     * @param args unused
     */
    public static void main(String[] args) {
        UserInterface ui = new CLIUserInterface(System.in, System.out);

        Client client = new CLIClient(ui);
        client.connect("127.0.0.1", 8080);
        client.start();
    }
}
