package edu.ub.sportshub.data.events.database

import edu.ub.sportshub.models.Event
import edu.ub.sportshub.models.User

class EventLikedEvent(val event: Event, val user: User, val type: EventLikedEvent.Type) :
    DataEvent {

    enum class Type {
        LIKED,
        REMOVED_LIKE,
    }
}