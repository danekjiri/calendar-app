package cz.cuni.mff.danekji.calendar.server.storage;

import cz.cuni.mff.danekji.calendar.core.exceptions.server.ServerException;
import cz.cuni.mff.danekji.calendar.core.exceptions.server.XmlDatabaseException;
import cz.cuni.mff.danekji.calendar.core.models.Event;
import cz.cuni.mff.danekji.calendar.core.models.User;
import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

/**
 * Repository interface for calendars persistence.
 */
public interface EventRepository {
    /**
     * Logger for the EventRepository class.
     */
    Logger LOGGER = LogManager.getLogger(EventRepository.class);

    /**
     * Creates a new user account with the given username and password hash.
     *
     * @param user the user for which the account will be created
     * @param session the client session
     * @throws XmlDatabaseException if an error occurs while creating the account
     */
    void createAccount(User user, ClientSession session) throws XmlDatabaseException;

    /**
     * Authenticates a user with the given username and password hash.
     *
     * @param user the user for which the credentials will be verified
     * @param session the client session
     * @return true if authentication is successful, false otherwise
     */
    boolean authenticate(User user, ClientSession session);

    /**
     * Adds an event to the repository.
     *
     * @param user the user for which the given event will be added
     * @param event the event to add
     * @param session the client session
     * @return the ID of the added event
     * @throws XmlDatabaseException if an error occurs while adding the event
     * @throws IOException if an I/O error occurs
     */
    long addEvent(User user, Event event, ClientSession session) throws XmlDatabaseException, IOException;

    /**
     * Deletes an event from the repository.
     *
     * @param user the user for which the given event will be deleted
     * @param eventId the ID of the event to delete
     * @param session the client session
     */
    void deleteEvent(User user, Long eventId, ClientSession session);

    /**
     * Retrieves all events for the given user.
     *
     * @param user the user for which to retrieve events
     * @param session the client session
     * @return a list of events for the user
     */
    List<Event> getAllEvents(User user, ClientSession session);

    /**
     * Updates an event in the repository.
     *
     * @param user the user for which the event will be updated
     * @param event the event to update
     * @param session the client session
     */
    void updateEvent(User user, Event event, ClientSession session);

    /**
     * Validates the username of the user.
     *
     * @param user the user to validate
     * @param session the client session
     * @throws ServerException if the username is invalid
     */
    default void validateUsersUsername(User user, ClientSession session) {
        if (user.username() == null || user.username().isEmpty() || user.username().equals("unlogged")) {
            LOGGER.error("Client session '{}': Passed an invalid username: {}", session.getSessionId(), user.username());
            throw new ServerException("Username cannot be null or empty or 'unlogged'");
        }
    }

    /**
     * Deletes a user's account and all associated data.
     *
     * @param user The user to delete, including credentials for verification.
     * @param session The client session.
     * @throws XmlDatabaseException if the user does not exist or deletion fails.
     * @throws IOException if an I/O error occurs.
     */
    void deleteUser(User user, ClientSession session) throws XmlDatabaseException, IOException;
}
