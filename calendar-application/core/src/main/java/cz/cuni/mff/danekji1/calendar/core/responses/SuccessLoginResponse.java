package cz.cuni.mff.danekji1.calendar.core.responses;

import cz.cuni.mff.danekji1.calendar.core.models.User;

public final class SuccessLoginResponse implements Response {
    private final User user;
    private final String message;

    public SuccessLoginResponse(String message, User user) {
        this.message = message;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public String getMessage() { return message; }

    /**
     * Helps to handle the command on the client side when displaying response.
     */
    @Override
    public <R> R accept(ResponseVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
