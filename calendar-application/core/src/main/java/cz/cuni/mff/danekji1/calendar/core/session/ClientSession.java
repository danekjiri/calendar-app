package cz.cuni.mff.danekji1.calendar.core.session;

import cz.cuni.mff.danekji1.calendar.core.models.User;

public class ClientSession implements Session {
    private User user;
    private final int sessionId;
    private boolean isActive;

    public ClientSession(int sessionId) {
        this.sessionId = sessionId;
        this.isActive = true;
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

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void deactivate() {
        this.isActive = false;
    }
}
