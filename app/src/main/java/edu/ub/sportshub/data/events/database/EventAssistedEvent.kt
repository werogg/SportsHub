package edu.ub.sportshub.data.events.database

import edu.ub.sportshub.models.Event
import edu.ub.sportshub.models.User

class EventAssistedEvent(val event: Event, val user: User) :
    DataEvent {
}