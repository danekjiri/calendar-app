/**
 * The core module contains shared components and utilities for the calendar application.
 * It includes models, commands, responses, and exceptions used across the client and server.
 */
module cz.cuni.mff.danekji.calendar.core {
    requires org.apache.logging.log4j;
    requires org.jdom2;

    exports cz.cuni.mff.danekji.calendar.core.commands;
    exports cz.cuni.mff.danekji.calendar.core.responses;
    exports cz.cuni.mff.danekji.calendar.core.models;
    exports cz.cuni.mff.danekji.calendar.core.exceptions;
    exports cz.cuni.mff.danekji.calendar.core.exceptions.client;
    exports cz.cuni.mff.danekji.calendar.core.exceptions.server;
    exports cz.cuni.mff.danekji.calendar.core.xml;
    exports cz.cuni.mff.danekji.calendar.core.responses.error;
    exports cz.cuni.mff.danekji.calendar.core.responses.success;
    exports cz.cuni.mff.danekji.calendar.core.client.ui;
    exports cz.cuni.mff.danekji.calendar.core.client.session;

    opens cz.cuni.mff.danekji.calendar.core.commands;
    exports cz.cuni.mff.danekji.calendar.core.client;
}