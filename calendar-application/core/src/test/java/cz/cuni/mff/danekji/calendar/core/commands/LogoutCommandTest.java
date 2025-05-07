package cz.cuni.mff.danekji.calendar.core.commands;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;
import cz.cuni.mff.danekji.calendar.core.exceptions.InsufficientCommandPrivilegesException;

public class LogoutCommandTest {

    @Test
    public void buildCommand_succeedsIfLoggedIn() throws Exception {
        // arrange
        UserInterface mockUI = mock(UserInterface.class);
        ClientSession mockSession = mock(ClientSession.class);
        when(mockSession.isLoggedIn()).thenReturn(true);

        // act
        Command command = new LogoutCommand().buildCommand(mockUI, mockSession);

        // assert
        assertInstanceOf(LogoutCommand.class, command);
    }

    @Test
    public void buildCommand_failsIfNotLoggedIn() {
        // arrange
        UserInterface mockUI = mock(UserInterface.class);
        ClientSession mockSession = mock(ClientSession.class);
        when(mockSession.isLoggedIn()).thenReturn(false);

        // act & assert
        assertThrows(InsufficientCommandPrivilegesException.class, () ->
                new LogoutCommand().buildCommand(mockUI, mockSession));
    }
}