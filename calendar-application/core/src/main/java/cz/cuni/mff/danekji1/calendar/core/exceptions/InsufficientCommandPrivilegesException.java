package cz.cuni.mff.danekji1.calendar.core.exceptions;

import cz.cuni.mff.danekji1.calendar.core.exceptions.client.ClientException;

/**
 * Exception thrown when a user does not have sufficient privileges to execute a command.
 * <p>
 * This exception is a subclass of {@link CalendarException} and is used to indicate
 * that the user does not have the required privileges to execute a specific command.
 */
public class InsufficientCommandPrivilegesException extends CalendarException {
    public InsufficientCommandPrivilegesException(String message) {
        super(message);
    }
}
