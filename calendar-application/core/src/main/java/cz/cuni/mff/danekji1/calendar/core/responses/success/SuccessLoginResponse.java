package cz.cuni.mff.danekji1.calendar.core.responses.success;

import cz.cuni.mff.danekji1.calendar.core.models.User;
import cz.cuni.mff.danekji1.calendar.core.responses.Response;
import cz.cuni.mff.danekji1.calendar.core.responses.ResponseVisitor;

import java.io.IOException;

public record SuccessLoginResponse(String message, User user) implements Response {

    /**
     * Helps to handle the command on the client side when displaying response.
     */
    @Override
    public <R, C> R accept(ResponseVisitor<R, C> visitor, C context) throws IOException {
        return visitor.visit(this, context);
    }
}
