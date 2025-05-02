package cz.cuni.mff.danekji1.calendar.core.responses.success;

import cz.cuni.mff.danekji1.calendar.core.responses.Response;
import cz.cuni.mff.danekji1.calendar.core.responses.ResponseVisitor;

public record SuccessLogoutResponse(String message) implements Response {

    @Override
    public <R> R accept(ResponseVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
