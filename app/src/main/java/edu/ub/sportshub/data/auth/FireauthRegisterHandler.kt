package edu.ub.sportshub.data.auth

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import edu.ub.sportshub.data.enums.RegisterResult
import edu.ub.sportshub.data.events.auth.RegisterPerformedEvent
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.models.User

class FireauthRegisterHandler : RegisterHandler() {

    private val authDatabaseHelper = AuthDatabaseHelper()
    private val storeDatabaseHelper = StoreDatabaseHelper()

    override fun performRegister(username: String, fullname: String, email: String, pass: String, defaultImage: String) {

        // Search if given username is available
        storeDatabaseHelper.getUsersCollection().whereEqualTo("username", username).get()
            .addOnCompleteListener { it ->
                if (it.isSuccessful) {
                    // If no users registered with the given username continue with signup flow
                    if (it.result?.isEmpty!!) {
                        continueRegister(username, fullname, email, pass, defaultImage)
                    } else {
                        executeListeners(RegisterPerformedEvent(RegisterResult.USERNAME_ALREADY_EXISTS))
                    }
                }
            }

    }

    private fun continueRegister(username: String, fullname: String, email: String, pass: String, defaultImage: String) {
        // Try to create the account
        authDatabaseHelper.createAccount(email, pass)
            .addOnCompleteListener {
                // If signup is succes continue with the flow
                if (it.isSuccessful) {
                    onRegisterSuccess(username, fullname, email, defaultImage)
                }
            }
            // If failed check the reason
            .addOnFailureListener {
                if (it is FirebaseAuthWeakPasswordException) {
                    executeListeners(RegisterPerformedEvent(RegisterResult.WEAK_PASSWORD))
                } else if (it is FirebaseAuthUserCollisionException) {
                    executeListeners(RegisterPerformedEvent(RegisterResult.MAIL_ALREADY_EXISTS))
                }
            }
    }

    private fun onRegisterSuccess(username: String, fullname: String, email: String, defaultImage: String) {
        // Instance the user object and store it
        val newUser = User(
            username,
            fullname,
            "",
            Timestamp.now(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            email,
            defaultImage,
            authDatabaseHelper.getCurrentUser()!!.uid,
            false,
            mutableListOf()
        )

        storeDatabaseHelper.storeUser(newUser)
        if (authDatabaseHelper.getCurrentUser() != null)
            authDatabaseHelper.getCurrentUser()!!.sendEmailVerification()
        executeListeners(RegisterPerformedEvent(RegisterResult.SUCCESS))
    }
}