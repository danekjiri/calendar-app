package cz.cuni.mff.danekji.calendar.core.models;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.jdom2.Element;
import java.time.LocalDate;
import java.time.LocalTime;

public class EventTest {

    @Test
    public void toXMLElement_generatesCorrectXml() {
        // arrange
        Event event = new Event(1L, "Party", LocalDate.of(2025, 12, 31), LocalTime.of(20, 0), "Home", "New Year");

        // act
        Element xml = event.toXMLElement();

        // assert
        assertEquals("event", xml.getName());
        assertEquals("1", xml.getChildText("id"));
        assertEquals("Party", xml.getChildText("title"));
    }

    @Test
    public void fromXMLElement_parsesXml() {
        // arrange
        Element xml = new Element("event")
                .addContent(new Element("id").setText("1"))
                .addContent(new Element("title").setText("Party"))
                .addContent(new Element("date").setText("2025-12-31"))
                .addContent(new Element("time").setText("20:00"))
                .addContent(new Element("location").setText("Home"))
                .addContent(new Element("description").setText("New Year"));

        // act
        Event event = Event.fromXMLElement(xml);

        // assert
        assertEquals(1L, event.getId());
        assertEquals("Party", event.getTitle());
    }
}