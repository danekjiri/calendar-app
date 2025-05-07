package cz.cuni.mff.danekji.calendar.core.commands;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;
import cz.cuni.mff.danekji.calendar.core.exceptions.InsufficientCommandPrivilegesException;
import cz.cuni.mff.danekji.calendar.core.exceptions.client.InvalidInputException;

public class DeleteEventCommandTest {

    @Test
    public void buildCommand_createsCommandWithEventId() throws Exception {
        // arrange
        UserInterface mockUI = mock(UserInterface.class);
        ClientSession mockSession = mock(ClientSession.class);
        when(mockSession.isLoggedIn()).thenReturn(true);
        when(mockUI.promptForInput(anyString())).thenReturn("42");

        // act
        DeleteEventCommand command = (DeleteEventCommand) new DeleteEventCommand().buildCommand(mockUI, mockSession);

        // assert
        assertEquals(42L, command.getEventId());
    }

    @Test
    public void buildCommand_failsIfNotLoggedIn() {
        // arrange
        UserInterface mockUI = mock(UserInterface.class);
        ClientSession mockSession = mock(ClientSession.class);
        when(mockSession.isLoggedIn()).thenReturn(false);

        // act & assert
        assertThrows(InsufficientCommandPrivilegesException.class, () ->
                new DeleteEventCommand().buildCommand(mockUI, mockSession));
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
                new DeleteEventCommand().buildCommand(mockUI, mockSession));
    }
}