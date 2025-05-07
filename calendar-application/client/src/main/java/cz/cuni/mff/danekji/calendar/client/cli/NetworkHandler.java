package cz.cuni.mff.danekji.calendar.client.cli;

import cz.cuni.mff.danekji.calendar.core.commands.Command;
import cz.cuni.mff.danekji.calendar.core.responses.Response;

import java.io.IOException;

public interface NetworkHandler {
    int connect(String host, int port) throws IOException, ClassNotFoundException;
    Response sendCommand(Command command) throws IOException, ClassNotFoundException;
    void disconnect() throws IOException;
    boolean isConnected();
}