package edu.ub.sportshub.home


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import edu.ub.sportshub.R
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.models.User
import edu.ub.sportshub.profile.ProfileOtherActivity

/**
 * A simple [Fragment] subclass.
 */
class UsersFragment : Fragment() {

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

        val userContainer = view.findViewById<LinearLayout>(R.id.userContainer)
        userContainer?.removeAllViews()

        val searchView = view.findViewById<SearchView>(R.id.searchView)

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                if(newText.trim().isNotEmpty()){
                    searchUsers(newText);
                }
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                if(query.trim().isNotEmpty()){
                    searchUsers(query);
                }
                return false
            }

        })
    }

    private fun searchUsers(query: String) {

        var n = false
        var userContainer = view?.findViewById<LinearLayout>(R.id.userContainer)
        val curr = authDatabaseHelper.getCurrentUser()!!.uid

        storeDatabaseHelper.getUsersCollection().whereGreaterThanOrEqualTo("username", query).get()
            .addOnSuccessListener { users ->
                for (user in  users) {
                    val userObj = user.toObject(User::class.java)
                    val uid = userObj.getUid()
                    if(uid!=curr){
                        val dpSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130f, context?.resources?.displayMetrics).toInt()
                        val userView = LayoutInflater.from(context).inflate(R.layout.user_view, null);
                        val userViewTitleView = userView.findViewById<TextView>(R.id.username)
                        val userViewFollowView = userView.findViewById<TextView>(R.id.follow)
                        val userViewBannerImage = userView.findViewById<ImageView>(R.id.profilePicture)

                        userViewTitleView.text = userObj.getUsername()

                        if (userObj.getFollowersUsers().contains(curr)) {
                            userViewFollowView.text = getString(R.string.following)
                            userViewFollowView.setTextColor(Color.GREEN)
                        }
                        else {
                            userViewFollowView.text = getString(R.string.not_following)
                            userViewFollowView.setTextColor(Color.RED)
                        }

                        Picasso.with(context)
                            .load(user.data["profilePicture"].toString())
                            .resize(dpSize, dpSize)
                            .into(userViewBannerImage)

                        n = true

                        userView.setOnClickListener {
                            userClicked(uid)
                        }

                        userContainer?.addView(userView)
                    }
                }
            }

        if(!n){
            userContainer = view?.findViewById<LinearLayout>(R.id.userContainer)
            userContainer?.removeAllViewsInLayout()
        }

    }

    private fun userClicked(userId: String) {
        val intent = Intent(context, ProfileOtherActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }


}
