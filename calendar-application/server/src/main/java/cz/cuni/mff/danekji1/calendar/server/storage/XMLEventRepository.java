package cz.cuni.mff.danekji1.calendar.server.storage;

import cz.cuni.mff.danekji1.calendar.core.exceptions.InvalidInputException;
import cz.cuni.mff.danekji1.calendar.core.exceptions.XmlDatabaseException;
import cz.cuni.mff.danekji1.calendar.core.models.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
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
    private final static Path XML_FILE_FOLDER = Path.of("./data");

    public XMLEventRepository() {
        if (Files.notExists(XML_FILE_FOLDER)) {
            try {
                Files.createDirectories(XML_FILE_FOLDER);
            } catch (IOException e) {
                LOGGER.fatal("Failed to create XML file folder", e);
            }
        }
    }

    private static Path getUserFilePath(String username) {
        return Path.of(XML_FILE_FOLDER + "/" + username + ".xml");
    }

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

    private static Element getUserCalendarRoot(Path file) {
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

    @Override
    public void addEvent(String username, Event event) {

    }

    @Override
    public void removeEvent(String username, Long eventId) {

    }

    @Override
    public List<Event> getAllEvents(String username) {
        return List.of();
    }

    @Override
    public List<Event> getFutureEvents(String username) {
        return List.of();
    }

    @Override
    public void updateEvent(String username, Event event) {

    }
}
