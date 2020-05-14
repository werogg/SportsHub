package edu.ub.sportshub.data.auth

import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import edu.ub.sportshub.data.enums.LoginResult
import edu.ub.sportshub.data.events.auth.LoginPerformedEvent
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.models.User

class FireauthLoginHandler : LoginHandler() {

    private val authDatabaseHelper = AuthDatabaseHelper()
    private val storeDatabaseHelper = StoreDatabaseHelper()

    override fun performLogin(username: String, password: String) {

        if (username == "" || password == "") {
            executeListeners(LoginPerformedEvent(LoginResult.MISSING_FIELDS))
            return
        }

        val usersCollectionRef = storeDatabaseHelper.getUsersCollection()

        usersCollectionRef.whereEqualTo("username", username).get()
            .addOnSuccessListener {
                if (it.documents.size == 1) {
                    val mail = it.documents[0].toObject(User::class.java)?.getEmail()
                    continueLogin(mail, password)
                } else {
                    executeListeners(LoginPerformedEvent(LoginResult.WRONG_USERNAME))
                }
            }
    }

    private fun continueLogin(mail: String?, password: String) {
        if (!mail.isNullOrEmpty()) {
            authDatabaseHelper.loginAccount(mail, password)
                .addOnSuccessListener {
                    onLoginSuccess()
                }
                .addOnFailureListener {
                    if (it is FirebaseTooManyRequestsException) {
                        executeListeners(LoginPerformedEvent(LoginResult.TOO_MUCH_REQUESTS))
                    }
                    else if (it is FirebaseAuthException) {
                        val errorCode = it.errorCode
                        if (errorCode == "ERROR_WRONG_PASSWORD")  {
                            executeListeners(LoginPerformedEvent(LoginResult.WRONG_PASSWORD))
                        }
                    } else {
                        executeListeners(LoginPerformedEvent(LoginResult.UNKNOWN_EXCEPTION))
                    }
                }
        }

    }

    private fun onLoginSuccess() {
        val uid = authDatabaseHelper.getCurrentUser()!!.uid

        storeDatabaseHelper.retrieveUser(uid)
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                if (user != null) {
                    if (user.isBanned()) {
                        authDatabaseHelper.signOut()
                        executeListeners(LoginPerformedEvent(LoginResult.USER_BANNED))
                    } else if (authDatabaseHelper.isUserLogged()) {
                        if (!(authDatabaseHelper.getCurrentUser()!!.isEmailVerified)) {
                            authDatabaseHelper.signOut()
                            executeListeners(LoginPerformedEvent(LoginResult.USER_NOT_VERIFIED))
                        } else {
                            executeListeners(LoginPerformedEvent(LoginResult.SUCCESS))
                        }
                    }
                }
            }
    }

}