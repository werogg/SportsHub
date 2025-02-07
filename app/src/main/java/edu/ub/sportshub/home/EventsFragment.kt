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
import edu.ub.sportshub.data.data.DataAccessObjectFactory
import edu.ub.sportshub.data.events.database.DataEvent
import edu.ub.sportshub.data.events.database.EventsLoadedEvent
import edu.ub.sportshub.data.events.database.FollowingUsersEventsLoadedEvent
import edu.ub.sportshub.data.listeners.DataChangeListener
import edu.ub.sportshub.data.models.event.EventDao
import edu.ub.sportshub.event.EventActivity
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.models.Event
import edu.ub.sportshub.models.User
import edu.ub.sportshub.profile.ProfileActivity
import edu.ub.sportshub.profile.ProfileOtherActivity
import edu.ub.sportshub.utils.StringUtils

/**
 * A simple [Fragment] subclass.
 */
class EventsFragment : Fragment(), DataChangeListener {

    private var authDatabaseHelper = AuthDatabaseHelper()
    private var eventsToShow = mutableListOf<Pair<Event, User>>()
    private lateinit var eventDao : EventDao
    private var followingUsersEvents : MutableList<Pair<Event, User>>? = null
    private var ownedEvents : MutableList<Pair<Event, User>>? = null
    private var eventsMergedList = mutableListOf<Pair<Event, User>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        eventDao = DataAccessObjectFactory.getEventDao()
        eventDao.registerListener(this)

        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val eventContainer = view.findViewById<LinearLayout>(R.id.eventsContainer)
        val refreshingLayout = view.findViewById<SwipeRefreshLayout>(R.id.eventsSwipeRefresh)

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

    private fun showFollowingUsersEvents() {
        val refreshingLayout = view?.findViewById<SwipeRefreshLayout>(R.id.eventsSwipeRefresh)
        refreshingLayout?.isRefreshing = true
        val loggedUserUid = authDatabaseHelper.getCurrentUser()?.uid.toString()

        Thread {
            kotlin.run {
                eventDao.fetchFollowingUsersEvents(loggedUserUid)
                eventDao.fetchUserEvents(loggedUserUid)
            }
        }.start()
    }

    /**
     * Update the view which contains the events
     */
    private fun updateShowingEvents() {
        // Get the refreshing layout to check his state
        val refreshingLayout = view?.findViewById<SwipeRefreshLayout>(R.id.eventsSwipeRefresh)

        if (!isEventsLoaded()) {
            refreshingLayout?.isRefreshing = false
            return
        }

        eventsToShow = mutableListOf()
        eventsToShow.addAll(eventsMergedList)

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
                    Intent(context, ProfileOtherActivity::class.java)
                }
                intent.putExtra("userId", pair.first.getCreatorUid())
                startActivity(intent)
            }

            eventContainer?.addView(eventView)
        }
        refreshingLayout?.isRefreshing = false
    }

    private fun isEventsLoaded(): Boolean {
        return ownedEvents != null && followingUsersEvents != null
    }

    private fun mergeEventsIntoShowListAndShow() {
        val mergedList = mutableListOf<Pair<Event, User>>()
        if (isEventsLoaded()) {
            mergedList.addAll(ownedEvents!!)
            mergedList.addAll(followingUsersEvents!!)
            eventsMergedList = mergedList
            updateShowingEvents()
        }
    }

    override fun onDataLoaded(event: DataEvent) {
        if (event is FollowingUsersEventsLoadedEvent) {
            followingUsersEvents = event.eventList
            val refreshingLayout = view?.findViewById<SwipeRefreshLayout>(R.id.eventsSwipeRefresh)
            if (followingUsersEvents!!.isEmpty()) refreshingLayout?.isRefreshing = false

        } else if (event is EventsLoadedEvent) {
            val tempList = mutableListOf<Pair<Event, User>>()

            for (loadedEvent in event.eventList)
                tempList.add(Pair(loadedEvent, event.owner))

            ownedEvents = tempList
        }

        if (isEventsLoaded()) mergeEventsIntoShowListAndShow()
    }

    override fun onResume() {
        super.onResume()
        showFollowingUsersEvents()
    }
}
