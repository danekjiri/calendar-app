package cz.cuni.mff.danekji1.calendar.client.cli;

import cz.cuni.mff.danekji1.calendar.core.models.User;
import cz.cuni.mff.danekji1.calendar.core.ui.ClientState;

public class ClientSession implements ClientState {
    private User user;
    private final int sessionId;

    public ClientSession(int sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public int getSessionId() {
        return sessionId;
    }

    @Override
    public boolean isLoggedIn() {
        return user != null;
    }

    @Override
    public User getCurrentUser() {
        return user;
    }

    @Override
    public void setCurrentUser(User user) {
        assert user != null;
        this.user = user;
    }

    @Override
    public void unsetCurrentUser() {
        this.user = null;
    }
}
