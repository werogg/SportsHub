package edu.ub.sportshub.event

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FieldValue
import com.squareup.picasso.Picasso
import edu.ub.sportshub.R
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.models.Event
import edu.ub.sportshub.models.User
import edu.ub.sportshub.utils.StringUtils
import edu.ub.sportshub.handlers.ToolbarHandler

class EventActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mAuthDatabaseHelper = AuthDatabaseHelper()
    private var mStoreDatabaseHelper = StoreDatabaseHelper()
    private lateinit var mMapView : MapView
    private var popupWindowImage : PopupWindow? = null
    private var eventId : String? = null
    private var event : Event? = null
    private var googleMap : GoogleMap? = null
    private var liked = false
    private var assist = false
    private val toolbarHandler = ToolbarHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        eventId = intent.getStringExtra("eventId")
        setupActivityFunctionalities(savedInstanceState)

        if (eventId != null) {
            mStoreDatabaseHelper.retrieveEvent(eventId!!).addOnSuccessListener {
                event = it.toObject(Event::class.java)

                if (event?.getCreatorUid()!! == mAuthDatabaseHelper.getCurrentUser()?.uid) {
                    val editButton = findViewById<FloatingActionButton>(R.id.event_edit_event_floating_button)
                    editButton.visibility = View.VISIBLE
                }

                updateEventInfo()
            } .addOnFailureListener {
                finish()
            }
        }

        toolbarHandler.setupToolbarBasics()
    }

    /**
     * Update the event related info
     */
    private fun updateEventInfo() {
        // Retrieve all info views
        val assistsTextView = findViewById<TextView>(R.id.assistsTextView)
        val likesTextView = findViewById<TextView>(R.id.likesTextView)
        val eventTitleTextView = findViewById<TextView>(R.id.eventNameTextView)
        val eventDescriptionTextView = findViewById<TextView>(R.id.descTextView)
        val eventBannerImageView = findViewById<ImageView>(R.id.bannerImage)
        val dateTextView = findViewById<TextView>(R.id.dateTextView)
        val hourTextView = findViewById<TextView>(R.id.hourTextView)
        val addressTextView = findViewById<TextView>(R.id.addressTextView)
        val likeButton = findViewById<ExtendedFloatingActionButton>(R.id.like_floating_button)
        val creatorText = findViewById<TextView>(R.id.creatorText)
        val creatorImage = findViewById<ImageView>(R.id.creatorImage)

        // Update them with event info
        eventDescriptionTextView.text = event?.getDescription()
        eventTitleTextView.text = event?.getTitle()

        likesTextView.text = StringUtils.compactNumberString(event?.getLikes()!!)
        assistsTextView.text = StringUtils.compactNumberString(event?.getAssists()!!)

        if (event?.getUsersLiked()!!.contains(mAuthDatabaseHelper.getCurrentUser()?.uid)) {
            liked = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                likeButton.icon = getDrawable(R.drawable.baseline_thumb_up_alt_24)
            } else {
                likeButton.icon = resources.getDrawable(R.drawable.baseline_thumb_up_alt_24)
            }
        }

        mStoreDatabaseHelper.retrieveUser(event?.getCreatorUid().toString()).addOnSuccessListener {
            val creatorUser = it.toObject(User::class.java)
            Picasso.with(applicationContext)
                .load(creatorUser?.getProfilePicture())
                .into(creatorImage)

            creatorText.text = creatorUser?.getUsername()
        }

        Picasso.with(applicationContext)
            .load(event?.getEventImage())
            .into(eventBannerImageView)

        dateTextView.text = StringUtils.getFormatedDateFromTimestamp(event?.getStartEventDate()!!)
        hourTextView.text = StringUtils.getFormatedHourFromTimestamp(event?.getStartEventDate()!!)
        addressTextView.text = StringUtils.getAddressFromLocation(applicationContext,
            event?.getPosition()?.latitude!!, event?.getPosition()?.longitude!!)

        // Animate and focus google map
        if (googleMap != null) {
            val coords = LatLng(event!!.getPosition().latitude, event!!.getPosition().longitude)
            googleMap?.addMarker(MarkerOptions().position(coords).title(event?.getTitle()))
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
        // go to profile
    }

    private fun onAssistButtonClicked() {
        // TODO notifications

        if (eventId != null) {

            val assistButton = findViewById<ExtendedFloatingActionButton>(R.id.will_assist_floating_button)
            val userId = mAuthDatabaseHelper.getCurrentUser()?.uid

            if (assist) {
                // Remove the user from event's user liked list
                mStoreDatabaseHelper.retrieveEventRef(eventId!!)
                    .update(
                        "usersAssists",
                        FieldValue.arrayRemove(userId)
                    )
                // Remove the event from the user's events liked list
                mStoreDatabaseHelper.retrieveUserRef(userId!!)
                    .update("eventsAssist",
                        FieldValue.arrayRemove(eventId)
                    )
                assist = false

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    assistButton.icon = getDrawable(R.drawable.outline_star_border_24)
                } else {
                    assistButton.icon = resources.getDrawable(R.drawable.outline_star_border_24)
                }
            } else {
                // Add the user to event's user liked list
                mStoreDatabaseHelper.retrieveEventRef(eventId!!)
                    .update(
                        "usersAssists",
                        FieldValue.arrayUnion(mAuthDatabaseHelper.getCurrentUser()?.uid)
                    )

                // Add the event from to user's events liked list
                mStoreDatabaseHelper.retrieveUserRef(userId!!)
                    .update("eventsAssist",
                        FieldValue.arrayUnion(eventId)
                    )

                assist = true

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    assistButton.icon = getDrawable(R.drawable.baseline_star_24)
                } else {
                    assistButton.icon = resources.getDrawable(R.drawable.baseline_star_24)
                }
            }
        }
    }

    private fun onLikeButtonClicked() {
        // TODO notifications

        if (eventId != null) {
            val likeButton = findViewById<ExtendedFloatingActionButton>(R.id.like_floating_button)
            val userId = mAuthDatabaseHelper.getCurrentUser()?.uid

            if (liked) {
                // Remove the user from event's user liked list
                mStoreDatabaseHelper.retrieveEventRef(eventId!!)
                    .update(
                        "usersLiked",
                        FieldValue.arrayRemove(userId)
                    )
                // Remove the event from the user's events liked list
                mStoreDatabaseHelper.retrieveUserRef(userId!!)
                    .update("eventsLiked",
                        FieldValue.arrayRemove(eventId)
                    )
                liked = false

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    likeButton.icon = getDrawable(R.drawable.outline_thumb_up_alt_24)
                } else {
                    likeButton.icon = resources.getDrawable(R.drawable.outline_thumb_up_alt_24)
                }
            } else {
                // Add the user to event's user liked list
                mStoreDatabaseHelper.retrieveEventRef(eventId!!)
                    .update(
                        "usersLiked",
                        FieldValue.arrayUnion(mAuthDatabaseHelper.getCurrentUser()?.uid)
                    )

                // Add the event from to user's events liked list
                mStoreDatabaseHelper.retrieveUserRef(userId!!)
                    .update("eventsLiked",
                        FieldValue.arrayUnion(eventId)
                    )

                liked = true

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    likeButton.icon = getDrawable(R.drawable.baseline_thumb_up_alt_24)
                } else {
                    likeButton.icon = resources.getDrawable(R.drawable.baseline_thumb_up_alt_24)
                }
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
            .load(event?.getEventImage())
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
}
