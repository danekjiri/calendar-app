package cz.cuni.mff.danekji1.calendar.core.session;

import cz.cuni.mff.danekji1.calendar.core.models.User;

public interface Session {
    boolean isLoggedIn();                  // Check if a user is logged in
    User getCurrentUser();                 // Get the current user (nullable if not logged in)
    void unsetCurrentUser();              // Unset the current user
    void setCurrentUser(User user);
    int getSessionId();                // Get the session ID
    boolean isActive();                  // Check if the session is active
    void deactivate();                   // Terminate the session
}