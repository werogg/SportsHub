package edu.ub.sportshub.models

import com.google.firebase.Timestamp

open class Notification (
    private var id : String,
    private var uid : String,
    private var date : Timestamp,
    private var creatorUid : String
) {

    constructor() : this(
        "", "", Timestamp.now(), ""
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

}