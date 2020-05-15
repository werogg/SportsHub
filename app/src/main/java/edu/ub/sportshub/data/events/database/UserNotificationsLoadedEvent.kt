package edu.ub.sportshub.data.events.database

import edu.ub.sportshub.models.Notification
import edu.ub.sportshub.models.User

class UserNotificationsLoadedEvent(val notifications : MutableList<Pair<Notification, User>>) : DataEvent {
}