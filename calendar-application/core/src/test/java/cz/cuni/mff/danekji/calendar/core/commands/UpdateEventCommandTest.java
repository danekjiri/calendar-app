package cz.cuni.mff.danekji.calendar.core.commands;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;
import cz.cuni.mff.danekji.calendar.core.exceptions.InsufficientCommandPrivilegesException;
import cz.cuni.mff.danekji.calendar.core.exceptions.client.InvalidInputException;
import cz.cuni.mff.danekji.calendar.core.models.Event;
import java.time.LocalDate;
import java.time.LocalTime;

public class UpdateEventCommandTest {

    @Test
    public void buildCommand_createsEventWithUpdatedFields() throws Exception {
        // arrange
        UserInterface mockUI = mock(UserInterface.class);
        ClientSession mockSession = mock(ClientSession.class);
        when(mockSession.isLoggedIn()).thenReturn(true);
        when(mockUI.promptForInput(anyString()))
                .thenReturn("1", "New Title", "2025-12-01", "10:00", "New Location", "New Description");

        // act
        UpdateEventCommand command = (UpdateEventCommand) new UpdateEventCommand().buildCommand(mockUI, mockSession);
        Event event = command.getEvent();

        // assert
        assertEquals(1L, event.getId());
        assertEquals("New Title", event.getTitle());
        assertEquals(LocalDate.of(2025, 12, 1), event.getDate());
        assertEquals(LocalTime.of(10, 0), event.getTime());
        assertEquals("New Location", event.getLocation());
        assertEquals("New Description", event.getDescription());
    }

    @Test
    public void buildCommand_usesNullForUnchangedFields() throws Exception {
        // arrange
        UserInterface mockUI = mock(UserInterface.class);
        ClientSession mockSession = mock(ClientSession.class);
        when(mockSession.isLoggedIn()).thenReturn(true);
        when(mockUI.promptForInput(anyString()))
                .thenReturn("1", "", "", "", "", ""); // Empty inputs for optional fields

        // act
        UpdateEventCommand command = (UpdateEventCommand) new UpdateEventCommand().buildCommand(mockUI, mockSession);
        Event event = command.getEvent();

        // assert
        assertEquals(1L, event.getId());
        assertNull(event.getTitle());
        assertNull(event.getDate());
        assertNull(event.getTime());
        assertNull(event.getLocation());
        assertNull(event.getDescription());
    }

    @Test
    public void buildCommand_failsIfNotLoggedIn() {
        // arrange
        UserInterface mockUI = mock(UserInterface.class);
        ClientSession mockSession = mock(ClientSession.class);
        when(mockSession.isLoggedIn()).thenReturn(false);

        // act & assert
        assertThrows(InsufficientCommandPrivilegesException.class, () ->
                new UpdateEventCommand().buildCommand(mockUI, mockSession));
    }

    @Test
    public void buildCommand_throwsInvalidInputForInvalidId() throws Exception {
        // arrange
        UserInterface mockUI = mock(UserInterface.class);
        ClientSession mockSession = mock(ClientSession.class);
        when(mockSession.isLoggedIn()).thenReturn(true);
        when(mockUI.promptForInput(anyString())).thenReturn("invalid");

        // act & assert
        assertThrows(InvalidInputException.class, () ->
                new UpdateEventCommand().buildCommand(mockUI, mockSession));
    }

    @Test
    public void buildCommand_throwsInvalidInputForInvalidDate() throws Exception {
        // arrange
        UserInterface mockUI = mock(UserInterface.class);
        ClientSession mockSession = mock(ClientSession.class);
        when(mockSession.isLoggedIn()).thenReturn(true);
        when(mockUI.promptForInput(anyString()))
                .thenReturn("1", "Title", "invalid-date", "10:00", "Location", "Description");

        // act & assert
        assertThrows(InvalidInputException.class, () ->
                new UpdateEventCommand().buildCommand(mockUI, mockSession));
    }

    @Test
    public void buildCommand_throwsInvalidInputForInvalidTime() throws Exception {
        // arrange
        UserInterface mockUI = mock(UserInterface.class);
        ClientSession mockSession = mock(ClientSession.class);
        when(mockSession.isLoggedIn()).thenReturn(true);
        when(mockUI.promptForInput(anyString()))
                .thenReturn("1", "Title", "2025-12-01", "invalid-time", "Location", "Description");

        // act & assert
        assertThrows(InvalidInputException.class, () ->
                new UpdateEventCommand().buildCommand(mockUI, mockSession));
    }
}