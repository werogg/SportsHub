package edu.ub.sportshub.data.models.event

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import edu.ub.sportshub.models.Event

interface IEventDao {
    fun fetchUserAssistEvents(uid: String)
    fun fetchUserEvents(uid: String)
    fun fetchEvent(eid : String)
    fun fetchFollowingUsersEvents(uid: String)
    fun giveLike(uid: String, eid: String)
    fun giveAssist(uid: String, eid: String)
    fun editEvent(eid: String, title: String, loc: GeoPoint, description: String, image: String)
    fun createEvent(uid: String, title: String, eventDate: Timestamp, creationDate: Timestamp, loc: GeoPoint, description: String, image: String)
}