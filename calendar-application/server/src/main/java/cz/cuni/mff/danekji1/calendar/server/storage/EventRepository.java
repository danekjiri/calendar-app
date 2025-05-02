package cz.cuni.mff.danekji1.calendar.server.storage;

import cz.cuni.mff.danekji1.calendar.core.exceptions.server.XmlDatabaseException;
import cz.cuni.mff.danekji1.calendar.core.models.Event;
import cz.cuni.mff.danekji1.calendar.core.models.User;

import java.io.IOException;
import java.util.List;

/**
 * Repository interface for event persistence.
 */
public interface EventRepository {
    /**
     * Creates a new user account with the given username and password hash.
     *
     * @param user the user for which the account will be created
     * @throws XmlDatabaseException if an error occurs while creating the account
     */
    void createAccount(User user) throws XmlDatabaseException;

    /**
     * Authenticates a user with the given username and password hash.
     *
     * @param user the user for which the credentials will be verified
     * @return true if authentication is successful, false otherwise
     */
    boolean authenticate(User user);

    /**
     * Adds an event to the repository.
     *
     * @param user the user for which the given event will be added
     * @param event the event to add
     * @return the ID of the added event
     */
    long addEvent(User user, Event event) throws XmlDatabaseException, IOException;
    void removeEvent(String username, Long eventId);
    List<Event> getAllEvents(String username);
    List<Event> getFutureEvents(String username);
    void updateEvent(String username, Event event);
}
