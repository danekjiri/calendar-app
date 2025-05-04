package cz.cuni.mff.danekji1.calendar.core.ui;

import cz.cuni.mff.danekji1.calendar.core.session.Session;
import cz.cuni.mff.danekji1.calendar.core.commands.Command;
import cz.cuni.mff.danekji1.calendar.core.responses.Response;
import cz.cuni.mff.danekji1.calendar.core.Client;

import java.io.IOException;
import java.util.Map;

/**
 * Defines the UI for the client. In future this can be enhanced to support a GUI.
 */
public interface UserInterface {
    void start(Client client);
    void displayResponse(Response response, Session session) throws IOException;
    String promptForInput(String message) throws IOException;
    Map<String, Class<? extends Command>> getCommandRegistry();
}
