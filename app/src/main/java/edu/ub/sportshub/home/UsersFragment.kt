package edu.ub.sportshub.home


import android.annotation.SuppressLint
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
import edu.ub.sportshub.data.data.DataAccessObjectFactory
import edu.ub.sportshub.data.listeners.UserDataChangeListener
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.profile.ProfileOtherActivity
import edu.ub.sportshub.data.models.user.UserDao
import edu.ub.sportshub.data.users.database.DataUser
import edu.ub.sportshub.data.users.database.UsersFolloweesUser
import edu.ub.sportshub.models.User

/**
 * A simple [Fragment] subclass.
 */
class UsersFragment : Fragment(), UserDataChangeListener {

    private val authDatabaseHelper = AuthDatabaseHelper()
    private val storeDatabaseHelper = StoreDatabaseHelper()
    private var followingUsers = mutableListOf<User>()
    private lateinit var userDao : UserDao

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        userDao = DataAccessObjectFactory.getUserDao()
        userDao.registerListener(this)
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userContainer = view.findViewById<LinearLayout>(R.id.userContainer)
        userContainer?.removeAllViews()

        val searchView = view.findViewById<SearchView>(R.id.searchView)

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String): Boolean {
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


    @SuppressLint("SetTextI18n")
    private fun searchUsers(query: String) {

        var n = false
        var userContainer = view?.findViewById<LinearLayout>(R.id.userContainer)
        val curr = authDatabaseHelper.getCurrentUser()!!.uid
        userDao.fetchFollowees(curr)

        storeDatabaseHelper.getUsersCollection().whereGreaterThanOrEqualTo("username", query).get()
            .addOnSuccessListener { users ->
                for (user in  users) {

                    val uid = user.data["uid"].toString()
                    val followee = followingUsers.filter { it.getUid() == uid }

                    if(uid!=curr){
                        val dpSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130f, context?.resources?.displayMetrics).toInt()
                        val userView = LayoutInflater.from(context).inflate(R.layout.user_view, null);
                        val userViewTitleView = userView.findViewById<TextView>(R.id.username)
                        val userViewFollowView = userView.findViewById<TextView>(R.id.follow)
                        val userViewBannerImage = userView.findViewById<ImageView>(R.id.profilePicture)

                        userViewTitleView.text = user.getData().get("username").toString()

                        if(!followee.isEmpty()){
                            userViewFollowView.text = "@string/not_following_status"
                            userViewFollowView.setTextColor(Color.parseColor("#fc030b"))
                        }
                        else{
                            userViewFollowView.text = "@string/following_status"
                            userViewFollowView.setTextColor(Color.parseColor("#03fc31"))
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

    override fun onDataLoaded(user: DataUser){
        if(user is UsersFolloweesUser){
            followingUsers = user.followersUsers;
        }
    }


    private fun userClicked(userId: String) {

        val intent = Intent(context, ProfileOtherActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }


}
