package cz.cuni.mff.danekji1.calendar.server.storage;

import cz.cuni.mff.danekji1.calendar.core.exceptions.client.InvalidInputException;
import cz.cuni.mff.danekji1.calendar.core.exceptions.server.ServerException;
import cz.cuni.mff.danekji1.calendar.core.exceptions.server.XmlDatabaseException;
import cz.cuni.mff.danekji1.calendar.core.models.Event;
import cz.cuni.mff.danekji1.calendar.core.models.User;
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
            } catch (IOException e) {
                throw new XmlDatabaseException("Failed to create XML file folder");
            }
        }
    }



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
     * @param user user for which the calendar will be created
     * @throws InvalidInputException if the username is null, empty, or "unlogged".
     */
    @Override
    public void createAccount(User user) {
        if (user.username() == null || user.username().isEmpty() || user.username().equals("unlogged")) {
            throw new InvalidInputException("Username cannot be null or empty or 'unlogged'");
        }
        if (Files.exists(getUserFilePath(user.username()))) {
            throw new InvalidInputException("The calendar for username '" + user.username() + "' already exists");
        }

        try {
            var file = Files.createFile(getUserFilePath(user.username()));

            var outputter = new XMLOutputter();
            outputter.setFormat(Format.getPrettyFormat());
            outputter.output(buildNewCalendar(user), Files.newOutputStream(file));

            LOGGER.info("Created calendar for user '{}'", user.username());
        } catch (IOException e) {
            LOGGER.debug("Failed to create calendar for user '{}'", user.username(), e);
            throw new XmlDatabaseException("Failed to create calendar for user '" + user.username() + "'");
        }
    }

    private static Document getUserCalendarDocument(Path file) throws XmlDatabaseException {
        try {
            SAXBuilder builder = new SAXBuilder();
            var document = builder.build(file.toFile());

            if (document == null) {
                throw new XmlDatabaseException("The XML file is empty");
            }
            if (!document.getRootElement().getName().equals(XMLCalendarTags.CALENDAR_TAG)) {
                throw new XmlDatabaseException("The XML file is not a valid calendar");
            }

            return document;
        } catch (Exception e) {
            LOGGER.debug("Failed to create SAX parser", e);
            throw new XmlDatabaseException("Failed to create SAX parser");
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
    public synchronized boolean authenticate(User user) {
        if (user.username() == null || user.username().isEmpty() || user.username().equals("unlogged")) {
            LOGGER.debug("Username cannot be null or empty or 'unlogged'");
            return false;
        }

        if (!Files.exists(getUserFilePath(user.username()))) {
            LOGGER.debug("The calendar for username '{}' does not exist", user.username());
            return false;
        }

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
    public long addEvent(User user, Event event) throws XmlDatabaseException, IOException {
        validateUsersUsername(user);
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
        return nextEventId;
    }

    @Override
    public void deleteEvent(User user, Long eventId) {
        validateUsersUsername(user);
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
            LOGGER.debug("Event with ID '{}' not found in the calendar for user '{}'", eventId, user.username());
            throw new XmlDatabaseException("Event with ID '" + eventId + "' not found in the calendar");
        }

        try {
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            outputter.output(document, Files.newOutputStream(getUserFilePath(user.username())));
        } catch (IOException e) {
            LOGGER.debug("Failed to save calendar after deleting event", e);
            throw new XmlDatabaseException("Failed to save calendar after deleting event");
        }
    }

    @Override
    public List<Event> getAllEvents(User user) {
        validateUsersUsername(user);
        validateUserRepositoryLocation(user);

        var document = getUserCalendarDocument(getUserFilePath(user.username()));
        var eventsElement = document.getRootElement().getChild(XMLCalendarTags.EVENTS_TAG);

        List<Event> events = new ArrayList<>();
        for (var eventElement : eventsElement.getChildren()) {
            var event = Event.fromXMLElement(eventElement);
            events.add(event);
        }

        return events;
    }

    @Override
    public List<Event> getFutureEvents(User user) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void updateEvent(User user, Event event) {
        validateUsersUsername(user);
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
            throw new XmlDatabaseException("Event with ID '" + event.getId() + "' not found in the calendar");
        }

        try {
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            outputter.output(document, Files.newOutputStream(getUserFilePath(user.username())));
        } catch (IOException e) {
            LOGGER.warn("Failed to modify calendar event", e);
            throw new XmlDatabaseException("Failed to modify calendar event");
        }
    }
}
