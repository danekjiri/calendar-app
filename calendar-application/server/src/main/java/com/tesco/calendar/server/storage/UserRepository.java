package com.tesco.calendar.server.storage;

import com.tesco.calendar.core.models.User;

/**
 * Repository interface for user authentication and creation.
 */
public interface UserRepository {
    /**
     * Authenticate user credentials.
     * Return a User if authentication is successful.
     * Otherwise, null.
     */
    User authenticate(String username, String password);

    void createUser(String username, String password);
}
