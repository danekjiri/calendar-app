package cz.cuni.mff.danekji.calendar.core.client.session;

import static org.junit.jupiter.api.Assertions.*;

import cz.cuni.mff.danekji.calendar.core.models.User;
import org.junit.jupiter.api.Test;
import java.net.InetSocketAddress;

public class ClientSessionTest {

    @Test
    public void isLoggedIn_returnsFalseInitially() {
        // arrange
        ClientSession session = new ClientSession(1, new InetSocketAddress("localhost", 8080));

        // act
        assertFalse(session.isLoggedIn());
    }

    @Test
    public void setCurrentUser_logsInUser() {
        // arrange
        ClientSession session = new ClientSession(1, new InetSocketAddress("localhost", 8080));
        User user = new User("test", 123);

        // act
        session.setCurrentUser(user);

        // assert
        assertTrue(session.isLoggedIn());
        assertEquals(user, session.getCurrentUser());
    }

    @Test
    public void terminate_makesSessionInactive() {
        // arrange
        ClientSession session = new ClientSession(1, new InetSocketAddress("localhost", 8080));

        // act && assert
        assertTrue(session.isActive());
        session.terminate();
        assertFalse(session.isActive());
    }
}