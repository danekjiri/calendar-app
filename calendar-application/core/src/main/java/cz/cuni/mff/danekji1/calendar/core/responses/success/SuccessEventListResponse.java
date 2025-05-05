package cz.cuni.mff.danekji1.calendar.core.responses.success;

import cz.cuni.mff.danekji1.calendar.core.models.Event;
import cz.cuni.mff.danekji1.calendar.core.responses.Response;
import cz.cuni.mff.danekji1.calendar.core.responses.ResponseVisitor;

import java.io.IOException;
import java.util.List;

public record SuccessEventListResponse(List<Event> events) implements Response {

    /**
     * {@inheritDoc}
     */
    @Override
    public <R, S> R accept(ResponseVisitor<R, S> visitor, S session) throws IOException {
        return visitor.visit(this, session);
    }
}
