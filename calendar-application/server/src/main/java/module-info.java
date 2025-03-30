module com.tesco.calendar.server {
    requires com.tesco.calendar.core;
    requires java.logging;
    exports com.tesco.calendar.server;
    exports com.tesco.calendar.server.storage;
}