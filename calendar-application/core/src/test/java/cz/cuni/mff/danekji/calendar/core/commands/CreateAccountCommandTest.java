package cz.cuni.mff.danekji.calendar.core.commands;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;
import cz.cuni.mff.danekji.calendar.core.exceptions.client.InvalidInputException;
import cz.cuni.mff.danekji.calendar.core.models.User;

public class CreateAccountCommandTest {

    @Test
    public void buildCommand_createsUserFromInput() throws Exception {
        // arrange
        UserInterface mockUI = mock(UserInterface.class);
        ClientSession mockSession = mock(ClientSession.class);
        when(mockUI.promptForInput(anyString())).thenReturn("bob");
        when(mockUI.promptForPassword(anyString())).thenReturn("password");

        // act
        CreateAccountCommand command = (CreateAccountCommand) new CreateAccountCommand().buildCommand(mockUI, mockSession);
        User user = command.getUser();

        // assert
        assertEquals("bob", user.username());
        assertEquals("password".hashCode(), user.passwordHash());
    }

    @Test
    public void buildCommand_throwsInvalidInputForEmptyUsername() throws Exception {
        // arrange
        UserInterface mockUI = mock(UserInterface.class);
        ClientSession mockSession = mock(ClientSession.class);
        when(mockUI.promptForInput(anyString())).thenReturn("", "password");

        // act & assert
        assertThrows(InvalidInputException.class, () ->
                new CreateAccountCommand().buildCommand(mockUI, mockSession));
    }
}