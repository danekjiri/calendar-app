package cz.cuni.mff.danekji.calendar.core.commands;

import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;
import cz.cuni.mff.danekji.calendar.core.exceptions.InsufficientCommandPrivilegesException;
import cz.cuni.mff.danekji.calendar.core.exceptions.client.InvalidInputException;
import cz.cuni.mff.danekji.calendar.core.models.User;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DeleteUserCommandTest {

    @Test
    public void buildCommand_createsCommandWithVerificationUser() throws Exception {
        // arrange
        UserInterface mockUI = mock(UserInterface.class);
        ClientSession mockSession = mock(ClientSession.class);
        User loggedInUser = new User("alice", "old_password".hashCode());

        when(mockSession.isLoggedIn()).thenReturn(true);
        when(mockSession.getCurrentUser()).thenReturn(loggedInUser);
        when(mockUI.promptForPassword(anyString())).thenReturn("password123");

        // act
        DeleteUserCommand command = (DeleteUserCommand) new DeleteUserCommand().buildCommand(mockUI, mockSession);
        User verificationUser = command.getUser();

        // assert
        assertEquals("alice", verificationUser.username());
        assertEquals("password123".hashCode(), verificationUser.passwordHash());
    }

    @Test
    public void buildCommand_throwsForEmptyPassword() throws IOException {
        // arrange
        UserInterface mockUI = mock(UserInterface.class);
        ClientSession mockSession = mock(ClientSession.class);

        when(mockSession.isLoggedIn()).thenReturn(true);
        when(mockUI.promptForPassword(anyString())).thenReturn(" "); // Simulates empty or whitespace input

        // act & assert
        assertThrows(InvalidInputException.class, () ->
                new DeleteUserCommand().buildCommand(mockUI, mockSession));
    }

    @Test
    public void buildCommand_throwsWhenNotLoggedIn() {
        // arrange
        UserInterface mockUI = mock(UserInterface.class);
        ClientSession mockSession = mock(ClientSession.class);
        when(mockSession.isLoggedIn()).thenReturn(false);

        // act & assert
        assertThrows(InsufficientCommandPrivilegesException.class, () ->
                new DeleteUserCommand().buildCommand(mockUI, mockSession));
    }
}