package cz.cuni.mff.danekji.calendar.core.exceptions.server;

/**
 * Exception thrown when a problem occurs while processing XML data in the database.
 * <p>
 * This exception is a subclass of {@link ServerException} and is used to indicate
 *  errors related to XML data handling in the calendar application. Eg: when
 *  the XML data is malformed or cannot be parsed correctly or user does not exist.
 */
public class XmlDatabaseException extends ServerException {
    /**
     * Constructs a new XmlDatabaseException with the specified detail message.
     *
     * @param message the detail message
     */
    public XmlDatabaseException(String message) {
        super(message);
    }
}
