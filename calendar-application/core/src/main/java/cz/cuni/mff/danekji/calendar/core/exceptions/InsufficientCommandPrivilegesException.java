package cz.cuni.mff.danekji.calendar.core.exceptions;

/**
 * Exception thrown when a user does not have sufficient privileges to execute a command.
 * <p>
 * This exception is a subclass of {@link CalendarException} and is used to indicate
 * that the user does not have the required privileges to execute a specific command.
 */
public class InsufficientCommandPrivilegesException extends CalendarException {
    /**
     * Constructs a new InsufficientCommandPrivilegesException with the specified detail message.
     *
     * @param message the detail message
     */
    public InsufficientCommandPrivilegesException(String message) {
        super(message);
    }
}
