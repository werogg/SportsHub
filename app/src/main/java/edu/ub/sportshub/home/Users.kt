package edu.ub.sportshub.home


import android.content.Intent
import android.os.Bundle
import android.util.Log
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

        val userContainer = view?.findViewById<LinearLayout>(R.id.userContainer)
        userContainer?.removeAllViews()

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

        var n = false
        var userContainer = view?.findViewById<LinearLayout>(R.id.userContainer)
        var curr = authDatabaseHelper.getCurrentUser()!!.uid

        storeDatabaseHelper.getUsersCollection().whereGreaterThanOrEqualTo("username", query).get()
            .addOnSuccessListener { users ->
                for (user in  users) {
                    var uid = user.getData().get("uid").toString()
                    if(uid!=curr){
                        val dpSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130f, context?.resources?.displayMetrics).toInt()
                        val userView = LayoutInflater.from(context).inflate(R.layout.user_view, null);
                        val userViewTitleView = userView.findViewById<TextView>(R.id.username)
                        val userViewFollowView = userView.findViewById<TextView>(R.id.follow)
                        val userViewBannerImage = userView.findViewById<ImageView>(R.id.profilePicture)

                        userViewTitleView.text = user.getData().get("username").toString()


                        Picasso.with(context)
                            .load(user.getData().get("profilePicture").toString())
                            .resize(dpSize, dpSize)
                            .into(userViewBannerImage)

                        n = true
                        userContainer?.addView(userView)
                    }
                }
            }

        if(n==false){
            val userContainer = view?.findViewById<LinearLayout>(R.id.userContainer)
            userContainer?.removeAllViewsInLayout()
        }

    }


}


