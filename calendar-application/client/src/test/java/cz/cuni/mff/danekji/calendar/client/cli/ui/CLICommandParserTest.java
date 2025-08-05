package cz.cuni.mff.danekji.calendar.client.cli.ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;
import cz.cuni.mff.danekji.calendar.core.commands.Command;
import cz.cuni.mff.danekji.calendar.core.commands.LoginCommand;
import org.junit.jupiter.api.Test;

public class CLICommandParserTest {

    @Test
    public void registerCommand_addsToRegistry() {
        CLICommandParser parser = new CLICommandParser();
        parser.registerCommand("login", LoginCommand.class);

        assertTrue(parser.getUnmodifiableCommandRegistry().containsKey("login"));
        assertEquals(LoginCommand.class, parser.getUnmodifiableCommandRegistry().get("login"));
    }

    @Test
    public void parse_createsCommandFromInput() throws Exception {
        CLICommandParser parser = new CLICommandParser();
        parser.registerCommand("login", LoginCommand.class);
        UserInterface mockUI = mock(UserInterface.class);
        ClientSession mockSession = mock(ClientSession.class);

        when(mockUI.promptForInput(anyString())).thenReturn("user");
        when(mockUI.promptForPassword(anyString())).thenReturn("pass");

        Command command = parser.parse("login", mockUI, mockSession);

        assertInstanceOf(LoginCommand.class, command);
        assertEquals("user", ((LoginCommand) command).getUser().username());
    }
}