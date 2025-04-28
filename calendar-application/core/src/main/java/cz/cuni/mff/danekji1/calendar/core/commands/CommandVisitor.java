package cz.cuni.mff.danekji1.calendar.core.commands;

/**
 * Visitor interface for processing commands on server side.
 */
public interface CommandVisitor<R, C> {
    R visit(LoginCommand command, C context);
    R visit(CreateAccountCommand command, C context);
    // more to be added as needed...
}
