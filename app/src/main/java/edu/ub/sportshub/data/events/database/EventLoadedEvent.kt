package edu.ub.sportshub.data.events.database

import edu.ub.sportshub.data.events.DataEvent
import edu.ub.sportshub.models.Event

class EventLoadedEvent(val event: Event) : DataEvent {
}