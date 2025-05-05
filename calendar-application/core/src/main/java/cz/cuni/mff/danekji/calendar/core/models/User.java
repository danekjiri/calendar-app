package cz.cuni.mff.danekji.calendar.core.models;

import cz.cuni.mff.danekji.calendar.core.xml.XMLCalendarTags;
import org.jdom2.Element;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a user in the system.
 * Contains username and password hash.
 * @param username The username of the user.
 * @param passwordHash The hash of the user's password.
 */
public record User(String username, int passwordHash) implements Serializable {

    /**
     * Coverts the User object to an XML element.
     *
     * @return The XML element representing the user.
     */
    public Element toXMLElement() {
        Element userElement = new Element(XMLCalendarTags.USER_TAG);

        Element usernameElement = new Element(XMLCalendarTags.USERNAME_TAG);
        usernameElement.setText(String.valueOf(username));
        Element passwordHashElement = new Element(XMLCalendarTags.PASSWORD_HASH_TAG);
        passwordHashElement.setText(Integer.toString(passwordHash));

        userElement.addContent(List.of(usernameElement, passwordHashElement));
        return userElement;
    }
}
