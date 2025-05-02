package cz.cuni.mff.danekji1.calendar.core.ui;

import cz.cuni.mff.danekji1.calendar.core.responses.Response;
import cz.cuni.mff.danekji1.calendar.core.Client;

import java.io.IOException;

/**
 * Defines the UI for the client. In future this can be enhanced to support a GUI.
 */
public interface UserInterface {
    void start(Client client);
    void displayResponse(Response response, ClientState session);
    String promptForInput(String message) throws IOException;

}
