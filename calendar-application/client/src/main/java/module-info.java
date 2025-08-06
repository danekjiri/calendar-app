/**
 * The client module provides the command-line interface for interacting with the calendar server.
 * It handles user input, command parsing, and communication with the server.
 */
module cz.cuni.mff.danekji.calendar.client {
    requires cz.cuni.mff.danekji.calendar.core;
    requires org.apache.logging.log4j;
    requires javafx.fxml;
    requires javafx.controls;
    requires java.logging;

    opens cz.cuni.mff.danekji.calendar.client.gui.controllers to javafx.fxml;

    exports cz.cuni.mff.danekji.calendar.client.cli;
    exports cz.cuni.mff.danekji.calendar.client.cli.ui;
    exports cz.cuni.mff.danekji.calendar.client.network;
    exports cz.cuni.mff.danekji.calendar.client.gui;
}