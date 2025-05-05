package cz.cuni.mff.danekji1.calendar.core.session;

import cz.cuni.mff.danekji1.calendar.core.models.User;

import java.net.SocketAddress;

/**
 * Represents a session for a client connected to the server.
 * <p>
 * This class is used to manage the state of a client session, including
 * the client's address, session ID, and the currently logged-in user.
 */
public final class ClientSession  {
    private final SocketAddress clientAddress;
    private final int sessionId;
    private User user;
    private boolean isActive;

    /**
     * Constructor for ClientSession.
     *
     * @param sessionId The unique identifier for the session.
     * @param socketAddress The address of the client.
     */
    public ClientSession(int sessionId, SocketAddress socketAddress) {
        this.sessionId = sessionId;
        this.clientAddress = socketAddress;
        this.isActive = true;
    }

    /**
     * Gets the address of the client.
     *
     * @return The address of the client.
     */
    public SocketAddress getClientAddress() {
        return clientAddress;
    }

    /**
     * Gets the unique identifier assigned for the session.
     *
     * @return The session ID.
     */
    public int getSessionId() {
        return sessionId;
    }

    /**
     * Checks if the client is logged in, unlogged user mean the user is null
     *
     * @return True if the client is logged in, false otherwise.
     */
    public boolean isLoggedIn() {
        return user != null;
    }

    /**
     * Gets the currently logged-in user.
     *
     * @return The currently logged-in user, or null if no user is logged in.
     */
    public User getCurrentUser() {
        return user;
    }

    /**
     * Sets the currently logged-in user.
     *
     * @param user The user to set as the currently logged-in user.
     */
    public void setCurrentUser(User user) {
        assert user != null;
        this.user = user;
    }

    /**
     * Unsets the currently logged-in user.
     * This effectively logs out the user.
     */
    public void unsetCurrentUser() {
        this.user = null;
    }

    /**
     * Checks if the session is active.
     *
     * @return True if the session is active, false otherwise.
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Terminates the session.
     * This sets the session to inactive.
     */
    public void terminate() {
        this.isActive = false;
    }
}
