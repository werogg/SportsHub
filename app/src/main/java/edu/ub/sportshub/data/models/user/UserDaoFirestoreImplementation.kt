package edu.ub.sportshub.data.models.user

import android.util.Log
import edu.ub.sportshub.data.events.database.UserLoadedEvent
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.models.User

class UserDaoFirestoreImplementation : UserDao() {
    val TAG = UserDaoFirestoreImplementation::class.java.name
    var user : User? = null
    var userCollection : List<User>? = null

    override fun fetchUser(uid: String)  {
        val storeDatabaseHelper = StoreDatabaseHelper()
        storeDatabaseHelper.retrieveUser(uid)
            .addOnSuccessListener {
            val user = it.toObject(User::class.java)
            executeListeners(UserLoadedEvent(user!!))
        }
            .addOnFailureListener {
                Log.e(TAG, it.message)
            }
    }
}