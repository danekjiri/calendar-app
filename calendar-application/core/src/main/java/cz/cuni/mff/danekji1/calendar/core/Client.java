package cz.cuni.mff.danekji1.calendar.core;

import cz.cuni.mff.danekji1.calendar.core.commands.Command;
import cz.cuni.mff.danekji1.calendar.core.responses.Response;
import cz.cuni.mff.danekji1.calendar.core.ui.ClientState;

public interface Client {
    void connect(String host, int port);
    void disconnect();
    void start();
    boolean isConnectionOpen();
    ClientState getCurrentSession();
    Response sendCommand(Command command);
}
