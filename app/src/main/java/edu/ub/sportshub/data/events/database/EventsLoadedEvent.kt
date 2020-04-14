package edu.ub.sportshub.data.events.database

import edu.ub.sportshub.data.events.DataEvent
import edu.ub.sportshub.models.Event
import edu.ub.sportshub.models.User

class EventsLoadedEvent(val eventList: MutableList<Event>, val owner: User) : DataEvent {
}