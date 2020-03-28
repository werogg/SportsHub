package edu.ub.sportshub.helpers

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import edu.ub.sportshub.models.User

class StoreDatabaseHelper : DatabaseHelper() {

    private var mFirebaseFirestore = getFirebaseStore()
    private var usersCollectionRef = mFirebaseFirestore.collection("users")
    private var eventsCollectionRef = mFirebaseFirestore.collection("events")

    fun storeUser(user : User) {
        usersCollectionRef.document(user.getUid()).set(user)
    }

    fun retrieveUser(uid : String) : Task<DocumentSnapshot> {
        return usersCollectionRef.document(uid).get()
    }

    fun retrieveEvent(id: String) : Task<DocumentSnapshot> {
        return eventsCollectionRef.document(id).get()
    }

    fun getUsersCollection(): CollectionReference {
        return mFirebaseFirestore.collection("users")
    }
}