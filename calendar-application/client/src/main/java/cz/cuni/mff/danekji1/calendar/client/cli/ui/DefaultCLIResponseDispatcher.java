package cz.cuni.mff.danekji1.calendar.client.cli.ui;

import cz.cuni.mff.danekji1.calendar.core.responses.*;
import cz.cuni.mff.danekji1.calendar.core.responses.error.ErrorResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.error.EventListResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.success.SuccessLoginResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.success.SuccessLogoutResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.success.SuccessResponse;
import cz.cuni.mff.danekji1.calendar.core.ui.ClientState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultCLIResponseDispatcher implements ResponseVisitor<Void, ClientState> {
    private static final Logger LOGGER = LogManager.getLogger(DefaultCLIResponseDispatcher.class);

    @Override
    public Void visit(SuccessResponse response, ClientState session) {
        LOGGER.info("Success: {}", response.message());
        return null;
    }

    @Override
    public Void visit(SuccessLoginResponse response, ClientState session) {
        LOGGER.info("Success: {}", response.message());
        session.setCurrentUser(response.user());
        return null;
    }

    @Override
    public Void visit(SuccessLogoutResponse response, ClientState session) {
        LOGGER.info("Success: {}", response.message());
        session.unsetCurrentUser();
        return null;
    }

    @Override
    public Void visit(ErrorResponse response, ClientState session) {
        LOGGER.error("Error: {}", response.getErrorMessage());
        return null;
    }

    @Override
    public Void visit(EventListResponse response, ClientState session) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
