package cz.cuni.mff.danekji1.calendar.server.storage;

import cz.cuni.mff.danekji1.calendar.core.exceptions.InvalidInputException;
import cz.cuni.mff.danekji1.calendar.core.exceptions.XmlDatabaseException;
import cz.cuni.mff.danekji1.calendar.core.models.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class XMLEventRepository implements EventRepository {
    private final static Logger logger = LogManager.getLogger(XMLEventRepository.class);

    private final static Path XML_FILE_FOLDER = Path.of("./data");

    public XMLEventRepository() {
        if (Files.notExists(XML_FILE_FOLDER)) {
            try {
                Files.createDirectories(XML_FILE_FOLDER);
            } catch (IOException e) {
                logger.fatal("Failed to create XML file folder", e);
            }
        }
    }

    private Path getUserFilePath(String username) {
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
            logger.info("Created calendar for user '{}'", username);
        } catch (IOException e) {
            logger.debug("Failed to create calendar for user '{}'", username, e);
            throw new XmlDatabaseException("Failed to create calendar for user '" + username + "'");
        }
    }

    @Override
    public void authenticate(String username, int passwordHash) {

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
