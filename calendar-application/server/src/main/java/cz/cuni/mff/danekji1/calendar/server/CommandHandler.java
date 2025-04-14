package cz.cuni.mff.danekji1.calendar.server;

import cz.cuni.mff.danekji1.calendar.core.commands.Command;
import cz.cuni.mff.danekji1.calendar.core.responses.Response;

public interface CommandHandler<T extends Command> {
    Response handle(T command, Session session);
}