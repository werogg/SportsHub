package edu.ub.sportshub.home


import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import edu.ub.sportshub.R
import edu.ub.sportshub.event.EventActivity
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.models.Event
import edu.ub.sportshub.models.User
import edu.ub.sportshub.profile.ProfileActivity
import edu.ub.sportshub.profile.ProfileOtherActivity
import edu.ub.sportshub.utils.StringUtils

/**
 * A simple [Fragment] subclass.
 */
class EventsFragment : Fragment() {

    private var storeDatabaseHelper = StoreDatabaseHelper()
    private var authDatabaseHelper = AuthDatabaseHelper()
    private var eventsToShow = mutableListOf<Pair<Event, User>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val eventContainer = view?.findViewById<LinearLayout>(R.id.eventsContainer)
        val refreshingLayout = view?.findViewById<SwipeRefreshLayout>(R.id.eventsSwipeRefresh)

        setupRefreshListener()

        eventsToShow.clear()
        eventContainer?.removeAllViews()

        refreshingLayout?.isRefreshing = true

        Thread(Runnable {
            showFollowingUsersEvents()
        }).start()
    }

    /**
     * Setup listener to refresh events with swipe down
     */
    private fun setupRefreshListener() {
        val refreshingLayout = view?.findViewById<SwipeRefreshLayout>(R.id.eventsSwipeRefresh)
        val eventContainer = view?.findViewById<LinearLayout>(R.id.eventsContainer)
        refreshingLayout?.setOnRefreshListener {
            eventsToShow.clear()
            eventContainer?.removeAllViews()
            showFollowingUsersEvents()
        }
    }

    /**
     * First step to show events on the home page
     */
    private fun showFollowingUsersEvents() {
        val refreshingLayout = view?.findViewById<SwipeRefreshLayout>(R.id.eventsSwipeRefresh)
        refreshingLayout?.isRefreshing = true
        if (maxEventsReached()) return
        val loggedUserUid = authDatabaseHelper.getCurrentUser()?.uid.toString()
        // Retrieve the current logged user
        storeDatabaseHelper.retrieveUser(loggedUserUid).addOnSuccessListener { loggedUser ->
            val currentUser = loggedUser.toObject(User::class.java)

            // Get his followed users and for every user go to second step
            val followingUsers = currentUser?.getFollowingUsers()
            followingUsers?.add(currentUser.getUid())
            if (followingUsers != null) {
                for (followedUserUid in followingUsers) {
                    val lastCall = followingUsers[followingUsers.lastIndex] == followedUserUid
                    showFollowingUsersEventsSecondStep(followedUserUid, lastCall)
                }
            }
        }
    }

    /**
     * Second step to show events on the home page
     */
    private fun showFollowingUsersEventsSecondStep(followedUserUid: String, lastCall: Boolean) {
        if (maxEventsReached()) return
        // Retrieve the followed user and go to the Third step
        storeDatabaseHelper.retrieveUser(followedUserUid).addOnSuccessListener {
            val followedUser = it.toObject(User::class.java)
            showFollowingUsersEventsThirdStep(followedUser, lastCall)
        }
    }

    /**
     * Third step to show events on the home page
     */
    private fun showFollowingUsersEventsThirdStep(
        followedUser: User?,
        lastCall: Boolean
    ) {
        val refreshingLayout = view?.findViewById<SwipeRefreshLayout>(R.id.eventsSwipeRefresh)
        if (maxEventsReached()) return
        // Get user events ids
        val eventsOwnedIds = followedUser?.getEventsOwned()
        if (eventsOwnedIds != null) {

            if (eventsOwnedIds.isEmpty() && lastCall) refreshingLayout?.isRefreshing = false
            else {

                // Retrieve every event by his eventId
                for (eventId in eventsOwnedIds) {
                    storeDatabaseHelper.retrieveEvent(eventId).addOnSuccessListener {
                        val event = it.toObject(Event::class.java)
                        // Just add it to the view if the event is not deleted
                        if (event != null && !event.isDeleted()) {
                            // Add it to events that will be shown and update the view
                            eventsToShow.add(Pair(event, followedUser))
                            updateShowingEvents()
                        }
                    }
                }
            }
        }
    }

    /**
     * Update the view which contains the events
     */
    private fun updateShowingEvents() {
        // Get the refreshing layout to check his state
        val refreshingLayout = view?.findViewById<SwipeRefreshLayout>(R.id.eventsSwipeRefresh)

        // Sort the events by time
        eventsToShow.sortBy {
            it.first.getCreationDate().seconds
        }

        val eventContainer = view?.findViewById<LinearLayout>(R.id.eventsContainer)
        eventContainer?.removeAllViews()


        for (pair in eventsToShow) {
            val dpSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130f, context?.resources?.displayMetrics).toInt()
            val eventView = LayoutInflater.from(context).inflate(R.layout.event_view, null);
            val eventUserTitle = eventView.findViewById<TextView>(R.id.eventUserTitle)
            val eventViewTitleView = eventView.findViewById<TextView>(R.id.title)
            val eventViewDescriptionView = eventView.findViewById<TextView>(R.id.description)
            val eventViewBannerImage = eventView.findViewById<ImageView>(R.id.eventImage)
            val eventUserImageView = eventView.findViewById<CircleImageView>(R.id.eventUserImage)
            val eventAssistsTextView = eventView.findViewById<TextView>(R.id.eventAssistsTextView)
            val eventLikesTextView = eventView.findViewById<TextView>(R.id.eventLikesTextView)
            val eventUserProfileLayout = eventView.findViewById<LinearLayout>(R.id.eventUserProfileLayout)
            val eventEventLayout = eventView.findViewById<LinearLayout>(R.id.eventEventLayout)

            Picasso.with(context)
                .load(pair.first.getEventImage())
                .resize(dpSize, dpSize)
                .into(eventViewBannerImage)

            Picasso.with(context)
                .load(pair.second.getProfilePicture())
                .into(eventUserImageView)

            eventUserTitle.text = pair.second.getUsername()
            eventAssistsTextView.text = StringUtils.compactNumberString(pair.first.getAssists())
            eventLikesTextView.text = StringUtils.compactNumberString(pair.first.getLikes())
            eventViewTitleView.text = pair.first.getTitle()
            eventViewDescriptionView.text = pair.first.getDescription()


            eventEventLayout.setOnClickListener {
                val intent = Intent(context, EventActivity::class.java)
                intent.putExtra("eventId", pair.first.getId())
                startActivity(intent)
            }

            eventUserProfileLayout.setOnClickListener {
                val intent = if (pair.first.getCreatorUid() == authDatabaseHelper.getCurrentUser()?.uid) {
                    Intent(context, ProfileActivity::class.java)
                } else {
                    Intent(context, EventActivity::class.java)
                }
                intent.putExtra("userId", pair.first.getCreatorUid())
                startActivity(intent)
            }

            eventContainer?.addView(eventView)
        }
        refreshingLayout?.isRefreshing = false
    }

    private fun maxEventsReached() : Boolean {
        return eventsToShow.size == 30
    }
}
