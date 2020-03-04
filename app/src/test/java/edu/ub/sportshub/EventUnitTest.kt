package edu.ub.sportshub

import edu.ub.sportshub.models.Event
import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 * Event related Unit tests.
 */
class EventUnitTest {

    private var event : Event = Event(
        "123", "Test Event", "Test Description Event",
        mutableListOf("fwqf"), Date(2010, 5, 23), Calendar.getInstance().time,
        false
    )

    /**
     * Test if isCompleted function is working.
     */
    @Test
    fun isCompleted_isCorrect() {
        assertEquals(true, event.isCompleted())
    }
}
