package cz.cuni.mff.danekji.calendar.server;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import cz.cuni.mff.danekji.calendar.core.responses.success.SuccessLogoutResponse;
import org.junit.jupiter.api.Test;
import cz.cuni.mff.danekji.calendar.core.commands.*;
import cz.cuni.mff.danekji.calendar.core.responses.Response;
import cz.cuni.mff.danekji.calendar.core.responses.success.SuccessLoginResponse;
import cz.cuni.mff.danekji.calendar.core.responses.success.SuccessResponse;
import cz.cuni.mff.danekji.calendar.core.responses.error.ErrorResponse;
import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.models.User;
import cz.cuni.mff.danekji.calendar.core.models.Event;
import cz.cuni.mff.danekji.calendar.server.storage.EventRepository;

public class DefaultCommandDispatcherTest {

    @Test
    public void visitLoginCommand_authenticatesUser() {
        // arrange
        EventRepository mockRepo = mock(EventRepository.class);
        DefaultCommandDispatcher dispatcher = new DefaultCommandDispatcher(mockRepo);
        ClientSession session = new ClientSession(42, null);

        User user = new User("alice", 123);
        LoginCommand command = new LoginCommand(user);

        when(mockRepo.authenticate(user, session)).thenReturn(true);

        // act
        Response response = dispatcher.visit(command, session);

        // assert
        assertInstanceOf(SuccessLoginResponse.class, response, "Should return success response on valid login");
        assertEquals(user, session.getCurrentUser(), "Session should set the authenticated user");
    }

    @Test
    public void visitAddEventCommand_addsEventIfLoggedIn() throws Exception {
        // arrange
        EventRepository mockRepo = mock(EventRepository.class);
        DefaultCommandDispatcher dispatcher = new DefaultCommandDispatcher(mockRepo);
        ClientSession session = new ClientSession(42, null);
        User user = new User("alice", 123);
        Event event = new Event("Meeting", null, null, null, null);

        // act
        session.setCurrentUser(user);
        AddEventCommand command = new AddEventCommand(event);

        when(mockRepo.addEvent(user, event, session)).thenReturn(1L);

        Response response = dispatcher.visit(command, session);

        // assert
        assertInstanceOf(SuccessResponse.class, response, "Should return success response when event is added");
        assertTrue(((SuccessResponse) response).message().contains("added successfully"), "Response message should confirm success");
    }

    @Test
    public void visitAddEventCommand_failsIfNotLoggedIn() {
        // arrange
        EventRepository mockRepo = mock(EventRepository.class);
        DefaultCommandDispatcher dispatcher = new DefaultCommandDispatcher(mockRepo);
        ClientSession session = new ClientSession(42, null);
        AddEventCommand command = new AddEventCommand(new Event("Meeting", null, null, null, null));

        // act
        Response response = dispatcher.visit(command, session);

        // assert
        assertInstanceOf(ErrorResponse.class, response, "Should return error response when not logged in");
        assertEquals("You must be logged in to add an event.", ((ErrorResponse) response).errorMessage(), "Error message should indicate login requirement");
    }

}