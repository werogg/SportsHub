package edu.ub.sportshub.profile.follow

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.squareup.picasso.Picasso
import edu.ub.sportshub.R
import edu.ub.sportshub.data.data.DataAccessObjectFactory
import edu.ub.sportshub.data.events.database.DataEvent
import edu.ub.sportshub.data.events.database.UsersFolloweesEvent
import edu.ub.sportshub.data.listeners.DataChangeListener
import edu.ub.sportshub.data.models.user.UserDao
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.models.User
import edu.ub.sportshub.profile.ProfileOtherActivity

class MyFollowees(id: String) : Fragment(), DataChangeListener {
    private var authDatabaseHelper = AuthDatabaseHelper()
    private lateinit var userDao : UserDao
    private var usersToShow = mutableListOf<User>()
    private var uid = id

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userDao = DataAccessObjectFactory.getUserDao()
        userDao.registerListener(this)
        return inflater.inflate(R.layout.fragment_users_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val eventContainer = view.findViewById<LinearLayout>(R.id.eventsContainerProfileFollow)
        val refreshingLayout = view.findViewById<SwipeRefreshLayout>(R.id.eventsSwipeRefresh_follow)
        setupRefreshListener()
        usersToShow.clear()
        eventContainer?.removeAllViews()
        refreshingLayout?.isRefreshing = true
        Thread(Runnable {
            showFollowingUsers()
        }).start()
    }

    private fun setupRefreshListener() {
        val refreshingLayout = view?.findViewById<SwipeRefreshLayout>(R.id.eventsSwipeRefresh_follow)
        val eventContainer = view?.findViewById<LinearLayout>(R.id.eventsContainerProfileFollow)
        val title = view?.findViewById<TextView>(R.id.txt_header_follow)
        refreshingLayout?.setOnRefreshListener {
            usersToShow.clear()
            title?.text = ""
            eventContainer?.removeAllViews()
            showFollowingUsers()
        }
    }

    private fun showFollowingUsers() {
        val title = view?.findViewById<TextView>(R.id.txt_header_follow)
        title?.text= resources.getText(R.string.followees2)
        val refreshingLayout = view?.findViewById<SwipeRefreshLayout>(R.id.eventsSwipeRefresh_follow)
        refreshingLayout?.isRefreshing = true
        Thread {
            kotlin.run {
                userDao.fetchFollowees(uid)
            }
        }.start()
    }

    private fun updateShowingUsers(){
        val refreshingLayout2 = view?.findViewById<SwipeRefreshLayout>(R.id.eventsSwipeRefresh_follow)
        val eventContainer = view?.findViewById<LinearLayout>(R.id.eventsContainerProfileFollow)
        eventContainer?.removeAllViews()
        val title = view?.findViewById<TextView>(R.id.txt_header)
        title?.text= resources.getText(R.string.followees2)
        for (user in usersToShow) {
            val dpSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130f, context?.resources?.displayMetrics).toInt()
            val userView = LayoutInflater.from(context).inflate(R.layout.user_view, null);
            val userTitle = userView.findViewById<TextView>(R.id.username)
            val imageProfileFollow = userView.findViewById<ImageView>(R.id.profilePicture)
            val following = userView.findViewById<TextView>(R.id.follow)
            val layout = userView.findViewById<LinearLayout>(R.id.layout_to_user)
            if (user.getProfilePicture().equals("")){
                Picasso.with(context)
                    .load(R.mipmap.ic_usuari_foreground)
                    .resize(dpSize, dpSize)
                    .into(imageProfileFollow)
            } else {
                Picasso.with(context)
                    .load(user.getProfilePicture())
                    .resize(dpSize, dpSize)
                    .into(imageProfileFollow)
            }
            userTitle.text = user.getUsername()
            following.text = resources.getText(R.string.following)

            layout.setOnClickListener(){
                val intent = Intent(context, ProfileOtherActivity::class.java)
                intent.putExtra("userId", user.getUid())
                startActivity(intent)
            }
            eventContainer?.addView(userView)

        }
        refreshingLayout2?.isRefreshing = false
    }

    override fun onDataLoaded(event: DataEvent) {
        if (event is UsersFolloweesEvent){
            usersToShow = event.eventList
            updateShowingUsers()
            val refreshingLayout = view?.findViewById<SwipeRefreshLayout>(R.id.eventsSwipeRefresh_follow)
            refreshingLayout?.isRefreshing = false
        }
    }


}
