module cz.cuni.mff.danekji.calendar.core {
    requires org.apache.logging.log4j;
    requires org.jdom2;
    requires jdk.httpserver;

    exports cz.cuni.mff.danekji1.calendar.core.commands;
    exports cz.cuni.mff.danekji1.calendar.core.responses;
    exports cz.cuni.mff.danekji1.calendar.core.models;
    exports cz.cuni.mff.danekji1.calendar.core.exceptions;
    exports cz.cuni.mff.danekji1.calendar.core.exceptions.client;
    exports cz.cuni.mff.danekji1.calendar.core.exceptions.server;
    exports cz.cuni.mff.danekji1.calendar.core.xml;
    exports cz.cuni.mff.danekji1.calendar.core.responses.error;
    exports cz.cuni.mff.danekji1.calendar.core.responses.success;
    exports cz.cuni.mff.danekji1.calendar.core.ui;
    exports cz.cuni.mff.danekji1.calendar.core;

    opens cz.cuni.mff.danekji1.calendar.core.commands
            to cz.cuni.mff.danekji.calendar.client;
}