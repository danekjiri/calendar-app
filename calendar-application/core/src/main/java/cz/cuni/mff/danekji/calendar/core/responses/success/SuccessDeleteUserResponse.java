package cz.cuni.mff.danekji.calendar.core.responses.success;

import cz.cuni.mff.danekji.calendar.core.responses.Response;
import cz.cuni.mff.danekji.calendar.core.responses.ResponseVisitor;

import java.io.IOException;

/**
 * Represents a successful user deletion response.
 * This response is used when the client successfully deletes a user account.
 *
 * @param message The message indicating the success of the deletion operation.
 */
public record SuccessDeleteUserResponse(String message) implements Response {

    @Override
    public <R, S> R accept(ResponseVisitor<R, S> visitor, S session) throws IOException {
        return visitor.visit(this, session);
    }
}