module cz.cuni.mff.danekji.calendar.client {
    requires cz.cuni.mff.danekji.calendar.core;
    requires org.apache.logging.log4j;
    requires jdk.jshell;

    exports cz.cuni.mff.danekji1.calendar.client.ui;
    exports cz.cuni.mff.danekji1.calendar.client;
}