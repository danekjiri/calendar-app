package cz.cuni.mff.danekji1.calendar.core.session;

import cz.cuni.mff.danekji1.calendar.core.models.User;

import java.net.SocketAddress;

public final class ClientSession  {
    private final SocketAddress clientAddress;
    private final int sessionId;
    private User user;
    private boolean isActive;

    public ClientSession(int sessionId, SocketAddress socketAddress) {
        this.sessionId = sessionId;
        this.clientAddress = socketAddress;
        this.isActive = true;
    }

    public SocketAddress getClientAddress() {
        return clientAddress;
    }

    public int getSessionId() {
        return sessionId;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public User getCurrentUser() {
        return user;
    }

    public void setCurrentUser(User user) {
        assert user != null;
        this.user = user;
    }

    public void unsetCurrentUser() {
        this.user = null;
    }

    public boolean isActive() {
        return isActive;
    }

    public void terminate() {
        this.isActive = false;
    }
}
