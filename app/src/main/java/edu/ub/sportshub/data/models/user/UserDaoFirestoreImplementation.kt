package edu.ub.sportshub.data.models.user

import android.util.Log
import edu.ub.sportshub.data.users.database.UserLoadedUser
import edu.ub.sportshub.data.users.database.UsersFolloweesUser
import edu.ub.sportshub.data.users.database.UsersFollowersUser
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
            executeListeners(UserLoadedUser(user!!))
        }
            .addOnFailureListener {
                Log.e(TAG, it.message)
            }
    }

    override fun fetchFollowees(uid: String) {
        val listausers = mutableListOf<User>()
        val storeDatabaseHelper = StoreDatabaseHelper()
        storeDatabaseHelper.retrieveUser(uid)
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                val lista = user?.getFollowingUsers()
                if (lista != null){
                    for (uid in lista){
                        storeDatabaseHelper.retrieveUser(uid)
                            .addOnSuccessListener { follow ->
                                val followees = follow.toObject(User::class.java)
                                listausers.add(followees!!)
                                executeListeners(UsersFolloweesUser(listausers))
                            }
                    }
                }

            }
    }

    override fun fetchFollowers(uid: String) {
        val listausers = mutableListOf<User>()
        val storeDatabaseHelper = StoreDatabaseHelper()
        storeDatabaseHelper.retrieveUser(uid)
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                val lista = user?.getFollowersUsers()
                if (lista != null){
                    for (uid in lista){
                        storeDatabaseHelper.retrieveUser(uid)
                            .addOnSuccessListener { follow ->
                                val followees = follow.toObject(User::class.java)
                                listausers.add(followees!!)
                                executeListeners(UsersFollowersUser(listausers))
                            }
                    }
                }

            }
    }


}