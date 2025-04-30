module cz.cuni.mff.danekji.calendar.core {
    requires org.apache.logging.log4j;

    exports cz.cuni.mff.danekji1.calendar.core.commands;
    exports cz.cuni.mff.danekji1.calendar.core.responses;
    exports cz.cuni.mff.danekji1.calendar.core.models;
    exports cz.cuni.mff.danekji1.calendar.core.exceptions;
    exports cz.cuni.mff.danekji1.calendar.core.exceptions.client;
    exports cz.cuni.mff.danekji1.calendar.core.exceptions.server;
}