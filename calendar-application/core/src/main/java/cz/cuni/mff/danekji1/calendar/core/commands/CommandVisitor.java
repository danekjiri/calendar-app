package cz.cuni.mff.danekji1.calendar.core.commands;

/**
 * Visitor interface for processing commands on server side.
 */
public interface CommandVisitor<R, C> {
    R visit(LoginCommand command, C context);
    R visit(CreateAccountCommand command, C context);
    R visit(AddEventCommand command, C context);
    R visit(LogoutCommand command, C context);
    R visit(HelpCommand command, C context);
    R visit(ShowEventsCommand command, C context);
    R visit(DeleteEventCommand command, C context);
    // more to be added as needed...
}
