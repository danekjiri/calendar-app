package cz.cuni.mff.danekji1.calendar.client.cli.ui;

import cz.cuni.mff.danekji1.calendar.core.responses.*;
import cz.cuni.mff.danekji1.calendar.core.responses.error.ErrorResponse;
import cz.cuni.mff.danekji1.calendar.core.responses.success.*;
import cz.cuni.mff.danekji1.calendar.core.session.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class DefaultCLIResponseDispatcher implements ResponseVisitor<Void, Session> {
    private static final Logger LOGGER = LogManager.getLogger(DefaultCLIResponseDispatcher.class);

    private final OutputStreamWriter output;

    public DefaultCLIResponseDispatcher(OutputStream output) {
        this.output = new OutputStreamWriter(output);
    }

    @Override
    public Void visit(SuccessResponse response, Session session) throws IOException {
        output.write(response.message() + "\n");
        output.flush();
        return null;
    }

    @Override
    public Void visit(SuccessLoginResponse response, Session session) throws IOException {
        output.write(response.message() + "\n");
        session.setCurrentUser(response.user());
        output.flush();
        return null;
    }

    @Override
    public Void visit(SuccessLogoutResponse response, Session session) throws IOException {
        output.write(response.message() + "\n");
        session.unsetCurrentUser();
        output.flush();
        return null;
    }

    @Override
    public Void visit(SuccessEventListResponse response, Session session) throws IOException {
        output.write("Event list:\n");
        for (var event : response.events()) {
            output.write("\t");
            output.write(event.toString());
            output.write("\n");
        }
        output.flush();
        return null;
    }

    @Override
    public Void visit(SuccessQuit response, Session context) throws IOException {
        output.write("Quitting...\n");
        output.flush();
        context.deactivate();
        return null;
    }

    @Override
    public Void visit(ErrorResponse response, Session session) throws IOException {
        output.write(response.errorMessage() + "\n");
        output.flush();
        return null;
    }
}
