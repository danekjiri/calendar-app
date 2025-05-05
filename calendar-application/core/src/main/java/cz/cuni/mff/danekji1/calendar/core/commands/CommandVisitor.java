package cz.cuni.mff.danekji1.calendar.core.commands;

/**
 * Visitor interface for processing commands on server side.
 */
public interface CommandVisitor<R, C> {
    R visit(LoginCommand command, C session);
    R visit(CreateAccountCommand command, C session);
    R visit(AddEventCommand command, C session);
    R visit(LogoutCommand command, C session);
    R visit(HelpCommand command, C session);
    R visit(ShowEventsCommand command, C session);
    R visit(DeleteEventCommand command, C session);
    R visit(UpdateEventCommand command, C session);
    R visit(QuitCommand command, C session);
    // more to be added as needed...
}
