package cz.cuni.mff.danekji1.calendar.server.storage;

import cz.cuni.mff.danekji1.calendar.core.exceptions.client.InvalidInputException;
import cz.cuni.mff.danekji1.calendar.core.exceptions.server.XmlDatabaseException;
import cz.cuni.mff.danekji1.calendar.core.models.Event;
import cz.cuni.mff.danekji1.calendar.core.models.User;
import cz.cuni.mff.danekji1.calendar.core.session.ClientSession;
import cz.cuni.mff.danekji1.calendar.core.xml.XMLCalendarTags;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class XMLEventRepository implements EventRepository {
    private final static Logger LOGGER = LogManager.getLogger(XMLEventRepository.class);

    public XMLEventRepository() throws XmlDatabaseException {
        if (Files.notExists(XML_FILE_FOLDER)) {
            try {
                Files.createDirectories(XML_FILE_FOLDER);
                LOGGER.info("Created XML file folder '{}'", XML_FILE_FOLDER.toAbsolutePath());
            } catch (IOException e) {
                LOGGER.error("Could not create XML file folder '{}': {}", XML_FILE_FOLDER.toAbsolutePath(), e.getMessage());
                throw new XmlDatabaseException("FATAL ERROR: Failed to create XML file folder.");
            }
        }
    }

    /**
     * Creates XML Document containing template for a new calendar.
     * @param user User for which the calendar will be created
     * @return XML Document containing template for a new calendar
     */
    private Document buildNewCalendar(User user) {
        Element calendarElement = new Element(XMLCalendarTags.CALENDAR_TAG);

        Element userElement = user.toXMLElement();
        Element nextEventIdElement = new Element(XMLCalendarTags.NEXT_EVENT_ID_TAG);
        nextEventIdElement.setText("1");
        Element eventsElement = new Element(XMLCalendarTags.EVENTS_TAG);

        calendarElement.addContent(List.of(userElement, nextEventIdElement, eventsElement));
        return new Document(calendarElement);
    }

    /**
     * Creates a new calendar for the user with the given username and password hash.
     * The calendar is stored in an XML file with the username as the filename.
     *
     * @param user User for which the calendar will be created
     * @throws InvalidInputException if the username is null, empty, or "unlogged".
     */
    @Override
    public synchronized void createAccount(User user, ClientSession session) throws XmlDatabaseException {
        validateUsersUsername(user, session);
        if (Files.exists(getUserFilePath(user.username()))) {
            LOGGER.error("Client session '{}': The calendar for username '{}' already exists.",session.getSessionId(), user.username());
            throw new InvalidInputException("The calendar for username '" + user.username() + "' already exists");
        }

        try {
            var file = Files.createFile(getUserFilePath(user.username()));

            var outputter = new XMLOutputter();
            outputter.setFormat(Format.getPrettyFormat());
            outputter.output(buildNewCalendar(user), Files.newOutputStream(file));

            LOGGER.info("Created calendar for user '{}'", user.username());
        } catch (IOException e) {
            LOGGER.error("Failed to create calendar for user '{}'", user.username(), e);
            throw new XmlDatabaseException("Failed to create calendar for user '" + user.username() + "'");
        }
    }

    /**
     * Parses a user's calendar XML file and returns the Document object.
     * @param file The path to the user's calendar XML file
     * @return the Document object representing the user's calendar
     * @throws XmlDatabaseException if an error occurs while parsing the XML file or if the file is not valid calendar
     */
    private static Document getUserCalendarDocument(Path file) throws XmlDatabaseException {
        try {
            SAXBuilder builder = new SAXBuilder();
            var document = builder.build(file.toFile());

            if (document == null) {
                LOGGER.error("Failed to get calendar from file '{}': File is empty or invalid XML", file);
                throw new XmlDatabaseException("The XML file is empty");
            }
            if (!document.getRootElement().getName().equals(XMLCalendarTags.CALENDAR_TAG)) {
                LOGGER.error("The XML file '{}' is not a calendar file", file);
                throw new XmlDatabaseException("The XML file is not a valid calendar");
            }

            return document;
        } catch (Exception e) {
            LOGGER.error("Failed to parse file '{}'", file, e);
            throw new XmlDatabaseException("Failed to parse XML file: " + file);
        }
    }

    /**
     * Authenticates a user with the given username and password hash.
     * The method checks if the username exists and if the password hash matches.
     *
     * @param user The user that is being authenticated.
     * @return true if authentication is successful, false otherwise.
     */
    @Override
    public synchronized boolean authenticate(User user, ClientSession session) {
        validateUsersUsername(user, session);
        validateUserRepositoryLocation(user);

        var document = getUserCalendarDocument(getUserFilePath(user.username()));
        var calendarElement = document.getRootElement();
        var userElement = calendarElement.getChild(XMLCalendarTags.USER_TAG);

        String usernameFromFile = userElement.getChildText(XMLCalendarTags.USERNAME_TAG);
        int passwordHashFromFile = Integer.parseInt(userElement.getChildText(XMLCalendarTags.PASSWORD_HASH_TAG));

        return user.username().equals(usernameFromFile) && passwordHashFromFile == user.passwordHash();
    }

    /**
     * Adds an event to the user's calendar.
     * The method increments <nextEventId></nextEventId> XML elements value and adds the event with it into the XML file.
     *
     * @param user The user for which the event will be added.
     * @param event The event to add.
     * @return The ID of the added event.
     */
    @Override
    public long addEvent(User user, Event event, ClientSession session) throws XmlDatabaseException, IOException {
        validateUsersUsername(user, session);
        validateUserRepositoryLocation(user);

        var document = getUserCalendarDocument(getUserFilePath(user.username()));
        var rootCalendar = document.getRootElement();
        var nextEventIdElement = rootCalendar.getChild(XMLCalendarTags.NEXT_EVENT_ID_TAG);
        long nextEventId = Long.parseLong(nextEventIdElement.getText());

        Event eventWithId = Event.withId(nextEventId, event);
        nextEventIdElement.setText(String.valueOf(nextEventId + 1));

        var eventsElement = rootCalendar.getChild(XMLCalendarTags.EVENTS_TAG);
        eventsElement.addContent(eventWithId.toXMLElement());

        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        outputter.output(document, Files.newOutputStream(getUserFilePath(user.username())));
        LOGGER.info("Client session '{}': Added new calendar event for user '{}'", session.getSessionId(), user.username());
        return nextEventId;
    }

    /**
     * Deletes an event from the user's calendar.
     * The method searches for the event with the given ID and removes it from the XML file.
     *
     * @param user The user for which the event will be deleted.
     * @param eventId The ID of the event to delete.
     * @param session The client session.
     * @throws XmlDatabaseException if an error occurs while deleting the event
     */
    @Override
    public void deleteEvent(User user, Long eventId, ClientSession session) throws XmlDatabaseException {
        validateUsersUsername(user, session);
        validateUserRepositoryLocation(user);

        var document = getUserCalendarDocument(getUserFilePath(user.username()));
        var eventsElement = document.getRootElement().getChild(XMLCalendarTags.EVENTS_TAG);

        boolean eventFound = false;
        for (var eventElement : eventsElement.getChildren()) {
            if (eventElement.getChildText(XMLCalendarTags.ID_TAG).equals(String.valueOf(eventId))) {
                eventsElement.removeContent(eventElement);
                eventFound = true;
                break;
            }
        }

        if (!eventFound) {
            LOGGER.error("Client session '{}': Event with ID '{}' not found in the calendar for user '{}'", session.getSessionId(), eventId, user.username());
            throw new XmlDatabaseException("Event with ID '" + eventId + "' not found in the calendar");
        }

        try {
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            outputter.output(document, Files.newOutputStream(getUserFilePath(user.username())));
            LOGGER.info("Client session '{}': Deleted event with ID '{}' from calendar for user '{}'", session.getSessionId(), eventId, user.username());
        } catch (IOException e) {
            LOGGER.error("Failed to save calendar for user '{}' while deleting event", user.username(), e);
            throw new XmlDatabaseException("Failed to save '" + user.username() + "' calendar while deleting event");
        }
    }

    /**
     * Retrieves all events from the user's calendar.
     * The method reads the XML file and returns a list of events.
     *
     * @param user The user for which the events will be retrieved.
     * @param session The client session.
     * @return A list of events from the user's calendar.
     * @throws XmlDatabaseException if an error occurs while retrieving the events
     */
    @Override
    public List<Event> getAllEvents(User user, ClientSession session) throws XmlDatabaseException {
        validateUsersUsername(user, session);
        validateUserRepositoryLocation(user);

        var document = getUserCalendarDocument(getUserFilePath(user.username()));
        var eventsElement = document.getRootElement().getChild(XMLCalendarTags.EVENTS_TAG);

        List<Event> events = new ArrayList<>();
        for (var eventElement : eventsElement.getChildren()) {
            var event = Event.fromXMLElement(eventElement);
            events.add(event);
        }

        LOGGER.info("Client session '{}': Retrieved all events for user '{}'", session.getSessionId(), user.username());
        return events;
    }

    /**
     * Updates an event in the user's calendar.
     * The method searches for the event with the given ID and updates its details.
     * If the event detail is null, the previous value is kept.
     *
     * @param user The user for which the event will be updated.
     * @param event The event to update.
     * @param session The client session.
     * @throws XmlDatabaseException if an error occurs while updating the event
     */
    @Override
    public void updateEvent(User user, Event event, ClientSession session) throws XmlDatabaseException {
        validateUsersUsername(user, session);
        validateUserRepositoryLocation(user);

        var document = getUserCalendarDocument(getUserFilePath(user.username()));
        var eventsElement = document.getRootElement().getChild(XMLCalendarTags.EVENTS_TAG);

        boolean eventFound = false;
        for (var eventElement : eventsElement.getChildren()) {
            if (eventElement.getChildText(XMLCalendarTags.ID_TAG).equals(String.valueOf(event.getId()))) {
                eventFound = true;
                if (event.getTitle() != null)
                    eventElement.getChild(XMLCalendarTags.TITLE_TAG).setText(event.getTitle());
                if (event.getDate() != null)
                    eventElement.getChild(XMLCalendarTags.DATE_TAG).setText(event.getDate().toString());
                if (event.getTime() != null)
                    eventElement.getChild(XMLCalendarTags.TIME_TAG).setText(event.getTime().toString());
                if (event.getLocation() != null)
                    eventElement.getChild(XMLCalendarTags.LOCATION_TAG).setText(event.getLocation());
                if (event.getDescription() != null)
                    eventElement.getChild(XMLCalendarTags.DESCRIPTION_TAG).setText(event.getDescription());
                break;
            }
        }

        if (!eventFound) {
            LOGGER.error("Client session '{}': Event with ID '{}' not found in the calendar for user '{}'", session.getSessionId(), event.getId(), user.username());
            throw new XmlDatabaseException("Event with ID '" + event.getId() + "' not found in the calendar");
        }

        try {
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            outputter.output(document, Files.newOutputStream(getUserFilePath(user.username())));
            LOGGER.info("Client session '{}': Updated event with ID '{}' in calendar for user '{}'", session.getSessionId(), event.getId(), user.username());
        } catch (IOException e) {
            LOGGER.error("Failed to modify the '{}' calendar event with ID '{}'",user.username(), event.getId(), e);
            throw new XmlDatabaseException("Failed to modify calendar event");
        }
    }
}
