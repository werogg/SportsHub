package edu.ub.sportshub.data.events.database

import edu.ub.sportshub.data.events.DataEvent
import edu.ub.sportshub.models.User

class UserLoadedEvent(val user: User) : DataEvent {
}