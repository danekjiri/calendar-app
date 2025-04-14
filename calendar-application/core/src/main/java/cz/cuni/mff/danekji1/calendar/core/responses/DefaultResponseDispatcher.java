package cz.cuni.mff.danekji1.calendar.core.responses;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultResponseDispatcher implements ResponseVisitor<Void> {
    private static final Logger LOGGER = LogManager.getLogger(DefaultResponseDispatcher.class);

    @Override
    public Void visit(SuccessResponse response) {
        LOGGER.info("Success: {}", response.getMessage());
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
