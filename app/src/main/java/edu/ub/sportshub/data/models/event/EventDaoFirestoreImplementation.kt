package edu.ub.sportshub.data.models.event

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import edu.ub.sportshub.data.events.database.EventLoadedEvent
import edu.ub.sportshub.data.events.database.EventsLoadedEvent
import edu.ub.sportshub.data.events.database.FollowingUsersEventsLoadedEvent
import edu.ub.sportshub.data.listeners.DataChangeListener
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.models.Event
import edu.ub.sportshub.models.User
import java.util.*

class EventDaoFirestoreImplementation : EventDao() {

    val TAG = EventDaoFirestoreImplementation::class.java.name
    val mStoreDatabaseHelper = StoreDatabaseHelper()
    lateinit var mListener : DataChangeListener
    lateinit var event : Event
    var eventCollection : List<Event>? = null
    var fetchingFollowingUserseventsEnded = false
    private var MAX_EVENTS_SHOWED = 30

    override fun fetchUserEvents(uid: String) {
        val events = mutableListOf<Event>()
        val storeDatabaseHelper = StoreDatabaseHelper()
        storeDatabaseHelper.retrieveUser(uid).addOnSuccessListener {
            val user = it.toObject(User::class.java)
            for (eid in user!!.getEventsOwned()) {
                storeDatabaseHelper.retrieveEvent(eid).addOnSuccessListener {ev ->
                    val event = ev.toObject(Event::class.java)
                    events.add(event!!)
                    executeListeners(EventsLoadedEvent(events, user))
                }
            }
        }
    }

    override fun fetchEvent(eid: String) {
        val storeDatabaseHelper = StoreDatabaseHelper()
        storeDatabaseHelper.retrieveEvent(eid)
            .addOnSuccessListener {
            val event = it.toObject(Event::class.java)
            executeListeners(EventLoadedEvent(event!!))
        }
            .addOnFailureListener {
                Log.e(TAG, it.message)
            }
    }

    override fun fetchFollowingUsersEvents(uid: String) {
        fetchingFollowingUserseventsEnded = false
        val events = mutableListOf<Pair<Event, User>>()
        val storeDatabaseHelper = StoreDatabaseHelper()
            storeDatabaseHelper.getUsersCollection()
                .whereArrayContains("followersUsers", uid)
                .get()
                .addOnSuccessListener { retUserList ->
                    val userList = mutableListOf<User>()
                    for (result in retUserList) {
                        userList.add(result.toObject(User::class.java))
                    }

                    if (userList.isEmpty()) executeListeners(FollowingUsersEventsLoadedEvent(events))
                    else {
                         for (followingUser in userList) {
                            if (fetchingFollowingUserseventsEnded) break
                             else {
                                storeDatabaseHelper.getEventsCollection().whereEqualTo("creatorUid", followingUser.getUid()).get()
                                    .addOnSuccessListener {retEventList ->
                                        val eventsList = retEventList.toObjects(Event::class.java)

                                        for (event in eventsList) {
                                            events.add(Pair(event, followingUser))
                                            executeListeners(FollowingUsersEventsLoadedEvent(events))

                                            if (events.size == MAX_EVENTS_SHOWED) {
                                                fetchingFollowingUserseventsEnded = true
                                                break
                                            }
                                        }
                                    }
                            }
                        }
                }
        }
    }

    override fun giveLike(uid: String, eid: String) {
        val storeDatabaseHelper = StoreDatabaseHelper()
        storeDatabaseHelper.retrieveEvent(eid)
            .addOnSuccessListener {
                val event = it.toObject(Event::class.java)
                if (event!!.getUsersLiked().contains(uid)) {
                    // Remove the user from event's user liked list
                    mStoreDatabaseHelper.retrieveEventRef(eid)
                        .update(
                            "usersLiked",
                            FieldValue.arrayRemove(uid)
                        )
                    // Remove the event from the user's events liked list
                    mStoreDatabaseHelper.retrieveUserRef(uid)
                        .update("eventsLiked",
                            FieldValue.arrayRemove(eid)
                        )
                } else {
                    mStoreDatabaseHelper.retrieveEventRef(eid)
                        .update(
                            "usersLiked",
                            FieldValue.arrayUnion(uid)
                        )

                    // Add the event from to user's events liked list
                    mStoreDatabaseHelper.retrieveUserRef(uid!!)
                        .update("eventsLiked",
                            FieldValue.arrayUnion(eid)
                        )
                }
            }
    }

    override fun giveAssist(uid: String, eid: String) {
        val storeDatabaseHelper = StoreDatabaseHelper()
        storeDatabaseHelper.retrieveEvent(eid)
            .addOnSuccessListener {
                val event = it.toObject(Event::class.java)
                if (event!!.getUsersAssists().contains(uid)) {
                    // Remove the user from event's user liked list
                    mStoreDatabaseHelper.retrieveEventRef(eid)
                        .update(
                            "usersAssists",
                            FieldValue.arrayRemove(uid)
                        )
                    // Remove the event from the user's events liked list
                    mStoreDatabaseHelper.retrieveUserRef(uid)
                        .update("eventsAssist",
                            FieldValue.arrayRemove(eid)
                        )
                } else {
                    // Add the user to event's user liked list
                    mStoreDatabaseHelper.retrieveEventRef(eid)
                        .update(
                            "usersAssists",
                            FieldValue.arrayUnion(uid)
                        )

                    // Add the event from to user's events liked list
                    mStoreDatabaseHelper.retrieveUserRef(uid)
                        .update("eventsAssist",
                            FieldValue.arrayUnion(eid)
                        )
                }
            }
    }


}