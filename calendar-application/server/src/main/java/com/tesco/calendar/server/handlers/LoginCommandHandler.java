package com.tesco.calendar.server.handlers;

import com.tesco.calendar.core.commands.LoginCommand;
import com.tesco.calendar.core.responses.Response;
import com.tesco.calendar.server.CommandHandler;
import com.tesco.calendar.server.Session;
import com.tesco.calendar.server.storage.UserRepository;

public class LoginCommandHandler implements CommandHandler<LoginCommand> {
    private final UserRepository userRepository;

    public LoginCommandHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Response handle(LoginCommand command, Session session) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
