package cz.cuni.mff.danekji.calendar.core.responses.success;

import cz.cuni.mff.danekji.calendar.core.responses.Response;
import cz.cuni.mff.danekji.calendar.core.responses.ResponseVisitor;

import java.io.IOException;

/**
 * Represents a successful quit response.
 * This response is used when the client successfully quits the application.
 *
 */
public record SuccessQuit() implements Response {

    /**
     * {@inheritDoc}
     */
    @Override
    public <R, S> R accept(ResponseVisitor<R, S> visitor, S session) throws IOException {
        return visitor.visit(this, session);
    }
}
