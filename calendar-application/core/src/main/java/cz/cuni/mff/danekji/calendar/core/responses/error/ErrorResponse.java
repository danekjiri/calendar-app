package cz.cuni.mff.danekji.calendar.core.responses.error;

import cz.cuni.mff.danekji.calendar.core.responses.Response;
import cz.cuni.mff.danekji.calendar.core.responses.ResponseVisitor;

import java.io.IOException;

/**
 * Represents an error response from the server.
 * This response contains an error message indicating what went wrong.
 *
 * @param errorMessage The error message describing the issue.
 */
public record ErrorResponse(String errorMessage) implements Response {

    /**
     * {@inheritDoc}
     */
    @Override
    public <R, S> R accept(ResponseVisitor<R, S> visitor, S session) throws IOException {
        return visitor.visit(this, session);
    }
}
