package cz.cuni.mff.danekji.calendar.core.responses.success;

import cz.cuni.mff.danekji.calendar.core.models.User;
import cz.cuni.mff.danekji.calendar.core.responses.Response;
import cz.cuni.mff.danekji.calendar.core.responses.ResponseVisitor;

import java.io.IOException;

/**
 * Represents a successful login response.
 * This response is used when the client successfully logs in to the server.
 *
 * @param message The success message returned in the response.
 * @param user    The user object containing user details.
 */
public record SuccessLoginResponse(String message, User user) implements Response {

    /**
     * {@inheritDoc}
     */
    @Override
    public <R, S> R accept(ResponseVisitor<R, S> visitor, S session) throws IOException {
        return visitor.visit(this, session);
    }
}
