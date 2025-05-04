package cz.cuni.mff.danekji1.calendar.core;

import cz.cuni.mff.danekji1.calendar.core.commands.Command;
import cz.cuni.mff.danekji1.calendar.core.responses.Response;
import cz.cuni.mff.danekji1.calendar.core.session.Session;

import java.io.IOException;

public interface Client {
    void connect(String host, int port);
    void start();
    void disconnect();
    boolean isConnectionOpen();
    Session getCurrentSession();
    Response sendCommand(Command command) throws ClassNotFoundException, IOException;
}
