package edu.ub.sportshub.models

import android.content.res.Resources
import com.google.firebase.Timestamp
import edu.ub.sportshub.R

class NotificationAssist(
    id: String,
    uid: String,
    date: Timestamp,
    private val assistUid : String,
    creatorUid: String
) : Notification(
    id,
    uid,
    date,
    creatorUid
), INotification {

    fun getAssistUid() : String {
        return assistUid
    }

    override fun getMessage(originUsername : String): String {
        return Resources.getSystem().getString(R.string.follow_message, originUsername)
    }
}