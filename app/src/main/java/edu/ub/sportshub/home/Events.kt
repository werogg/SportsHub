package edu.ub.sportshub.home


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth

import edu.ub.sportshub.R
import edu.ub.sportshub.event.EventActivity
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.models.User

/**
 * A simple [Fragment] subclass.
 */
class Events : Fragment() {

    private var storeDatabaseHelper = StoreDatabaseHelper()
    private var authDatabaseHelper = AuthDatabaseHelper()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showFollowingUsersEvents()
    }

    private fun retrieveUser() {

    }

    private fun showFollowingUsersEvents() {
        var loggedUserUid = authDatabaseHelper.getCurrentUser()?.uid.toString()
        storeDatabaseHelper.retrieveUser(loggedUserUid).addOnSuccessListener {
            var user = it.toObject(User::class.java)

        }

    }

    private fun nikeCardClicked() {
        val intent = Intent(activity, EventActivity::class.java)
        startActivity(intent)
    }


}
