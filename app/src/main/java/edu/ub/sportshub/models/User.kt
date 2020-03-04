package edu.ub.sportshub.models

class User(
    username: String,
    fullname: String,
    biography: String,
    mail: String,
    profilePicture: String,
    uid: String,
    banned: Boolean
) {

    private var username : String = username
        get() = field
        set(value) {
            field = value
        }
    private var fullname : String = fullname
        get() = field
        set(value) {
            field = value
        }
    private var biography : String = biography
        get() = field
        set(value) {
            field = value
        }
    private var mail : String = mail
        get() = field
        set(value) {
            field = value
        }
    private var profilePicture : String = profilePicture
        get() = field
        set(value) {
            field = value
        }
    private var uid : String = uid
        get() = field
        set(value) {
            field = value
        }
    private var banned : Boolean = banned
        get() = field
        set(value) {
            field = value
        }

}