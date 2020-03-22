package edu.ub.sportshub.helpers

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

open class DatabaseHelper {

    private var mFirebaseAuth = FirebaseAuth.getInstance()
    private var mFirebaseStore = FirebaseFirestore.getInstance()

    protected fun getFirebaseAuth(): FirebaseAuth {
        return mFirebaseAuth
    }

    protected fun getFirebaseStore() : FirebaseFirestore {
        return mFirebaseStore
    }

}