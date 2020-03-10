package edu.ub.sportshub.helpers

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.ub.sportshub.models.User

class DbHelper {

    private var mFirebaseAuth = FirebaseAuth.getInstance()
    private var mFirebaseStore = FirebaseFirestore.getInstance()

    /**
     * Returns an [User] if is logged
     * @return [User]
     */
    fun getLoggedUser() : User? {
        if (mFirebaseAuth.currentUser != null) {
            val uid = mFirebaseAuth.uid.toString()
            val userCollection = mFirebaseStore.collection(uid.toString())
            val username = userCollection.document("userName") as String
            val fullname = userCollection.document("fullName") as String
            val bio = userCollection.document("biography") as String
            val birthDate = userCollection.document("birthDate") as String
            val email = userCollection.document("email") as String
            val profilePicture = userCollection.document("profilePicture") as String

            return User(
                username,
                fullname,
                bio,
                birthDate,
                email,
                profilePicture,
                uid,
                false
            )
        }
        return null
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
                      ) : Boolean {
        var success = false

        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {

                val userData = hashMapOf(
                    "userName" to username,
                    "fullName" to fullname,
                    "birthDate" to birthDate,
                    "biography" to "",
                    "profilePicture" to profilePicture,
                    "banned" to false
                )

                val uidString = mFirebaseAuth.currentUser?.uid.toString()

                mFirebaseStore.collection("users").document(uidString)
                    .set(userData)
                    .addOnSuccessListener {
                        success = true
                    }
                success = success && it.isSuccessful
            }

        return success
    }






}