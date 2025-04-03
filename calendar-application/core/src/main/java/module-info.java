module com.tesco.calendar.core {
    exports com.tesco.calendar.core.commands
            to com.tesco.calendar.server,
            com.tesco.calendar.client;
    exports com.tesco.calendar.core.responses
            to com.tesco.calendar.server,
            com.tesco.calendar.client;
    exports com.tesco.calendar.core.models
            to com.tesco.calendar.server,
            com.tesco.calendar.client;
}