package cz.cuni.mff.danekji1.calendar.server;

import cz.cuni.mff.danekji1.calendar.core.commands.*;
import cz.cuni.mff.danekji1.calendar.core.exceptions.CalendarException;
import cz.cuni.mff.danekji1.calendar.core.exceptions.server.XmlDatabaseException;
import cz.cuni.mff.danekji1.calendar.core.responses.error.ErrorResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.success.*;
import cz.cuni.mff.danekji1.calendar.core.session.ClientSession;
import cz.cuni.mff.danekji1.calendar.server.storage.EventRepository;
import cz.cuni.mff.danekji1.calendar.core.responses.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Constructor;

/**
 * Default implementation of the {@link CommandVisitor} interface.
 * This class is responsible for dispatching commands to their respective handlers.
 * It implements the {@link CommandVisitor} interface to handle different command types.
 */
public class DefaultCommandDispatcher implements CommandVisitor<Response, ClientSession> {
    private static final Logger LOGGER = LogManager.getLogger(DefaultCommandDispatcher.class);
    private final EventRepository eventRepository;

    public DefaultCommandDispatcher(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * The implementation of the CommandVisitor endpoint for LoginCommand.
     * Checks if username and password matches any record in user-database and if so,
     *  logs in user and return successful response.
     * Otherwise, returns error response.
     *
     * @param command The {@link LoginCommand} command
     * @param session The client session
     * @return The {@link ErrorResponse} if login failed, otherwise {@link SuccessLoginResponse}
     */
    @Override
    public Response visit(LoginCommand command, ClientSession session) {
        if (session.isLoggedIn()) {
            LOGGER.error("Client session '{}': Attempt to login in account '{}' while already logged in as user '{}'.", session.getSessionId(), command.getUser().username(), session.getCurrentUser().username());
            return new ErrorResponse("You are already logged in as user '" + session.getCurrentUser().username() + "', logout first.");
        }

        if (eventRepository.authenticate(command.getUser(), session)) {
            session.setCurrentUser(command.getUser());
            LOGGER.info("Client session '{}': Logged in as user '{}'.", session.getSessionId(), session.getCurrentUser().username());
            return new SuccessLoginResponse("Login as user '" + command.getUser().username() + "' is successful.", command.getUser());
        } else {
            LOGGER.error("Client session '{}': Pass an invalid credentials '{}' while logging in.", session.getSessionId(), command.getUser());
            return new ErrorResponse("Invalid credentials.");
        }
    }

    /**
     * The implementation of the CommandVisitor endpoint for CreateAccountCommand.
     * Checks if username unique, if username and password good and if so,
     *  then create user in the server (xml) user-database and return successful response.
     * Otherwise, returns error response.
     *
     * @param command The {@link CreateAccountCommand} command
     * @param session The client session
     * @return The {@link ErrorResponse} if account creation failed, otherwise {@link SuccessResponse}
     */
    @Override
    public Response visit(CreateAccountCommand command, ClientSession session) {
        try {
            eventRepository.createAccount(command.getUser(), session);
        } catch (CalendarException e) {
            return new ErrorResponse(e.getMessage());
        }

        return new SuccessResponse("Account for account '" + command.getUser().username() + "' created successfully.");
    }

    /**
     * The implementation of the CommandVisitor endpoint for AddEventCommand.
     * Checks if user logged and if so then creates record about event, provided in command,
     *  into the server (xml) event-database and returns successful response.
     * Otherwise, returns error response.
     *
     * @param command The {@link AddEventCommand} command
     * @param session The client session
     * @return The {@link ErrorResponse} if event creation failed, otherwise {@link SuccessResponse}
     */
    @Override
    public Response visit(AddEventCommand command, ClientSession session) {
        if (!session.isLoggedIn()) {
            LOGGER.error("Client session '{}': Attempt to add event while not logged in.", session.getSessionId());
            return new ErrorResponse("You must be logged in to add an event.");
        }

        try {
            long newEventId = eventRepository.addEvent(session.getCurrentUser(), command.getEvent(), session);
            return new SuccessResponse("Event with id '" + newEventId + "' added successfully.");
        } catch (CalendarException | IOException e) {
            return new ErrorResponse(e.getMessage());
        }
    }

    /**
     * The implementation of the CommandVisitor endpoint for LogoutCommand.
     * Checks if user logged and if so, logs out user and returns successful response.
     * Otherwise, returns error response.
     *
     * @param command The {@link LogoutCommand} command
     * @param session The client session
     * @return The {@link ErrorResponse} if logout failed, otherwise {@link SuccessLogoutResponse}
     */
    @Override
    public Response visit(LogoutCommand command, ClientSession session) {
        if (!session.isLoggedIn()) {
            LOGGER.error("Client session '{}': Attempt to logout while not logged in.", session.getSessionId());
            return new ErrorResponse("You are not logged in.");
        }

        session.unsetCurrentUser();
        LOGGER.info("Client session '{}': User '{}' logged out.", session.getSessionId(), session.getCurrentUser().username());
        return new SuccessLogoutResponse("Logout successful.");
    }

    /**
     * The implementation of the CommandVisitor endpoint for HelpCommand.
     * Generates a help message with all available commands and their descriptions.
     *
     * @param command The {@link HelpCommand} command
     * @param context The client session
     * @return The {@link ErrorResponse} if help message generation failed, otherwise {@link SuccessResponse}
     */
    @Override
    public Response visit(HelpCommand command, ClientSession context) {
        StringBuilder helpMessage = new StringBuilder();
        helpMessage.append("Available commands:\n");

        for (var commandClass : command.getAvailableCommands().values()) {
            try {
                Constructor<? extends Command> constructor = commandClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                Command tempCommand = constructor.newInstance();

                Privileges currentPrivileges = context.isLoggedIn() ? Privileges.LOGGED : Privileges.UNLOGGED;
                if (tempCommand.getPrivileges() == Privileges.ALL || tempCommand.getPrivileges() == currentPrivileges) {
                    helpMessage.append(tempCommand.getName()).append(": ").append(tempCommand.getDescription()).append("\n");
                }
            } catch (Exception e) {
                LOGGER.error(e);
                return new ErrorResponse("Failed to generate help message.");
            }
        }

        LOGGER.info("Client session '{}': Help message generated successfully.", context.getSessionId());
        return new SuccessResponse(helpMessage.toString());
    }

    /**
     * The implementation of the CommandVisitor endpoint for ShowEventsCommand.
     * Checks if user logged and if so, retrieves all events from the server (xml) event-database
     *  and returns them in a successful response.
     * Otherwise, returns error response.
     *
     * @param command The {@link ShowEventsCommand} command
     * @param session The client session
     * @return The {@link ErrorResponse} if event retrieval failed, otherwise {@link SuccessEventListResponse}
     */
    @Override
    public Response visit(ShowEventsCommand command, ClientSession session) {
        if (!session.isLoggedIn()) {
            LOGGER.error("Client session '{}': Attempt to show events while not logged in.", session.getSessionId());
            return new ErrorResponse("You must be logged in to show events.");
        }

        try {
            var events = eventRepository.getAllEvents(session.getCurrentUser(), session);
            return new SuccessEventListResponse(events);
        } catch (XmlDatabaseException e) {
            return new ErrorResponse("Failed to retrieve events: " + e.getMessage());
        }
    }

    /**
     * The implementation of the CommandVisitor endpoint for DeleteEventCommand.
     * Checks if user logged and if so, deletes event with id provided in command
     *  from the server (xml) event-database and returns successful response.
     * Otherwise, returns error response.
     *
     * @param command The {@link DeleteEventCommand} command
     * @param session The client session
     * @return The {@link ErrorResponse} if event deletion failed, otherwise {@link SuccessResponse}
     */
    @Override
    public Response visit(DeleteEventCommand command, ClientSession session) {
        if (!session.isLoggedIn()) {
            LOGGER.error("Client session '{}': Attempt to delete event while not logged in.", session.getSessionId());
            return new ErrorResponse("You must be logged in to delete an event.");
        }

        try {
            eventRepository.deleteEvent(session.getCurrentUser(), command.getEventId(), session);
            return new SuccessResponse("Event with id '" + command.getEventId() + "' deleted successfully.");
        } catch (XmlDatabaseException e) {
            return new ErrorResponse("Failed to delete event: " + e.getMessage());
        }
    }

    /**
     * The implementation of the CommandVisitor endpoint for UpdateEventCommand.
     * Checks if user logged and if so, updates event with id provided in command
     *  in the server (xml) event-database and returns successful response.
     * Otherwise, returns error response.
     *
     * @param command The {@link UpdateEventCommand} command
     * @param session The client session
     * @return The {@link ErrorResponse} if event update failed, otherwise {@link SuccessResponse}
     */
    @Override
    public Response visit(UpdateEventCommand command, ClientSession session) {
        if (!session.isLoggedIn()) {
            LOGGER.error("Client session '{}': Attempt to update event while not logged in.", session.getSessionId());
            return new ErrorResponse("You must be logged in to update an event.");
        }

        try {
            eventRepository.updateEvent(session.getCurrentUser(), command.getEvent(), session);
            return new SuccessResponse("Event with id '" + command.getEvent().getId() + "' updated successfully.");
        } catch (XmlDatabaseException e) {
            return new ErrorResponse("Failed to update event: " + e.getMessage());
        }
    }

    /**
     * The implementation of the CommandVisitor endpoint for QuitCommand.
     * Terminates the client session and returns a successful response.
     *
     * @param command The {@link QuitCommand} command
     * @param session The client session
     * @return The {@link SuccessQuit} response
     */
    @Override
    public Response visit(QuitCommand command, ClientSession session) {
        session.terminate();
        LOGGER.info("Client session '{}': Quitting.", session.getSessionId());
        return new SuccessQuit();
    }
}
