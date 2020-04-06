package edu.ub.sportshub.home


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseUser

import edu.ub.sportshub.R
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.DatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.models.User
import edu.ub.sportshub.profile.ProfileActivity
import edu.ub.sportshub.profile.ProfileOtherActivity

/**
 * A simple [Fragment] subclass.
 */
class Users : Fragment() {

    private val authDatabaseHelper = AuthDatabaseHelper()
    private val storeDatabaseHelper = StoreDatabaseHelper()
    private var usersToShow = mutableListOf<User>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userCard = view.findViewById<CardView>(R.id.userCard)

        userCard.setOnClickListener {
            userCardClicked()
        }

        val userCard2 = view.findViewById<CardView>(R.id.userCard2)

        userCard2.setOnClickListener {
            userCard2Clicked()
        }

        val searchView = view.findViewById<SearchView>(R.id.searchView)

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
            override fun onQueryTextSubmit(query: String): Boolean {
                if(!query.trim().isEmpty()){
                    searchUsers(query);
                }
                return false
            }
        })
    }

    private fun searchUsers(query: String) {

    }

    private fun userCardClicked() {
        val intent = Intent(activity, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun userCard2Clicked() {
        val intent = Intent(activity, ProfileOtherActivity::class.java)
        startActivity(intent)
    }


}
