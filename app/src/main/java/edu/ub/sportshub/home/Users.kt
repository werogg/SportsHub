package edu.ub.sportshub.home


import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.firebase.firestore.QueryDocumentSnapshot

import com.squareup.picasso.Picasso
import edu.ub.sportshub.R
import edu.ub.sportshub.helpers.AuthDatabaseHelper
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
        var n = query.length
        storeDatabaseHelper.getUsersCollection().whereGreaterThanOrEqualTo("username", query).get()
            .addOnSuccessListener { users ->
                for (user in users) {
                    addUser(user)
                }
            }

    }

    private fun addUser(user: QueryDocumentSnapshot){

        val userContainer = view?.findViewById<LinearLayout>(R.id.userContainer)
        userContainer?.removeAllViews()

        val dpSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130f, context?.resources?.displayMetrics).toInt()
        val userView = LayoutInflater.from(context).inflate(R.layout.user_view, null);
        val userViewTitleView = userView.findViewById<TextView>(R.id.username)
        val userViewFollowView = userView.findViewById<TextView>(R.id.follow)
        val userViewBannerImage = userView.findViewById<CardView>(R.id.userImage)

        /*
        Picasso.with(context)
            .load(user.getData().get("profilePicture").toString())
            .resize(dpSize, dpSize)
            .into(userViewBannerImage)

         */


        userView.setOnClickListener {
            val intent = Intent(activity, ProfileActivity::class.java)
            startActivity(intent)
        }

        userViewTitleView.text = user.getData().get("username").toString()

        // Deal with following. Get current user and check

        userContainer?.addView(userView)

    }



}


