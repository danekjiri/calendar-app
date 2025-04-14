package cz.cuni.mff.danekji1.calendar.core.responses;

//  might be record?
public final class ErrorResponse implements Response {
    private final String errorMessage;

    public ErrorResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() { return errorMessage; }

    /**
     * Helps to handle the command on the client side when displaying response.
     */
    @Override
    public <R> R accept(ResponseVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
