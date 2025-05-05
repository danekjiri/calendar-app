package cz.cuni.mff.danekji1.calendar.core.ui;

import cz.cuni.mff.danekji1.calendar.core.session.ClientSession;
import cz.cuni.mff.danekji1.calendar.core.commands.Command;
import cz.cuni.mff.danekji1.calendar.core.responses.Response;
import cz.cuni.mff.danekji1.calendar.core.Client;

import java.io.IOException;
import java.util.Map;

/**
 * Defines the UI for the client. In future this can be enhanced to support a GUI.
 */
public interface UserInterface {
    /**
     * Starts the UI.
     *
     * @param client the client to be used
     */
    void start(Client client);

    /**
     * Accepts a visitor on response to present the right response to the user.
     *
     * @param response The response to be displayed accepted
     * @param session The client session
     * @throws IOException if an I/O error occurs (displaying the response)
     */
    void displayResponse(Response response, ClientSession session) throws IOException;

    /**
     * Asks the user for input using displayed message and returns the input.
     *
     * @param message The message to be displayed
     * @return The input from the user
     * @throws IOException if an I/O error occurs (displaying the message)
     */
    String promptForInput(String message) throws IOException;

    /**
     * Gets the unmodifiable command registry that maps command names to their classes.
     * @return The unmodifiable command registry
     */
    Map<String, Class<? extends Command>> getUnmodifiableCommandRegistry();
}
