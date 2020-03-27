package edu.ub.sportshub.home


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import edu.ub.sportshub.R
import edu.ub.sportshub.event.EventActivity
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.models.Event
import edu.ub.sportshub.models.User

/**
 * A simple [Fragment] subclass.
 */
class Events : Fragment() {

    private var storeDatabaseHelper = StoreDatabaseHelper()
    private var authDatabaseHelper = AuthDatabaseHelper()
    private var eventsToShow = mutableListOf<Event>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRefreshListener()
        showFollowingUsersEvents()
    }

    private fun setupRefreshListener() {
        var refreshingLayout = view?.findViewById<SwipeRefreshLayout>(R.id.eventsSwipeRefresh)
        refreshingLayout?.setOnRefreshListener {
            eventsToShow.clear()
            showFollowingUsersEvents()
        }
    }

    private fun showFollowingUsersEvents() {
        val refreshingLayout = view?.findViewById<SwipeRefreshLayout>(R.id.eventsSwipeRefresh)
        var loggedUserUid = authDatabaseHelper.getCurrentUser()?.uid.toString()
        storeDatabaseHelper.retrieveUser(loggedUserUid).addOnSuccessListener { loggedUser ->
            val currentUser = loggedUser.toObject(User::class.java)
            val followingUsers = currentUser?.getFollowingUsers()

            if (followingUsers != null) {
                for (followedUserUid in followingUsers) {
                    showFollowingUsersEventsSecondStep(followedUserUid)
                }
            }
        }
    }

    private fun showFollowingUsersEventsSecondStep(followedUserUid: String) {
        val refreshingLayout = view?.findViewById<SwipeRefreshLayout>(R.id.eventsSwipeRefresh)

        storeDatabaseHelper.retrieveUser(followedUserUid).addOnSuccessListener {
            val followedUser = it.toObject(User::class.java)
            showFollowingUsersEventsThirdStep(followedUser)
        }
    }

    private fun showFollowingUsersEventsThirdStep(followedUser: User?) {
        val refreshingLayout = view?.findViewById<SwipeRefreshLayout>(R.id.eventsSwipeRefresh)
        val eventsOwnedIds = followedUser?.getEventsOwned()
        if (eventsOwnedIds != null) {
            for (eventId in eventsOwnedIds) {
                storeDatabaseHelper.retrieveEvent(eventId).addOnSuccessListener {
                    val event = it.toObject(Event::class.java)
                    if (event != null) {
                        eventsToShow.add(event)
                        updateShowingEvents()
                    }
                }.addOnFailureListener {
                    //refreshingLayout?.isRefreshing = false
                }
            }
        }
    }

    private fun updateShowingEvents() {
        val refreshingLayout = view?.findViewById<SwipeRefreshLayout>(R.id.eventsSwipeRefresh)

        eventsToShow.sortBy {
            it.getCreationDate().seconds
        }

        val eventContainer = view?.findViewById<LinearLayout>(R.id.eventsContainer)
        eventContainer?.removeAllViews()


        for (event in eventsToShow) {
            val eventView = LayoutInflater.from(context).inflate(R.layout.event_view, null);
            val eventViewTitleView = eventView.findViewById<TextView>(R.id.title)
            val eventViewDescriptionView = eventView.findViewById<TextView>(R.id.description)

            eventViewTitleView.text = event.getTitle()
            eventViewDescriptionView.text = event.getDescription()

            eventView.setOnClickListener {
                val intent = Intent(context, EventActivity::class.java)
                intent.putExtra("eventId", event.getId())
                startActivity(intent)
            }

            eventContainer?.addView(eventView)
        }
        refreshingLayout?.isRefreshing = false
    }
}
