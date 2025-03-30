package com.tesco.calendar.server;

import com.tesco.calendar.core.commands.*;
import com.tesco.calendar.core.responses.*;
import com.tesco.calendar.server.storage.EventRepository;
import com.tesco.calendar.server.storage.UserRepository;

public class CommandDispatcher implements CommandVisitor<Response> {
    private final Session session;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public CommandDispatcher(Session session, UserRepository userRepository, EventRepository eventRepository) {
        this.session = session;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * The implementation of the CommandVisitor endpoint for LoginCommand.
     * Checks if username and password matches any record in user-database and if so,
     *  logs in user and return successful response.
     * Otherwise, returns error response.
     */
    @Override
    public Response visit(LoginCommand command) {
//        // example of simple implementation
//        if (session.isLoggedIn()) {
//            return new ErrorResponse("Already logged in.");
//        }
//        User user = userRepository.authenticate(command.getUsername(), command.getPasswordHash());
//        if (user != null) {
//            session.setCurrentUser(user);
//            return new SuccessResponse("Login successful.");
//        } else {
//            return new ErrorResponse("Invalid credentials.");
//        }
        throw new UnsupportedOperationException("LoginCommand not implemented yet.");
        // todo: implement login command
    }

    /**
     * The implementation of the CommandVisitor endpoint for CreateAccountCommand.
     * Checks if username unique, if username and password good and if so,
     *  then create user in the server (xml) user-database and return successful response.
     * Otherwise, returns error response.
     */
    @Override
    public Response visit(CreateAccountCommand command) {
        throw new UnsupportedOperationException("CreateAccountCommand not implemented yet.");
        // todo: implement create_user command
    }

    /**
     * The implementation of the CommandVisitor endpoint for AddEventCommand.
     * Checks if user logged and if so then creates record about event, provided in command,
     *  into the server (xml) event-database and returns successful response.
     * Otherwise, returns error response.
     */
    @Override
    public Response visit(AddEventCommand command) {
        throw new UnsupportedOperationException("AddEventCommand not implemented yet.");
        // todo: implement add command
    }

    // more command will be added later via com.tesco.calendar.core.commands.CommandVisitor
}
