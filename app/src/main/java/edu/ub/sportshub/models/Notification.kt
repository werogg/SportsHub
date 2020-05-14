package edu.ub.sportshub.models

import com.google.firebase.Timestamp
import edu.ub.sportshub.data.enums.NotificationType

open class Notification (
    private var id : String,
    private var uid : String,
    private var date : Timestamp,
    private var creatorUid : String,
    private var checked : Boolean,
    private var notificationType : NotificationType
) {

    constructor() : this(
        "", "", Timestamp.now(), "", false, NotificationType.FOLLOWED
    )

    fun getId() : String {
        return id
    }

    fun getUid() : String {
        return uid
    }

    fun getDate() : Timestamp {
        return date
    }

    fun getCreatorUid() : String {
        return creatorUid
    }

    fun isChecked() : Boolean {
        return checked
    }

    fun getNotificationType() : NotificationType {
        return notificationType
    }

}