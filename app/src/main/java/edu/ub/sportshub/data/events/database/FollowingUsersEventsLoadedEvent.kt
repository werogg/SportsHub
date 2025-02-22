package edu.ub.sportshub.data.events.database

import edu.ub.sportshub.models.Event
import edu.ub.sportshub.models.User

class FollowingUsersEventsLoadedEvent(val eventList: MutableList<Pair<Event, User>>) :
    DataEvent {
}