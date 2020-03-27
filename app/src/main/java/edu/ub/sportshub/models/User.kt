package edu.ub.sportshub.models

import com.google.firebase.Timestamp

class User(
    private var username: String,
    private var fullName: String,
    private var biography: String,
    private var signupDate: Timestamp,
    private var eventsLiked: MutableList<String>,
    private var eventsAssist: MutableList<String>,
    private var followingUsers: MutableList<String>,
    private var followersUsers: MutableList<String>,
    private var email: String,
    private var profilePicture: String,
    private var uid: String,
    private var banned: Boolean
) {

    constructor() : this(
        "", "", "", Timestamp.now(), mutableListOf(), mutableListOf(),
        mutableListOf(), mutableListOf(), "", "", "", false
    )

    fun getUsername() : String {
        return username
    }

    fun getFullName() : String {
        return fullName
    }

    fun getBiography() : String {
        return biography
    }

    fun getSignupDate() : Timestamp {
        return signupDate
    }

    fun getEmail() : String {
        return email
    }

    fun getProfilePicture() : String {
        return profilePicture
    }

    fun getUid() : String {
        return uid
    }

    fun isBanned() : Boolean {
        return banned
    }

    fun getEventsLiked() : MutableList<String> {
        return eventsLiked
    }

    fun getEventsAssist() : MutableList<String> {
        return eventsAssist
    }

    fun getFollowingUsers() : MutableList<String> {
        return followingUsers
    }

    fun getFollowersUsers() : MutableList<String> {
        return followersUsers
    }
}