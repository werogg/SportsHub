package edu.ub.sportshub.helpers

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import edu.ub.sportshub.models.User

class AuthDatabaseHelper : DatabaseHelper() {

    private val mFirebaseAuth = getFirebaseAuth()
    private val mStoreDatabaseHelper = StoreDatabaseHelper()

    /**
     * Check if the user is logged
     * @return true if logged
     */
    fun isUserLogged() : Boolean {
        return mFirebaseAuth.currentUser != null
    }

    /**
     * Creates an account on SportsHub database
     * @return true if the account was successfully created
     */
    fun createAccount(email: String,
                      password: String,
                      username : String,
                      fullname : String,
                      birthDate : String,
                      profilePicture : String
    ) : Task<AuthResult> {
        val biography = ""
        return mFirebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val newUser = User(
                        username,
                        fullname,
                        biography,
                        birthDate,
                        email,
                        profilePicture,
                        mFirebaseAuth.currentUser!!.uid,
                        false
                    )
                    mStoreDatabaseHelper.storeUser(newUser)
                }
            }
    }

    fun loginAccount(email : String, password: String) : Task<AuthResult> {
        return mFirebaseAuth.signInWithEmailAndPassword(email, password)
    }
}