package edu.ub.sportshub.models

import android.content.Context
import android.content.res.Resources
import com.google.firebase.Timestamp
import edu.ub.sportshub.R
import edu.ub.sportshub.data.enums.NotificationType

class NotificationLiked(
    id: String,
    uid: String,
    date: Timestamp,
    creatorUid: String,
    checked: Boolean,
    notificationType : NotificationType,
    private var eventName: String
) : Notification(
    id,
    uid,
    date,
    creatorUid,
    checked,
    notificationType
), INotification {

    constructor() : this(
        "", "", Timestamp.now(), "", false, NotificationType.FOLLOWED, ""
    )

    fun getEventName() : String {
        return eventName
    }

    override fun getMessage(context : Context, originUsername : String): String {
        return context.getString(R.string.notify_event_liked, originUsername, eventName)
    }
}