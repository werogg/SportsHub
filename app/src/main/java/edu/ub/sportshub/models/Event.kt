package edu.ub.sportshub.models

import java.util.*

/**
 *  An event post.
 *  This class has Event posting model.
 *
 *  @property creatorUid the creator uid of this event.
 *  @property title the title of this event.
 *  @property description the description of this event.
 *  @property eventImages an array of all the event images urls.
 *  @property startEventDate the date of celebration of this event.
 *  @property creationDate the date of the creation of this event.
 *  @property deleted indicates if the event is visible (non-public visible).
 *  @constructor Creates a full set up event.
 */
class Event (creatorUid : String,
             title: String,
             description : String,
             eventImages : MutableList<String>,
             startEventDate : Calendar,
             creationDate : Calendar,
             deleted : Boolean
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

    private var eventImages : MutableList<String> = eventImages
        get() = field
        set(value) {
            field = value
        }

    private var startEventDate : Calendar = startEventDate
        get() = field
        set(value) {
            field = value
        }

    private var creationDate : Calendar = creationDate
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
        return startEventDate.after(Calendar.getInstance().time)
    }
}