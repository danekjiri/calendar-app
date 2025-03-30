package com.tesco.calendar.client;

import com.tesco.calendar.core.responses.Response;

/**
 * Defines the UI for the client. In future this can be enhanced to support a GUI.
 */
public interface UserInterface {
    void start(Client client);
    void displayResponse(Response response);
}
