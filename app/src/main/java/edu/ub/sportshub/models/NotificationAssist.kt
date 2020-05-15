package edu.ub.sportshub.models

import android.content.Context
import android.content.res.Resources
import com.google.firebase.Timestamp
import edu.ub.sportshub.R
import edu.ub.sportshub.data.enums.NotificationType

class NotificationAssist(
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
        return when (getNotificationType()) {
            NotificationType.ASSIST_TO_CREATOR -> {
                context.getString(R.string.notify_assist_to_creator, originUsername, eventName)
            }

            NotificationType.ASSIST_TO_FOLLOWERS -> {
                context.getString(R.string.notify_assist_to_followers, originUsername, eventName)
            }
            else -> "ERROR"
        }
    }
}