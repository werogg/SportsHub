package edu.ub.sportshub.data.models.event

import edu.ub.sportshub.models.Event

interface IEventDao {
    fun fetchUserEvents(uid: String)
    fun fetchEvent(eid : String)
    fun fetchFollowingUsersEvents(uid: String)
    fun giveLike(uid: String, eid: String)
    fun giveAssist(uid: String, eid: String)
}