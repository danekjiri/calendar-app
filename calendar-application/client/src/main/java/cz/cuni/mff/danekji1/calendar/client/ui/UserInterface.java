package cz.cuni.mff.danekji1.calendar.client.ui;

import cz.cuni.mff.danekji1.calendar.client.Client;
import cz.cuni.mff.danekji1.calendar.core.responses.Response;

import java.io.IOException;

/**
 * Defines the UI for the client. In future this can be enhanced to support a GUI.
 */
public interface UserInterface {
    void start(Client client);
    void displayResponse(Response response);
    String promptForInput(String message) throws IOException;
}
