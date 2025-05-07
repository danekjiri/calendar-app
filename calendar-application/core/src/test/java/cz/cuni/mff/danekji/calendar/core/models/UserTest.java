package cz.cuni.mff.danekji.calendar.core.models;

import static org.junit.jupiter.api.Assertions.*;

import cz.cuni.mff.danekji.calendar.core.models.User;
import org.junit.jupiter.api.Test;
import org.jdom2.Element;

public class UserTest {

    @Test
    public void toXMLElement_generatesCorrectXml() {
        User user = new User("alice", "secret".hashCode());
        Element xml = user.toXMLElement();

        assertEquals("user", xml.getName());
        assertEquals("alice", xml.getChildText("username"));
        assertEquals(String.valueOf("secret".hashCode()), xml.getChildText("passwordHash"));
    }
}