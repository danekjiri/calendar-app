package cz.cuni.mff.danekji.calendar.client.cli.ui;

import static org.junit.jupiter.api.Assertions.*;

import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.models.User;
import cz.cuni.mff.danekji.calendar.core.responses.error.ErrorResponse;
import cz.cuni.mff.danekji.calendar.core.responses.success.SuccessLoginResponse;
import cz.cuni.mff.danekji.calendar.core.responses.success.SuccessResponse;
import org.junit.jupiter.api.Test;
import java.io.*;

public class CLIResponseDispatcherTest {

    @Test
    public void visitSuccessResponse_writesMessage() throws IOException {
        // arrange
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CLIResponseDispatcher dispatcher = new CLIResponseDispatcher(outputStream);
        SuccessResponse response = new SuccessResponse("Done!");

        // act
        dispatcher.visit(response, null);

        // assert
        assertTrue(outputStream.toString().contains("Done!"));
    }

    @Test
    public void visitErrorResponse_writesErrorMessage() throws IOException {
        // arrange
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CLIResponseDispatcher dispatcher = new CLIResponseDispatcher(outputStream);
        ErrorResponse response = new ErrorResponse("Failed!");

        // act
        dispatcher.visit(response, null);

        // assert
        assertTrue(outputStream.toString().contains("ERROR - Failed!"));
    }

    @Test
    public void visitSuccessLoginResponse_setsUser() throws IOException {
        // arrange
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CLIResponseDispatcher dispatcher = new CLIResponseDispatcher(outputStream);
        User user = new User("test", 123);
        SuccessLoginResponse response = new SuccessLoginResponse("Logged in", user);
        ClientSession session = new ClientSession(1, null);

        // act
        dispatcher.visit(response, session);

        // assert
        assertEquals(user, session.getCurrentUser());
        assertTrue(outputStream.toString().contains("Logged in"));
    }
}