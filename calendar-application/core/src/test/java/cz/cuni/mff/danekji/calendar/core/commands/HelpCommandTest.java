package cz.cuni.mff.danekji.calendar.core.commands;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;
import java.util.Map;

public class HelpCommandTest {

    @Test
    public void buildCommand_retrievesCommandRegistry() throws Exception {
        // arrange
        UserInterface mockUI = mock(UserInterface.class);
        ClientSession mockSession = mock(ClientSession.class);
        Map<String, Class<? extends Command>> registry = Map.of("login", LoginCommand.class);
        when(mockUI.getUnmodifiableCommandRegistry()).thenReturn(registry);

        // act
        HelpCommand command = (HelpCommand) new HelpCommand().buildCommand(mockUI, mockSession);
        assertEquals(registry, command.getAvailableCommands());
    }
}