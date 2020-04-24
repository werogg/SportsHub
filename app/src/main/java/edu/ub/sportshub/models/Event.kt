package edu.ub.sportshub.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

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
class Event(
    creatorUid: String,
    title: String,
    description: String,
    eventImage: String,
    startEventDate: Timestamp,
    creationDate: Timestamp,
    deleted: Boolean,
    usersLiked: MutableList<String>,
    usersAssists: MutableList<String>,
    position: GeoPoint // Use android.Geocoder object
            ) {

    private var creatorUid : String = creatorUid
        get() = field
        set(value) {
            field = value
        }

    private var title : String = title
        get() = field
        set(value) {
            field = value
        }
    private var description : String = description
        get() = field
        set(value) {
            field = value
        }

    private var eventImage : String = eventImage
        get() = field
        set(value) {
            field = value
        }

    private var startEventDate : Timestamp = startEventDate
        get() = field
        set(value) {
            field = value
        }

    private var creationDate : Timestamp = creationDate
        get() = field
        set(value) {
            field = value
        }

    private var deleted : Boolean = deleted
        get() = field
        set(value) {
            field = value
        }

    /**
     * Check if this [Event] is completed.
     * @return true if completed
     */
    fun isCompleted() : Boolean {
        return startEventDate.toDate().after(Timestamp.now().toDate())
    }

    private var usersLiked : MutableList<String> = usersLiked
        get() = field
        set(value) {
            field = value
        }

    private var usersAssists : MutableList<String> = usersAssists
        get() = field
        set(value) {
            field = value
        }

    private var position : GeoPoint = position
        get() = field
        set(value) {
            field = value
        }

    fun getLikes() : Int {
        return usersLiked.size
    }

    fun getAssists() : Int {
        return usersAssists.size
    }
}