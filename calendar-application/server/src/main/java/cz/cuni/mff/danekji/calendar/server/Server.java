package cz.cuni.mff.danekji.calendar.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cz.cuni.mff.danekji.calendar.core.commands.CommandVisitor;
import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.server.storage.XMLEventRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.danekji.calendar.server.storage.EventRepository;
import cz.cuni.mff.danekji.calendar.core.commands.Command;
import cz.cuni.mff.danekji.calendar.core.responses.Response;

/**
 * Main server class that manages client connections and dispatches commands.
 */
public class Server {
    private static final Logger LOGGER = LogManager.getLogger(Server.class.getName());

    private static final Random RANDOM = new Random(42);
    private final Map<Integer, ClientSession> sessions = Collections.synchronizedMap(new HashMap<>());
    private final CommandVisitor<Response, ClientSession> commandDispatcher;

    /**
     * Constructor for the server.
     * Initializes the command dispatcher with the provided event repository.
     *
     * @param eventRepository The event repository to be used for storing events.
     */
    public Server(EventRepository eventRepository) {
        this.commandDispatcher = new DefaultCommandDispatcher(eventRepository);
        LOGGER.info("Server initialized with event repository: {}", eventRepository.getClass().getSimpleName());
    }

    /**
     * Creates a new session for a client.
     * Each session is identified by a unique session ID.
     */
    private synchronized int getUniqueSessionId() {
        int sessionId;
        do {
            sessionId = RANDOM.nextInt(Integer.MAX_VALUE);
        } while (sessions.containsKey(sessionId));
        return sessionId;
    }

    /**
     * Starts the server and listens for incoming connections.
     * Each connection is handled in a separate thread.
     *
     * @param port The port on which the server will listen for incoming connections.
     */
    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port);
             ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            LOGGER.info("Server started on port {}", port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                int sessionId = getUniqueSessionId();
                var newSession = new ClientSession(sessionId, clientSocket.getRemoteSocketAddress());
                var retrievedSession = sessions.put(sessionId, newSession);
                assert retrievedSession == null;

                executor.submit(() -> handleClient(clientSocket, newSession));
            }
        } catch (Exception e) {
            LOGGER.fatal("Failed to start server on port '{}'", port, e);
        }
    }

    /**
     * Handles client connections in a separate thread.
     * Sets up the connection, processes commands, and ensures proper cleanup.
     */
    private void handleClient(Socket clientSocket, ClientSession session) {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {
            sendSessionId(out, session);
            runSessionLoop(in, out, session);
        } catch (IOException e) {
            LOGGER.error("Client session '{}' encountered an IO error.", session.getSessionId(), e);
        } finally {
            sessions.remove(session.getSessionId());
            try {
                clientSocket.close();
            } catch (IOException e) {
                LOGGER.error("Client session '{}' failed to close socket.", session.getSessionId(), e);
            }
            LOGGER.info("Client session '{}': Terminated and removed from sessions map.", session.getSessionId());
        }
    }

    /**
     * Sends the session ID to the client and logs the connection.
     */
    private void sendSessionId(ObjectOutputStream out, ClientSession session) throws IOException {
        out.writeObject(session.getSessionId());
        out.flush();
        LOGGER.info("Client address '{}' connected with sessionId '{}'", session.getClientAddress(), session.getSessionId());
    }

    /**
     * Runs the loop to process commands while the session is valid.
     */
    private void runSessionLoop(ObjectInputStream in, ObjectOutputStream out, ClientSession session) throws IOException {
        while (isSessionValid(session.getSessionId())) {
            try {
                Command command = (Command) in.readObject();
                LOGGER.info("Client session '{}': Received command '{}'.", session.getSessionId(), command);

                Response response = command.accept(commandDispatcher, session);
                out.writeObject(response);
                out.flush();
            } catch (EOFException e) {
                LOGGER.info("Client session '{}' closed the connection.", session.getSessionId());
                return;
            } catch (Exception e) {
                LOGGER.error("Client session '{}' encountered an unexpected error.", session.getSessionId(), e);
                return;
            }
        }
    }

    /**
     * Checks if the session is still valid (present in the map and active).
     *
     * @param sessionId The ID of the session to check.
     * @return true if the session is valid, false otherwise.
     */
    private boolean isSessionValid(int sessionId) {
        ClientSession session = sessions.get(sessionId);
        return session != null && session.isActive();
    }

    /**
     * Main method to start the server.
     * Initializes the server with an XML event repository and starts listening on the specified port.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        if (args.length > 1) {
            LOGGER.error("Usage: mvn exec:java -Dexec.args=\"<port>\"");
            return;
        }

        final int port = args.length == 0 ? 8080 : Integer.parseInt(args[0]);

        Server server = new Server(new XMLEventRepository());
        server.start(port);
    }
}