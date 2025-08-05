package cz.cuni.mff.danekji.calendar.core.commands;

import cz.cuni.mff.danekji.calendar.core.client.session.ClientSession;
import cz.cuni.mff.danekji.calendar.core.client.ui.UserInterface;
import cz.cuni.mff.danekji.calendar.core.exceptions.InsufficientCommandPrivilegesException;
import cz.cuni.mff.danekji.calendar.core.exceptions.client.InvalidInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShowFutureEventsCommandTest {

    @Mock
    private UserInterface mockUI;

    @Mock
    private ClientSession mockSession;

    private final LocalDate today = LocalDate.of(2025, 8, 5); // A fixed 'today' for predictable tests

    @BeforeEach
    void setUp() {
        when(mockSession.isLoggedIn()).thenReturn(true);
    }

    @Test
    public void buildCommand_calculatesCorrectDateForTomorrow() throws Exception {
        // arrange
        when(mockUI.promptForInput(anyString())).thenReturn("1"); // "Tomorrow"

        // act
        ShowFutureEventsCommand command = (ShowFutureEventsCommand) new ShowFutureEventsCommand().buildCommand(mockUI, mockSession);

        // assert
        LocalDate expectedDate = LocalDate.now().plusDays(1);
        assertEquals(expectedDate, command.getStartDate(), "Start date should be tomorrow.");
        assertEquals(expectedDate, command.getEndDate(), "End date should be tomorrow.");
    }

    @Test
    public void buildCommand_calculatesCorrectDateForThisWeek() throws Exception {
        // arrange
        when(mockUI.promptForInput(anyString())).thenReturn("2"); // "This Week"

        // act
        ShowFutureEventsCommand command = (ShowFutureEventsCommand) new ShowFutureEventsCommand().buildCommand(mockUI, mockSession);

        // assert
        LocalDate expectedStartDate = LocalDate.now();
        LocalDate expectedEndDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        assertEquals(expectedStartDate, command.getStartDate());
        assertEquals(expectedEndDate, command.getEndDate());
    }


    @Test
    public void buildCommand_selectsCustomDateRange() throws Exception {
        // arrange
        when(mockUI.promptForInput(anyString()))
                .thenReturn("5")
                .thenReturn("2025-09-01") // Start date
                .thenReturn("2025-09-30"); // End date

        // act
        ShowFutureEventsCommand command = (ShowFutureEventsCommand) new ShowFutureEventsCommand().buildCommand(mockUI, mockSession);

        // assert
        assertEquals(LocalDate.of(2025, 9, 1), command.getStartDate());
        assertEquals(LocalDate.of(2025, 9, 30), command.getEndDate());
    }

    @Test
    public void buildCommand_throwsForInvalidMenuChoice() throws IOException {
        // arrange
        when(mockUI.promptForInput(anyString())).thenReturn("99");

        // act & assert
        assertThrows(InvalidInputException.class, () ->
                new ShowFutureEventsCommand().buildCommand(mockUI, mockSession));
    }

    @Test
    public void buildCommand_throwsForInvalidCustomDateFormat() throws IOException {
        // arrange
        when(mockUI.promptForInput(anyString()))
                .thenReturn("5")
                .thenReturn("not-a-date");

        // act & assert
        assertThrows(InvalidInputException.class, () ->
                new ShowFutureEventsCommand().buildCommand(mockUI, mockSession));
    }

    @Test
    public void buildCommand_throwsForInvalidCustomDateRange() throws IOException {
        // arrange
        when(mockUI.promptForInput(anyString()))
                .thenReturn("5")
                .thenReturn("2025-10-01") // Start date
                .thenReturn("2025-09-01"); // End date (before start)

        // act & assert
        assertThrows(InvalidInputException.class, () ->
                new ShowFutureEventsCommand().buildCommand(mockUI, mockSession));
    }

    @Test
    public void buildCommand_throwsWhenNotLoggedIn() {
        // arrange
        when(mockSession.isLoggedIn()).thenReturn(false);

        // act & assert
        assertThrows(InsufficientCommandPrivilegesException.class, () ->
                new ShowFutureEventsCommand().buildCommand(mockUI, mockSession));
    }
}