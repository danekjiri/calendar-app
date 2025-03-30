package com.tesco.calendar.server;

import com.tesco.calendar.core.commands.Command;
import com.tesco.calendar.core.responses.Response;

public interface CommandHandler<T extends Command> {
    Response handle(T command, Session session);
}