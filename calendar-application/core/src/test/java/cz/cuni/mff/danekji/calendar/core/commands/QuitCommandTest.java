package cz.cuni.mff.danekji.calendar.core.commands;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;

public class QuitCommandTest {

    @Test
    public void buildCommand_alwaysSucceeds() throws Exception {
        // arrange
        UserInterface mockUI = mock(UserInterface.class);
        ClientSession mockSession = mock(ClientSession.class);

        // act & assert
        Command command = new QuitCommand().buildCommand(mockUI, mockSession);
        assertInstanceOf(QuitCommand.class, command);
    }
}