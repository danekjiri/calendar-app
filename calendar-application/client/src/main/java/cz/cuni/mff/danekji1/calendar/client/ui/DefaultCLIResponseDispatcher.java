package cz.cuni.mff.danekji1.calendar.client.ui;

import cz.cuni.mff.danekji1.calendar.core.responses.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultCLIResponseDispatcher implements ResponseVisitor<Void> {
    private static final Logger LOGGER = LogManager.getLogger(DefaultCLIResponseDispatcher.class);

    private final CLIUserInterface ui;

    public DefaultCLIResponseDispatcher(CLIUserInterface ui) {
        this.ui = ui;
    }

    @Override
    public Void visit(SuccessResponse response) {
        LOGGER.info("Success: {}", response.message());
        return null;
    }

    @Override
    public Void visit(SuccessLoginResponse response) {
        LOGGER.info("Success: {}", response.message());
        ui.setUser(response.user());
        return null;
    }

    @Override
    public Void visit(ErrorResponse response) {
        LOGGER.error("Error: {}", response.getErrorMessage());
        return null;
    }

    @Override
    public Void visit(EventListResponse response) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
