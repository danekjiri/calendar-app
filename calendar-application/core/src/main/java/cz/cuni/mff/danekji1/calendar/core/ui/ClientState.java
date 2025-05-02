package cz.cuni.mff.danekji1.calendar.core.ui;

import cz.cuni.mff.danekji1.calendar.core.models.User;

public interface ClientState {
    boolean isLoggedIn();                  // Check if a user is logged in
    User getCurrentUser();                 // Get the current user (nullable if not logged in)
    void unsetCurrentUser();              // Unset the current user
    void setCurrentUser(User user);
    int getSessionId();                // Get the session ID
}