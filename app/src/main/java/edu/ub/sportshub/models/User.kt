package edu.ub.sportshub.models

class User(
    username: String,
    fullName: String,
    biography: String,
    birthDate: String,
    email: String,
    profilePicture: String,
    uid: String,
    banned: Boolean
) {
    private var username : String = username
    private var fullName : String = fullName
    private var biography : String = biography
    private var birthDate : String = birthDate
    private var email : String = email
    private var profilePicture : String = profilePicture
    private var uid : String = uid
    private var banned : Boolean = banned

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

    fun getBirthDate() : String {
        return birthDate
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