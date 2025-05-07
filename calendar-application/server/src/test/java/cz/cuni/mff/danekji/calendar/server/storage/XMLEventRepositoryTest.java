package cz.cuni.mff.danekji.calendar.server.storage;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import cz.cuni.mff.danekji.calendar.core.exceptions.client.InvalidInputException;
import cz.cuni.mff.danekji.calendar.core.models.User;
import cz.cuni.mff.danekji.calendar.core.models.Event;
import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;

public class XMLEventRepositoryTest {

    @Test
    public void createAccount_createsXmlFile() throws Exception {
        // arrange
        XMLEventRepository repo = new XMLEventRepository();
        User user = new User("bob", 456);
        ClientSession session = new ClientSession(42, null);

        // act
        repo.createAccount(user, session);
        Path file = repo.getUserFilePath("bob");

        // assert
        assertTrue(Files.exists(file), "XML file should be created for new account");

        // cleanup
        Files.delete(file);
    }

    @Test
    public void addEvent_addsEventToXml() throws Exception {
        // arrange
        XMLEventRepository repo = new XMLEventRepository();
        User user = new User("alice", 123);
        ClientSession session = new ClientSession(42, null);

        // act
        repo.createAccount(user, session);
        Event event = new Event("Party", LocalDate.of(2023, 12, 31), LocalTime.of(20, 0), "Home", "New Year");
        long eventId = repo.addEvent(user, event, session);

        // assert
        assertEquals(1L, eventId, "First event should have ID 1");

        // verify event in XML
        var document = XMLEventRepository.getUserCalendarDocument(repo.getUserFilePath("alice"));
        var eventsElement = document.getRootElement().getChild("events");
        assertEquals(1, eventsElement.getChildren().size(), "XML should contain one event");
        assertEquals("1", eventsElement.getChildren().getFirst().getChildText("id"), "Event ID in XML should match");

        // cleanup
        Files.delete(repo.getUserFilePath("alice"));
    }

    @Test
    public void createAccount_throwsExceptionForExistingUser() throws Exception {
        // arrange
        XMLEventRepository repo = new XMLEventRepository();
        User user = new User("charlie", 789);
        ClientSession session = new ClientSession(42, null);

        // act
        repo.createAccount(user, session);

        // assert
        Exception exception = assertThrows(InvalidInputException.class, () -> repo.createAccount(user, session),
                "Should throw exception when creating duplicate account");
        assertTrue(exception.getMessage().contains("already exists"), "Exception message should indicate existing account");

        // cleanup
        Files.delete(repo.getUserFilePath("charlie"));
    }
}