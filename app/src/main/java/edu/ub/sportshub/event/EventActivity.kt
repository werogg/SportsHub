package edu.ub.sportshub.event

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import edu.ub.sportshub.R
import edu.ub.sportshub.data.data.DataAccessObjectFactory
import edu.ub.sportshub.data.enums.NotificationType
import edu.ub.sportshub.data.events.database.DataEvent
import edu.ub.sportshub.data.events.database.EventLoadedEvent
import edu.ub.sportshub.data.events.database.UserLoadedEvent
import edu.ub.sportshub.data.listeners.DataChangeListener
import edu.ub.sportshub.data.models.event.EventDao
import edu.ub.sportshub.data.models.notification.NotificationDao
import edu.ub.sportshub.data.models.user.UserDao
import edu.ub.sportshub.handlers.ToolbarHandler
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.models.Event
import edu.ub.sportshub.models.User
import edu.ub.sportshub.profile.ProfileOtherActivity
import edu.ub.sportshub.utils.StringUtils

class EventActivity : AppCompatActivity(), OnMapReadyCallback, DataChangeListener {

    private var mAuthDatabaseHelper = AuthDatabaseHelper()
    private lateinit var mMapView : MapView
    private var popupWindowImage : PopupWindow? = null
    private var eventId : String? = null
    private var googleMap : GoogleMap? = null
    private var liked = false
    private var assist = false
    private val toolbarHandler = ToolbarHandler(this)
    private lateinit var userDao : UserDao
    private lateinit var eventDao : EventDao
    private lateinit var notificationDao : NotificationDao
    private lateinit var loadingDialog : Dialog
    private var loadedUser : User? = null
    private var loadedEvent : Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userDao = DataAccessObjectFactory.getUserDao()
        eventDao = DataAccessObjectFactory.getEventDao()
        notificationDao = DataAccessObjectFactory.getNotificationDao()

        userDao.registerListener(this)
        eventDao.registerListener(this)

        setContentView(R.layout.activity_event)
        showDialog()
        eventId = intent.getStringExtra("eventId")
        setupActivityFunctionalities(savedInstanceState)

        if (eventId != null) {
            eventDao.fetchEvent(eventId!!)
        }

        toolbarHandler.setupToolbarBasics()
    }

    private fun showDialog(){
        //Dialog creation for loading data.
        val dialog = Dialog(this,R.style.Theme_Design_Light)
        val view: View = LayoutInflater.from(this).inflate(R.layout.layout_loading, null)
        val params: WindowManager.LayoutParams = dialog.window!!.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.setContentView(view)
        loadingDialog = dialog
        loadingDialog.show()
    }
    /**
     * Update the event related info
     */
    private fun updateEventInfo() {

        val assistsTextView = findViewById<TextView>(R.id.assistsTextView)
        val likesTextView = findViewById<TextView>(R.id.likesTextView)
        val eventTitleTextView = findViewById<TextView>(R.id.eventNameTextView)
        val eventDescriptionTextView = findViewById<TextView>(R.id.descTextView)
        val eventBannerImageView = findViewById<ImageView>(R.id.bannerImage)
        val dateTextView = findViewById<TextView>(R.id.dateTextView)
        val hourTextView = findViewById<TextView>(R.id.hourTextView)
        val addressTextView = findViewById<TextView>(R.id.addressTextView)
        val creatorText = findViewById<TextView>(R.id.creatorText)
        val creatorImage = findViewById<ImageView>(R.id.creatorImage)

        // Update them with event info
        eventDescriptionTextView.text = loadedEvent?.getDescription()
        eventTitleTextView.text = loadedEvent?.getTitle()
        likesTextView.text = StringUtils.compactNumberString(loadedEvent?.getLikes())
        assistsTextView.text = StringUtils.compactNumberString(loadedEvent?.getAssists())
        creatorText.text = loadedUser?.getUsername()
        dateTextView.text = StringUtils.getFormatedDateFromTimestamp(loadedEvent?.getStartEventDate()!!)
        hourTextView.text = StringUtils.getFormatedHourFromTimestamp(loadedEvent?.getStartEventDate()!!)
        addressTextView.text = StringUtils.getAddressFromLocation(applicationContext,
            loadedEvent?.getPosition()?.latitude!!, loadedEvent?.getPosition()?.longitude!!)

        Picasso.with(applicationContext)
            .load(loadedUser?.getProfilePicture())
            .into(creatorImage)

        Picasso.with(applicationContext)
            .load(loadedEvent?.getEventImage())
            .into(eventBannerImageView)

        setupMap()
        checkUserLikeAssist()
        loadingDialog.dismiss()
    }

    private fun checkUserLikeAssist() {
        val likeButton = findViewById<ExtendedFloatingActionButton>(R.id.like_floating_button)
        val assistButton = findViewById<ExtendedFloatingActionButton>(R.id.will_assist_floating_button)
        val currentUserUid = mAuthDatabaseHelper.getCurrentUser()?.uid

        if (loadedEvent?.getUsersLiked()!!.contains(currentUserUid)) {
            liked = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                likeButton.icon = getDrawable(R.drawable.baseline_thumb_up_alt_24)
            } else {
                likeButton.icon = resources.getDrawable(R.drawable.baseline_thumb_up_alt_24)
            }
        }

        if (loadedEvent?.getUsersAssists()!!.contains(currentUserUid)) {
            assist = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                assistButton.icon = getDrawable(R.drawable.baseline_star_24)
            } else {
                likeButton.icon = resources.getDrawable(R.drawable.baseline_star_24)
            }
        }
    }

    private fun setupMap() {
        // Animate and focus google map
        if (googleMap != null) {
            val coords = LatLng(loadedEvent!!.getPosition().latitude, loadedEvent!!.getPosition().longitude)
            googleMap?.addMarker(MarkerOptions().position(coords).title(loadedEvent?.getTitle()))
            val location = CameraUpdateFactory.newLatLngZoom(coords, 15F)
            googleMap?.animateCamera(location)
        }
    }

    /**
     * Setup all activity functionalities
     */
    private fun setupActivityFunctionalities(savedInstanceState: Bundle?) {
        setupListeners()
        setupMaps(savedInstanceState)
    }

    private fun setupMaps(savedInstanceState: Bundle?) {
        mMapView = findViewById(R.id.mapView)

        mMapView.onCreate(savedInstanceState)

        mMapView.getMapAsync(this)
    }

    private fun setupListeners() {
        val collapsableEventPicture = findViewById<AppBarLayout>(R.id.collapsableEventPicture)

        collapsableEventPicture.setOnClickListener {
            onBannerImageClick()
        }

        val editButton = findViewById<FloatingActionButton>(R.id.event_edit_event_floating_button)

        editButton.setOnClickListener {
            onEditEventButtonClicked()
        }

        val likeButton = findViewById<ExtendedFloatingActionButton>(R.id.like_floating_button)

        likeButton.setOnClickListener {
            onLikeButtonClicked()
        }

        val assistButton = findViewById<ExtendedFloatingActionButton>(R.id.will_assist_floating_button)

        assistButton.setOnClickListener {
            onAssistButtonClicked()
        }

        val creatorButton = findViewById<ConstraintLayout>(R.id.creatorButton)

        creatorButton.setOnClickListener {
            onCreatorButtonClicked()
        }
    }

    private fun onCreatorButtonClicked() {
        val intent = Intent(applicationContext, ProfileOtherActivity::class.java)
        intent.putExtra("userId", loadedEvent?.getCreatorUid())
        startActivity(intent)
    }

    private fun onAssistButtonClicked() {

        val userId = mAuthDatabaseHelper.getCurrentUser()?.uid

        if (loadedEvent != null && userId != null) {
            val assistButton = findViewById<ExtendedFloatingActionButton>(R.id.will_assist_floating_button)

            eventDao.giveAssist(userId, loadedEvent!!.getId())

            if (assist) {
                assist = false

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    assistButton.icon = getDrawable(R.drawable.outline_star_border_24)
                } else {
                    assistButton.icon = resources.getDrawable(R.drawable.outline_star_border_24)
                }
            } else {
                assist = true

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    assistButton.icon = getDrawable(R.drawable.baseline_star_24)
                } else {
                    assistButton.icon = resources.getDrawable(R.drawable.baseline_star_24)
                }

                notificationDao.sendEventNotificationToCreator(userId, loadedEvent!!.getCreatorUid(), loadedEvent!!.getTitle(), NotificationType.ASSIST_TO_CREATOR)
                notificationDao.sendEventNotificationToFollowers(userId, loadedEvent!!.getTitle(), NotificationType.ASSIST_TO_FOLLOWERS)
            }
        }
    }

    private fun onLikeButtonClicked() {
        val userId = mAuthDatabaseHelper.getCurrentUser()?.uid

        if (loadedEvent != null && userId != null) {
            val likeButton = findViewById<ExtendedFloatingActionButton>(R.id.like_floating_button)

            eventDao.giveLike(userId, loadedEvent!!.getId())

            if (liked) {
                liked = false

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    likeButton.icon = getDrawable(R.drawable.outline_thumb_up_alt_24)
                } else {
                    likeButton.icon = resources.getDrawable(R.drawable.outline_thumb_up_alt_24)
                }
            } else {
                liked = true

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    likeButton.icon = getDrawable(R.drawable.baseline_thumb_up_alt_24)
                } else {
                    likeButton.icon = resources.getDrawable(R.drawable.baseline_thumb_up_alt_24)
                }

                notificationDao.sendEventNotificationToCreator(userId, loadedEvent!!.getCreatorUid(), loadedEvent!!.getTitle(), NotificationType.LIKED)
            }
        }
    }

    private fun onEditEventButtonClicked() {
        val goEdit = Intent(this, EditEventActivity::class.java)
        goEdit.putExtra("eventId",eventId)
        startActivity(goEdit)
    }

    private fun onBannerImageClick() {
        val inflater = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.full_image, null)
        val coord = findViewById<CoordinatorLayout>(R.id.coordinatorLayout)
        popupWindowImage = PopupWindow(customView, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        val image = customView.findViewById(R.id.fullImage) as ImageView
        Picasso.with(applicationContext)
            .load(loadedEvent?.getEventImage())
            .into(image)
        popupWindowImage!!.showAtLocation(coord, Gravity.CENTER,0,0)

        val goBackButton = customView.findViewById(R.id.goBackFloatingButton) as FloatingActionButton
        goBackButton.setOnClickListener {
            popupWindowImage!!.dismiss()
            popupWindowImage = null
        }
    }

    override fun onBackPressed() {
        if (toolbarHandler.isNotificationsPopupVisible()) toolbarHandler.setNotificationsPopupVisibility(ToolbarHandler.NotificationsVisibility.GONE)
        else if (popupWindowImage != null && popupWindowImage!!.isShowing) popupWindowImage!!.dismiss()
        else super.onBackPressed()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap
    }

    override fun onStart() {
        super.onStart()
        mMapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mMapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    override fun onDataLoaded(event: DataEvent) {
        if (event is UserLoadedEvent) {
            loadedUser = event.user
            onAllDataLoaded()
        }
        else if (event is EventLoadedEvent) {
            loadedEvent = event.event
            eventFetched()
        }
    }

    private fun onAllDataLoaded() {
        updateEventInfo()
    }

    private fun eventFetched() {
        userDao.fetchUser(loadedEvent!!.getCreatorUid())
        checkEditButton()
    }

    private fun checkEditButton() {
        if (loadedEvent?.getCreatorUid() == mAuthDatabaseHelper.getCurrentUser()?.uid) {
            val editButton = findViewById<FloatingActionButton>(R.id.event_edit_event_floating_button)
            editButton.visibility = View.VISIBLE

            val creatorButton = findViewById<ConstraintLayout>(R.id.creatorButton)
            creatorButton.isEnabled = false
        }

    }
}
