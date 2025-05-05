package cz.cuni.mff.danekji.calendar.core.responses.success;

import cz.cuni.mff.danekji.calendar.core.responses.Response;
import cz.cuni.mff.danekji.calendar.core.responses.ResponseVisitor;

import java.io.IOException;

/**
 * Represents a successful response.
 * This response is used when the client successfully completes an operation on the server.
 *
 * @param message The message indicating the success of the operation.
 * @see Response
 */
public record SuccessResponse(String message) implements Response {

    /**
     * {@inheritDoc}
     */
    @Override
    public <R, S> R accept(ResponseVisitor<R, S> visitor, S session) throws IOException {
        return visitor.visit(this, session);
    }
}
