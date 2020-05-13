package edu.ub.sportshub.models

import android.content.res.Resources
import com.google.firebase.Timestamp
import edu.ub.sportshub.R

class NotificationFollowed(
    id: String,
    uid: String,
    date: Timestamp,
    creatorUid: String,
    checked: Boolean
) : Notification(
    id,
    uid,
    date,
    creatorUid,
    checked
), INotification {

    override fun getMessage(originUsername : String): String {
        return Resources.getSystem().getString(R.string.follow_message, originUsername)
    }

}