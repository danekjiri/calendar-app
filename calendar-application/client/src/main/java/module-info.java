/**
 * The client module provides the command-line interface for interacting with the calendar server.
 * It handles user input, command parsing, and communication with the server.
 */
module cz.cuni.mff.danekji.calendar.client {
    requires cz.cuni.mff.danekji.calendar.core;
    requires org.apache.logging.log4j;

    exports cz.cuni.mff.danekji.calendar.client.cli.ui;
    exports cz.cuni.mff.danekji.calendar.client.cli;
}