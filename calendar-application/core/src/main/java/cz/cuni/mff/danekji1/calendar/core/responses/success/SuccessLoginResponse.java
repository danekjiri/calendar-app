package cz.cuni.mff.danekji1.calendar.core.responses.success;

import cz.cuni.mff.danekji1.calendar.core.models.User;
import cz.cuni.mff.danekji1.calendar.core.responses.Response;
import cz.cuni.mff.danekji1.calendar.core.responses.ResponseVisitor;

public record SuccessLoginResponse(String message, User user) implements Response {

    /**
     * Helps to handle the command on the client side when displaying response.
     */
    @Override
    public <R> R accept(ResponseVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
