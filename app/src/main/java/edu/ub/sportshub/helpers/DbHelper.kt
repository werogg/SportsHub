package main.java.edu.ub.sportshub.helpers

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.ub.sportshub.models.User

class DbHelper {

    private var mFirebaseAuth = FirebaseAuth.getInstance()
    private var mFirebaseStore = FirebaseFirestore.getInstance()

    fun getLoggedUser() : User? {
        if (mFirebaseAuth.currentUser != null) {
            var uid = mFirebaseAuth.uid
            var userCollection = mFirebaseStore.collection(uid.toString())
            var mail = userCollection.document("email") as String
            var username = userCollection.document("username") as String
            var fullname = userCollection.document("fullname") as String
            var profilePicture = userCollection.document("profilePicture") as String

            User(
                mail,
                username,
                fullname,
                profilePicture,
                "",
                "",
                true
            )
        }
        return null
    }


    fun createAccount(email: String,
                      password: String,
                      username : String,
                      fullname : String,
                      profilePicture : String
                      ) : Boolean {
        var success = false

        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {



                mFirebaseAuth.currentUser.uid



                success = it.isSuccessful
            }

        return success
    }






}