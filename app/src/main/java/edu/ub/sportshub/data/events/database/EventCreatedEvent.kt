package edu.ub.sportshub.data.events.database

import edu.ub.sportshub.data.enums.CreateEventResult
import edu.ub.sportshub.models.Event

class EventCreatedEvent(val result: CreateEventResult) : DataEvent {

    private var eventId : String? = null

    constructor(result: CreateEventResult, eventId: String?) : this(result) {
        this.eventId = eventId
    }

    fun getEventId() : String? {
        return eventId
    }
}