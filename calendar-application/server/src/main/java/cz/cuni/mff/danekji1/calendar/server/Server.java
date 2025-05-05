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
import cz.cuni.mff.danekji1.calendar.core.session.ClientSession;
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

    private static final int SESSION_TIMEOUT = 3 * 60 * 1000; // 3 minutes
    private static final Random RANDOM = new Random(42);
    private final Map<Integer, ClientSession> sessions = Collections.synchronizedMap(new HashMap<>());
    private final CommandVisitor<Response, ClientSession> commandDispatcher;

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
        try {
            configureSocket(clientSocket);
        } catch (SocketException e) {
            LOGGER.error("Client session '{}' encountered a socket error.", session.getSessionId(), e);
            sessions.remove(session.getSessionId());
            return;
        }

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
     * Configures the client socket with a timeout.
     */
    private void configureSocket(Socket clientSocket) throws SocketException {
        clientSocket.setSoTimeout(SESSION_TIMEOUT);
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
                LOGGER.info("Client session '{}' received command '{}'", session.getSessionId(), command);

                Response response = command.accept(commandDispatcher, session);
                out.writeObject(response);
                out.flush();
            } catch (SocketTimeoutException e) {
                LOGGER.info("Session '{}' timed out after '{} seconds' inactivity. Closing connection...", session.getSessionId(), SESSION_TIMEOUT/1000);
                return;
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
     */
    private boolean isSessionValid(int sessionId) {
        ClientSession session = sessions.get(sessionId);
        return session != null && session.isActive();
    }

    public static void main(String[] args) {
        Server server = new Server(new XMLEventRepository());
        server.start(8080);
    }
}