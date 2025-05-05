package cz.cuni.mff.danekji.calendar.core.exceptions.client;

/**
 * Exception thrown when an unknown command is encountered.
 * <p>
 * This exception is a subclass of {@link ClientException} and is used to indicate
 * errors related to unrecognized commands in the calendar application.
 */
public class UnknownCommandException extends ClientException {
    /**
     * The unknown command that caused this exception.
     * <p>
     * This field stores the command that was not recognized by the application.
     * It is used to provide additional context when handling the exception.
     */
    private final String command;

    /**
     * Constructs a new UnknownCommandException with the specified detail message and command.
     *
     * @param message the detail message
     * @param command the unknown command
     */
    public UnknownCommandException(String message, String command) {
        super(message);
        this.command = command;
    }

    /**
     * Constructs a new UnknownCommandException with the specified command.
     *
     * @param command the unknown command
     */
    public UnknownCommandException(String command) {
        this("Unknown command: " + command, command);
    }

    /**
     * Returns the unknown command that caused this exception.
     *
     * @return the unknown command
     */
    public String getCommand() {
        return command;
    }
}
