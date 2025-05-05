package cz.cuni.mff.danekji1.calendar.server.storage;

import cz.cuni.mff.danekji1.calendar.core.exceptions.server.ServerException;
import cz.cuni.mff.danekji1.calendar.core.exceptions.server.XmlDatabaseException;
import cz.cuni.mff.danekji1.calendar.core.models.Event;
import cz.cuni.mff.danekji1.calendar.core.models.User;
import cz.cuni.mff.danekji1.calendar.core.session.ClientSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Repository interface for event persistence.
 */
public interface EventRepository {
    Logger LOGGER = LogManager.getLogger(EventRepository.class);
    Path XML_FILE_FOLDER = Path.of("./data");

    default Path getUserFilePath(String username) {
        return Path.of(XML_FILE_FOLDER + "/" + username + ".xml");
    }

    /**
     * Creates a new user account with the given username and password hash.
     *
     * @param user the user for which the account will be created
     * @throws XmlDatabaseException if an error occurs while creating the account
     */
    void createAccount(User user, ClientSession session) throws XmlDatabaseException;

    /**
     * Authenticates a user with the given username and password hash.
     *
     * @param user the user for which the credentials will be verified
     * @return true if authentication is successful, false otherwise
     */
    boolean authenticate(User user, ClientSession session);

    /**
     * Adds an event to the repository.
     *
     * @param user the user for which the given event will be added
     * @param event the event to add
     * @return the ID of the added event
     */
    long addEvent(User user, Event event, ClientSession session) throws XmlDatabaseException, IOException;
    void deleteEvent(User user, Long eventId, ClientSession session);
    List<Event> getAllEvents(User user, ClientSession session);
    void updateEvent(User user, Event event, ClientSession session);

    default void validateUsersUsername(User user, ClientSession session) {
        if (user.username() == null || user.username().isEmpty() || user.username().equals("unlogged")) {
            LOGGER.error("Client session '{}': Passed an invalid username: {}", session.getSessionId(), user.username());
            throw new ServerException("Username cannot be null or empty or 'unlogged'");
        }
    }

    default void validateUserRepositoryLocation(User user) {
        if (!Files.exists(getUserFilePath(user.username()))) {
            LOGGER.error("User calendar file for username '{}' does not exist", user.username());
            throw new XmlDatabaseException("The calendar for username '" + user.username() + "' does not exist");
        }
    }
}
