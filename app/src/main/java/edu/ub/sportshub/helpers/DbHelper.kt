package edu.ub.sportshub.helpers

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.ub.sportshub.exceptions.DatabaseRetrieveDataException
import edu.ub.sportshub.exceptions.RegistrationFailedException
import edu.ub.sportshub.models.User

class DbHelper {

    private var mFirebaseAuth = FirebaseAuth.getInstance()
    private var mFirebaseStore = FirebaseFirestore.getInstance()

    /**
     * Check if the user is logged
     * @return true if logged
     */
    fun isUserLogged() : Boolean {
        return mFirebaseAuth.currentUser != null
    }

    /**
     * Returns an [User] if is logged
     * @return [User]
     * @exception [DatabaseRetrieveDataException] if data couldn't be retrieved
     */
    fun getLoggedUser() : User? {
        if (isUserLogged()) {
            // Retrieve user uid
            val uid = mFirebaseAuth.uid.toString()

            // Get user data by uid
            val userCollection = mFirebaseStore.collection(uid)

            // Retrieve data documents from Firebase DataStore
            val usernameGetter = userCollection.document("userName").get()
            val fullnameGetter = userCollection.document("fullName").get()
            val bioGetter = userCollection.document("biography").get()
            val birthDateGetter = userCollection.document("birthDate").get()
            val emailGetter = userCollection.document("email").get()
            val profilePictureGetter = userCollection.document("profilePicture").get()

            // Init variables to be set latter by data stored in firebase
            var username = ""
            var fullname = ""
            var bio = ""
            var birthDate = ""
            var email = ""
            var profilePicture = ""

            // If retrieve data goes successful, set the variable to the correct data
            usernameGetter.addOnSuccessListener {
                username = it.data.toString()
            }
                .addOnFailureListener {
                    throw DatabaseRetrieveDataException(it.message)
                }

            fullnameGetter.addOnSuccessListener {
                fullname = it.data.toString()
            }
                .addOnFailureListener {
                    throw DatabaseRetrieveDataException(it.message)
                }

            bioGetter.addOnSuccessListener {
                bio = it.data.toString()
            }
                .addOnFailureListener {
                    throw DatabaseRetrieveDataException(it.message)
                }

            birthDateGetter.addOnSuccessListener {
                birthDate = it.data.toString()
            }
                .addOnFailureListener {
                    throw DatabaseRetrieveDataException(it.message)
                }

            emailGetter.addOnSuccessListener {
                email = it.data.toString()
            }
                .addOnFailureListener {
                    throw DatabaseRetrieveDataException(it.message)
                }

            profilePictureGetter.addOnSuccessListener {
                profilePicture = it.data.toString()
            }
                .addOnFailureListener {
                    throw DatabaseRetrieveDataException(it.message)
                }

            // Return the [User] object
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
     * @exception []
     */
    fun createAccount(email: String,
                      password: String,
                      username : String,
                      fullname : String,
                      birthDate : String,
                      profilePicture : String
                      ) {

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
                    .addOnFailureListener { exception ->
                        throw RegistrationFailedException(exception.message)
                    }
            }
    }






}