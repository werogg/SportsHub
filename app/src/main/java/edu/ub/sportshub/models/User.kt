package edu.ub.sportshub.models

class User(
    private var username: String,
    private var fullName: String,
    private var biography: String,
    private var signupDate: String,
    private var email: String,
    private var profilePicture: String,
    private var uid: String,
    private var banned: Boolean
) {

    constructor() : this(
        "", "", "", "", "", "", "", false
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

    fun getSignupDate() : String {
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

}