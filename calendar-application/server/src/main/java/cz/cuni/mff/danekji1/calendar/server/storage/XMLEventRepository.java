package cz.cuni.mff.danekji1.calendar.server.storage;

import cz.cuni.mff.danekji1.calendar.core.exceptions.client.InvalidInputException;
import cz.cuni.mff.danekji1.calendar.core.exceptions.server.ServerException;
import cz.cuni.mff.danekji1.calendar.core.exceptions.server.XmlDatabaseException;
import cz.cuni.mff.danekji1.calendar.core.models.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class XMLEventRepository implements EventRepository {
    private final static Logger LOGGER = LogManager.getLogger(XMLEventRepository.class);

    private static final String CALENDAR_TAG = "calendar";
    private static final String USER_TAG = "user";
    private static final String USERNAME_TAG = "username";
    private static final String PASSWORD_HASH_TAG = "passwordHash";
    private static final String NEXT_EVENT_ID_TAG = "nextEventId";
    private static final String EVENTS_TAG = "events";
    private final static Path XML_FILE_FOLDER = Path.of("./data");

    public XMLEventRepository() throws XmlDatabaseException {
        if (Files.notExists(XML_FILE_FOLDER)) {
            try {
                Files.createDirectories(XML_FILE_FOLDER);
            } catch (IOException e) {
                throw new XmlDatabaseException("Failed to create XML file folder");
            }
        }
    }

    private static Path getUserFilePath(String username) {
        return Path.of(XML_FILE_FOLDER + "/" + username + ".xml");
    }

    /**
     * Creates a new calendar for the user with the given username and password hash.
     * The calendar is stored in an XML file with the username as the filename.
     *
     * @param username The username of the user.
     * @param passwordHash The password hash of the user.
     * @throws InvalidInputException if the username is null, empty, or "unlogged".
     */
    @Override
    public void createAccount(String username, int passwordHash) {
        if (username == null || username.isEmpty() || username.equals("unlogged")) {
            throw new InvalidInputException("Username cannot be null or empty or 'unlogged'");
        }
        if (Files.exists(getUserFilePath(username))) {
            throw new InvalidInputException("The calendar for username '" + username + "' already exists");
        }

        try {
            var file = Files.createFile(getUserFilePath(username));
            Files.writeString(file,
                    """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <calendar>
                       <user>
                            <username>%s</username>
                            <passwordHash>%d</passwordHash>
                       </user>
                       <nextEventId>1</nextEventId>
                       <events></events>
                    </calendar>
                    """.formatted(username, passwordHash)
                    );
            LOGGER.info("Created calendar for user '{}'", username);
        } catch (IOException e) {
            LOGGER.debug("Failed to create calendar for user '{}'", username, e);
            throw new XmlDatabaseException("Failed to create calendar for user '" + username + "'");
        }
    }

    private static Element getUserCalendarRoot(Path file) throws XmlDatabaseException {
        try {
            SAXBuilder builder = new SAXBuilder();
            var document = builder.build(file.toFile());

            if (document == null) {
                throw new XmlDatabaseException("The XML file is empty");
            }
            if (!document.getRootElement().getName().equals(CALENDAR_TAG)) {
                throw new XmlDatabaseException("The XML file is not a valid calendar");
            }

            return document.getRootElement();
        } catch (Exception e) {
            LOGGER.debug("Failed to create SAX parser", e);
            throw new XmlDatabaseException("Failed to create SAX parser");
        }
    }

    /**
     * Authenticates a user with the given username and password hash.
     * The method checks if the username exists and if the password hash matches.
     *
     * @param username The username of the user.
     * @param passwordHash The password hash of the user.
     * @return true if authentication is successful, false otherwise.
     */
    @Override
    public synchronized boolean authenticate(String username, int passwordHash) {
        if (username == null || username.isEmpty() || username.equals("unlogged")) {
            LOGGER.debug("Username cannot be null or empty or 'unlogged'");
            return false;
        }

        if (!Files.exists(getUserFilePath(username))) {
            LOGGER.debug("The calendar for username '{}' does not exist", username);
            return false;
        }

        var rootCalendar = getUserCalendarRoot(getUserFilePath(username));
        var user = rootCalendar.getChild(USER_TAG);
        String usernameFromFile = user.getChildText(USERNAME_TAG);
        int passwordHashFromFile = Integer.parseInt(user.getChildText(PASSWORD_HASH_TAG));

        return username.equals(usernameFromFile) && passwordHashFromFile == passwordHash;
    }

    /**
     * Adds an event to the user's calendar.
     * The method increments <nextEventId></nextEventId> XML elements value and adds the event with it into the XML file.
     *
     * @param username The username of the user.
     * @param event The event to add.
     * @return The ID of the added event.
     */
    @Override
    public long addEvent(String username, Event event) {
        if (username == null || username.isEmpty() || username.equals("unlogged")) {
            throw new ServerException("Username cannot be null or empty or 'unlogged'");
        }

        if (!Files.exists(getUserFilePath(username))) {
            throw new XmlDatabaseException("The calendar for username '" + username + "' does not exist");
        }

        var rootCalendar = getUserCalendarRoot(getUserFilePath(username));
        var nextEventIdElement = rootCalendar.getChild(NEXT_EVENT_ID_TAG);
        long nextEventId = Long.parseLong(nextEventIdElement.getText());

        Event eventWithId = Event.withId(nextEventId, event);
        nextEventIdElement.setText(String.valueOf(nextEventId + 1));

        var eventsElement = rootCalendar.getChild(EVENTS_TAG);
        eventsElement.addContent(eventWithId.toXML());

        // todo: save the XML file
        return event.getId();
    }

    @Override
    public void removeEvent(String username, Long eventId) {
        throw new UnsupportedOperationException("Not implemented yet");

    }

    @Override
    public List<Event> getAllEvents(String username) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Event> getFutureEvents(String username) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void updateEvent(String username, Event event) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
