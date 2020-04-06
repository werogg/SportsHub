package edu.ub.sportshub.helpers

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import edu.ub.sportshub.models.User
import java.lang.ref.Reference

class StoreDatabaseHelper : DatabaseHelper() {

    private var mFirebaseFirestore = getFirebaseStore()
    private var usersCollectionRef = mFirebaseFirestore.collection("users")

    fun storeUser(user : User) {
        usersCollectionRef.document(user.getUid()).set(user)
    }

    fun retrieveUser(uid : String) : Task<DocumentSnapshot> {
        return usersCollectionRef.document(uid).get()
    }

    fun getUsersCollection(): CollectionReference {
        return mFirebaseFirestore.collection("users")
    }

}