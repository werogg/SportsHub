package edu.ub.sportshub.helpers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import edu.ub.sportshub.MainActivity
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
                    mFirebaseAuth.currentUser!!.sendEmailVerification()
                    signOut()
                }
            }
    }

    fun loginAccount(email : String, password: String) : Task<AuthResult> {
        return mFirebaseAuth.signInWithEmailAndPassword(email, password)
    }

    fun signOut(appCompatActivity: AppCompatActivity)  {
        mFirebaseAuth.signOut()
        val intent = Intent(appCompatActivity, MainActivity::class.java)
        appCompatActivity.startActivity(intent)
    }

    fun signOut()  {
        mFirebaseAuth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return mFirebaseAuth.currentUser
    }

    fun sendPasswordResetEmail(email : String) {
        mFirebaseAuth.sendPasswordResetEmail(email)
    }
}