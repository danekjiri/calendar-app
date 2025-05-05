package cz.cuni.mff.danekji1.calendar.core.responses.success;

import cz.cuni.mff.danekji1.calendar.core.responses.Response;
import cz.cuni.mff.danekji1.calendar.core.responses.ResponseVisitor;

import java.io.IOException;

public record SuccessResponse(String message) implements Response {

    /**
     * Helps to handle the command on the client side when displaying response.
     */
    @Override
    public <R, C> R accept(ResponseVisitor<R, C> visitor, C session) throws IOException {
        return visitor.visit(this, session);
    }
}
