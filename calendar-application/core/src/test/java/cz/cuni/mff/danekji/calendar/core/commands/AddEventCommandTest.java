package cz.cuni.mff.danekji.calendar.core.commands;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;
import cz.cuni.mff.danekji.calendar.core.exceptions.InsufficientCommandPrivilegesException;
import cz.cuni.mff.danekji.calendar.core.models.Event;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;

public class AddEventCommandTest {

    @Test
    public void buildCommand_createsEvent() throws Exception {
        // arrange
        UserInterface mockUI = mock(UserInterface.class);
        ClientSession mockSession = mock(ClientSession.class);
        when(mockSession.isLoggedIn()).thenReturn(true);
        when(mockUI.promptForInput(anyString()))
                .thenReturn("Meeting", "2023-12-01", "10:00", "Office", "Team sync");

        // act
        AddEventCommand command = (AddEventCommand) new AddEventCommand().buildCommand(mockUI, mockSession);
        Event event = command.getEvent();

        // assert
        assertEquals("Meeting", event.getTitle());
        assertEquals(LocalDate.of(2023, 12, 1), event.getDate());
        assertEquals(LocalTime.of(10, 0), event.getTime());
    }

    @Test
    public void buildCommand_failsIfNotLoggedIn() {
        // arrange
        UserInterface mockUI = mock(UserInterface.class);
        ClientSession mockSession = mock(ClientSession.class);
        when(mockSession.isLoggedIn()).thenReturn(false);

        // act && assert
        assertThrows(InsufficientCommandPrivilegesException.class, () ->
                new AddEventCommand().buildCommand(mockUI, mockSession));
    }
}