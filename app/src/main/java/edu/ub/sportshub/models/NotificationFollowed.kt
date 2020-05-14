package edu.ub.sportshub.models

import android.content.Context
import android.content.res.Resources
import com.google.firebase.Timestamp
import edu.ub.sportshub.R
import edu.ub.sportshub.data.enums.NotificationType

class NotificationFollowed(
    id: String,
    uid: String,
    date: Timestamp,
    creatorUid: String,
    checked: Boolean,
    notificationType : NotificationType
) : Notification(
    id,
    uid,
    date,
    creatorUid,
    checked,
    notificationType
), INotification {

    constructor() : this(
        "", "", Timestamp.now(), "", false, NotificationType.ASSIST
    )

    override fun getMessage(context : Context, originUsername : String): String {
        return context.getString(R.string.follow_message, originUsername)
    }

}