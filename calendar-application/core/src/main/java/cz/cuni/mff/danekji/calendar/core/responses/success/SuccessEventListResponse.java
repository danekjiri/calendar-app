package cz.cuni.mff.danekji.calendar.core.responses.success;

import cz.cuni.mff.danekji.calendar.core.models.Event;
import cz.cuni.mff.danekji.calendar.core.responses.Response;
import cz.cuni.mff.danekji.calendar.core.responses.ResponseVisitor;

import java.io.IOException;
import java.util.List;

/**
 * Represents a successful response containing a list of events.
 * This response is used when the client requests a list of events from the server.
 *
 * @param events The list of events returned in the response.
 */
public record SuccessEventListResponse(List<Event> events) implements Response {

    /**
     * {@inheritDoc}
     */
    @Override
    public <R, S> R accept(ResponseVisitor<R, S> visitor, S session) throws IOException {
        return visitor.visit(this, session);
    }
}
