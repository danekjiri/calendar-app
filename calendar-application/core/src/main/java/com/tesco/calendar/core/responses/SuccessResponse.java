package com.tesco.calendar.core.responses;

//  might be record?
public final class SuccessResponse implements Response {
    private final String message;

    public SuccessResponse(String message) {
        this.message = message;
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
