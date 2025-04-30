package cz.cuni.mff.danekji1.calendar.server.storage;

import cz.cuni.mff.danekji1.calendar.core.exceptions.server.XmlDatabaseException;
import cz.cuni.mff.danekji1.calendar.core.models.Event;
import java.util.List;

/**
 * Repository interface for event persistence.
 */
public interface EventRepository {
    /**
     * Creates a new user account with the given username and password hash.
     *
     * @param username the username of the new user
     * @param passwordHash the hash of the user's password
     * @throws XmlDatabaseException if an error occurs while creating the account
     */
    void createAccount(String username, int passwordHash) throws XmlDatabaseException;

    /**
     * Authenticates a user with the given username and password hash.
     *
     * @param username the username of the user
     * @param passwordHash the hash of the user's password
     * @return true if authentication is successful, false otherwise
     */
    boolean authenticate(String username, int passwordHash);

    /**
     * Adds an event to the repository.
     *
     * @param username the username of the user
     * @param event the event to add
     * @return the ID of the added event
     */
    long addEvent(String username, Event event);
    void removeEvent(String username, Long eventId);
    List<Event> getAllEvents(String username);
    List<Event> getFutureEvents(String username);
    void updateEvent(String username, Event event);
}
