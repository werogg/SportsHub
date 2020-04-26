package edu.ub.sportshub.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.IgnoreExtraProperties

/**
 *  An event post.
 *  This class has Event posting model.
 *
 *  @property creatorUid the creator uid of this event.
 *  @property title the title of this event.
 *  @property description the description of this event.
 *  @property eventImage an array of all the event images urls.
 *  @property startEventDate the date of celebration of this event.
 *  @property creationDate the date of the creation of this event.
 *  @property deleted indicates if the event is visible (non-public visible).
 *  @property usersLiked indicates the users who liked this event.
 *  @property usersAssists indicates the users who will assist this event.
 *  @constructor Creates a full set up event.
 */
@IgnoreExtraProperties
class Event(
    private var id: String,
    private var creatorUid: String,
    private var title: String,
    private var description: String,
    private var eventImage: String,
    private var startEventDate: Timestamp,
    private var creationDate: Timestamp,
    private var deleted: Boolean,
    private var usersLiked: MutableList<String>,
    private var usersAssists: MutableList<String>,
    private var position: GeoPoint // Use android.Geocoder object
            ) {

    constructor() : this(
        "", "", "", "", "", Timestamp.now(),
        Timestamp.now(), false, mutableListOf(), mutableListOf(),
        GeoPoint(0.0, 0.0)
    )

    fun getEventImage() : String {
        return eventImage
    }

    /**
     * Check if this [Event] is completed.
     * @return true if completed
     */
    fun isCompleted() : Boolean {
        return startEventDate.toDate().after(Timestamp.now().toDate())
    }

    fun getCreationDate() : Timestamp {
        return creationDate
    }

    fun getTitle() : String {
        return title
    }

    fun getDescription() : String {
        return description
    }

    fun getId() : String {
        return id
    }

    fun getLikes() : Int {
        return usersLiked.size
    }

    fun getAssists() : Int {
        return usersAssists.size
    }


    fun getPosition() : GeoPoint {
        return position
    }

    fun getStartEventDate() : Timestamp {
        return startEventDate
    }

    fun getCreatorUid() : String {
        return creatorUid
    }

    fun getUsersLiked() : MutableList<String> {
        return usersLiked
    }

    fun getUsersAssists() : MutableList<String> {
        return usersAssists
    }

    fun isDeleted() : Boolean {
        return deleted
    }
}