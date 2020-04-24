package edu.ub.sportshub.data.events.database

import edu.ub.sportshub.models.User

class UserLoadedEvent(val user: User) :
    DataEvent {
}