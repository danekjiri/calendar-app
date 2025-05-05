package cz.cuni.mff.danekji.calendar.core.xml;

/**
 * This class contains constants for XML tags used in the calendar application.
 * The tags are used to represent various elements in the XML structure of the calendar data.
 */
public final class XMLCalendarTags {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private XMLCalendarTags() {}

    /**
     * The root tag for the calendar XML.
     */
    public static final String CALENDAR_TAG = "calendar";

    /**
     * The tag for the user element in the XML.
     */
    public static final String USER_TAG = "user";

    /**
     * The tag for the username element in the XML.
     */
    public static final String USERNAME_TAG = "username";

    /**
     * The tag for the password hash element in the XML.
     */

    public static final String PASSWORD_HASH_TAG = "passwordHash";
    /**
     * The tag for the next event ID element in the XML.
     */

    public static final String NEXT_EVENT_ID_TAG = "nextEventId";

    /**
     * The tag for the events element in the XML.
     */
    public static final String EVENTS_TAG = "events";

    /**
     * The tag for an individual event element in the XML.
     */
    public static final String EVENT_TAG = "event";

    /**
     * The tag for the ID element of an event in the XML.
     */
    public static final String ID_TAG = "id";

    /**
     * The tag for the title element of an event in the XML.
     */
    public static final String TITLE_TAG = "title";

    /**
     * The tag for the date element of an event in the XML.
     */
    public static final String DATE_TAG = "date";

    /**
     * The tag for the time element of an event in the XML.
     */
    public static final String TIME_TAG = "time";

    /**
     * The tag for the location element of an event in the XML.
     */
    public static final String LOCATION_TAG = "location";

    /**
     * The tag for the description element of an event in the XML.
     */
    public static final String DESCRIPTION_TAG = "description";
}
