package com.tesco.calendar.core.commands;

/**
 * Visitor interface for processing commands on server side.
 */
public interface CommandVisitor<R> {
    R visit(LoginCommand command);
    R visit(CreateAccountCommand command);
    R visit(AddEventCommand command);
    // more to be added as needed...
}
