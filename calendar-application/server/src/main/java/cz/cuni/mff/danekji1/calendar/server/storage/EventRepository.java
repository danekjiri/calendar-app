package cz.cuni.mff.danekji1.calendar.server.storage;

import cz.cuni.mff.danekji1.calendar.core.exceptions.server.ServerException;
import cz.cuni.mff.danekji1.calendar.core.exceptions.server.XmlDatabaseException;
import cz.cuni.mff.danekji1.calendar.core.models.Event;
import cz.cuni.mff.danekji1.calendar.core.models.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Repository interface for event persistence.
 */
public interface EventRepository {
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
    void removeEvent(User user, Long eventId);
    List<Event> getAllEvents(User user);
    List<Event> getFutureEvents(User user);
    void updateEvent(User user, Event event);

    default void validateUsersUsername(User user) {
        if (user.username() == null || user.username().isEmpty() || user.username().equals("unlogged")) {
            throw new ServerException("Username cannot be null or empty or 'unlogged'");
        }
    }

    default void validateUserRepositoryLocation(User user) {
        if (!Files.exists(getUserFilePath(user.username()))) {
            throw new XmlDatabaseException("The calendar for username '" + user.username() + "' does not exist");
        }
    }
}
