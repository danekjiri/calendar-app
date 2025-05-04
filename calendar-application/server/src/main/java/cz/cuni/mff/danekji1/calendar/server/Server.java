package cz.cuni.mff.danekji1.calendar.server;

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

import cz.cuni.mff.danekji1.calendar.core.commands.CommandVisitor;
import cz.cuni.mff.danekji1.calendar.core.responses.error.ErrorResponse;
import cz.cuni.mff.danekji1.calendar.core.session.ClientSession;
import cz.cuni.mff.danekji1.calendar.core.session.Session;
import cz.cuni.mff.danekji1.calendar.server.storage.XMLEventRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.cuni.mff.danekji1.calendar.server.storage.EventRepository;
import cz.cuni.mff.danekji1.calendar.core.commands.Command;
import cz.cuni.mff.danekji1.calendar.core.responses.Response;

/**
 * Main server class that manages client connections and dispatches commands.
 */
public class Server {
    private static final Logger LOGGER = LogManager.getLogger(Server.class.getName());

    private static final Random RANDOM = new Random(42);
    private final Map<Integer, Session> sessions = Collections.synchronizedMap(new HashMap<>());
    private final CommandVisitor<Response, Session> commandDispatcher;

    public Server(EventRepository eventRepository) {
        this.commandDispatcher = new DefaultCommandDispatcher(eventRepository);
        LOGGER.info("Server initialized.");
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
     */
    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port);
             ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            LOGGER.info("Server started on port {}", port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                int sessionId = getUniqueSessionId();
                var prev = sessions.put(sessionId, new ClientSession(sessionId));
                assert prev == null;

                executor.submit(() -> handleClient(clientSocket, sessionId));
            }
        } catch (Exception e) {
            LOGGER.fatal("Failed to start server on port '{}', because of '{}'", port, e);
        }
    }

    /**
     * Handles client connections in a separate thread.
     * Sets up the connection, processes commands, and ensures proper cleanup.
     */
    private void handleClient(Socket clientSocket, int sessionId) {
        try {
            configureSocket(clientSocket);
        } catch (SocketException e) {
            LOGGER.error("Failed to configure socket for session {}: {}", sessionId, e.getMessage());
            sessions.remove(sessionId);
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {
            sendSessionId(out, clientSocket, sessionId);
            runSessionLoop(in, out, sessionId);
        } catch (IOException e) {
            LOGGER.error("IO error in client handler for session {}: {}", sessionId, e.getMessage());
        } finally {
            sessions.remove(sessionId);
            try {
                clientSocket.close();
            } catch (IOException e) {
                LOGGER.error("Error closing client socket for session {}: {}", sessionId, e.getMessage());
            }
            LOGGER.info("Session {} terminated and removed from sessions map.", sessionId);
        }
    }

    /**
     * Configures the client socket with a timeout.
     */
    private void configureSocket(Socket clientSocket) throws SocketException {
        clientSocket.setSoTimeout(3 * 60 * 1000); // 3 minutes
    }

    /**
     * Sends the session ID to the client and logs the connection.
     */
    private void sendSessionId(ObjectOutputStream out, Socket clientSocket, int sessionId) throws IOException {
        out.writeObject(sessionId);
        out.flush();
        LOGGER.info("Client connected: '{}' with sessionId '{}'", clientSocket.getRemoteSocketAddress(), sessionId);
    }

    /**
     * Runs the loop to process commands while the session is valid.
     */
    private void runSessionLoop(ObjectInputStream in, ObjectOutputStream out, int sessionId) {
        while (isSessionValid(sessionId)) {
            Session session = sessions.get(sessionId);
            try {
                Command command = (Command) in.readObject();
                LOGGER.debug("Received command: {}", command);

                Response response = command.accept(commandDispatcher, session);
                out.writeObject(response);
                out.flush();
                LOGGER.debug("Sent response: {}", response);
            } catch (SocketTimeoutException e) {
                LOGGER.info("Session {} timed out after inactivity.", session.getSessionId());
                return;
            } catch (EOFException e) {
                LOGGER.info("Client disconnected for session {}.", session.getSessionId());
                return;
            } catch (Exception e) {
                LOGGER.error("Error handling client for session {}: {}", session.getSessionId(), e.getMessage());
                return;
            }
        }
    }

    /**
     * Checks if the session is still valid (present in the map and active).
     */
    private boolean isSessionValid(int sessionId) {
        Session session = sessions.get(sessionId);
        return session != null && session.isActive();
    }

    public static void main(String[] args) {
        Server server = new Server(new XMLEventRepository());
        server.start(8080);
    }
}