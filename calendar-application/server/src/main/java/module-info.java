/**
 * The server module manages client connections, processes commands, and handles data persistence.
 * It includes the server implementation and storage mechanisms for the calendar application.
 */
module cz.cuni.mff.danekji.calendar.server {
    requires cz.cuni.mff.danekji.calendar.core;
    requires java.logging;
    requires org.apache.logging.log4j;
    requires org.jdom2;

    exports  cz.cuni.mff.danekji.calendar.server;
}