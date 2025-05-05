package cz.cuni.mff.danekji1.calendar.core.responses.success;

import cz.cuni.mff.danekji1.calendar.core.responses.Response;
import cz.cuni.mff.danekji1.calendar.core.responses.ResponseVisitor;

import java.io.IOException;

public record SuccessLogoutResponse(String message) implements Response {

    @Override
    public <R, C> R accept(ResponseVisitor<R, C> visitor, C session) throws IOException {
        return visitor.visit(this, session);
    }
}
