package com.tesco.calendar.server;

import com.tesco.calendar.core.models.User;

/**
 * Maintains session state for each client.
 */
public class Session {
    private User currentUser;

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}
