package edu.ub.sportshub.event

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hdodenhof.circleimageview.CircleImageView
import edu.ub.sportshub.R
import edu.ub.sportshub.home.HomeActivity
import edu.ub.sportshub.profile.ProfileActivity

class EventActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMapView : MapView
    private var popupWindowImage : PopupWindow? = null
    private var popupWindow : PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        setupActivityFunctionalities(savedInstanceState)
    }

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

        val profileText = findViewById<TextView>(R.id.toolbar_secondary_txt_my_profile)

        profileText.setOnClickListener {
            onProfileClick()
        }

        val profileImage = findViewById<CircleImageView>(R.id.toolbar_secondary_image_my_profile)

        profileImage.setOnClickListener {
            onProfileClick()
        }

        val homeText = findViewById<TextView>(R.id.toolbar_secondary_home)

        homeText.setOnClickListener {
            onHomeClick()
        }

        val editButton = findViewById<FloatingActionButton>(R.id.event_edit_event_floating_button)

        editButton.setOnClickListener {
            onEditEventButtonClicked()
        }

        val notificationsButton = findViewById<ImageView>(R.id.toolbar_secondary_notifications)

        notificationsButton.setOnClickListener {
            notificationsButtonClicked()
        }
    }

    private fun onEditEventButtonClicked() {
        val goEdit = Intent(this, EditEventActivity::class.java)
        startActivity(goEdit)
    }

    private fun onHomeClick() {
        val goHome = Intent(this, HomeActivity::class.java)
        startActivity(goHome)
    }

    private fun onProfileClick() {
        val goProfile = Intent(this, ProfileActivity::class.java)
        startActivity(goProfile)
    }

    private fun onBannerImageClick() {
        val inflater = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.full_image, null)
        val coord = findViewById<CoordinatorLayout>(R.id.coordinatorLayout)
        popupWindowImage = PopupWindow(customView, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        val image = customView.findViewById(R.id.fullImage) as ImageView
        image.setImageResource(R.drawable.ic_launcher_background)
        //pwindow.isOutsideTouchable = true
        //pwindow.isFocusable = true
        popupWindowImage!!.showAtLocation(coord, Gravity.CENTER,0,0)

        val goBackButton = customView.findViewById(R.id.goBackFloatingButton) as FloatingActionButton
        goBackButton.setOnClickListener {
            popupWindowImage!!.dismiss()
            popupWindowImage = null
        }

    }

    private fun notificationsButtonClicked() {
        val inflater = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.fragment_notifications_secondary, null)
        val coord = findViewById<CoordinatorLayout>(R.id.coordinatorLayout)
        popupWindow = PopupWindow(customView, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT, true)
        popupWindow!!.width = coord.width
        popupWindow!!.height = coord.height
        popupWindow!!.showAtLocation(coord, Gravity.CENTER,0,0)
    }

    override fun onBackPressed() {
        if (popupWindowImage != null) {
            popupWindowImage!!.dismiss()
            popupWindowImage = null
        } else if (popupWindow != null) {
            popupWindow!!.dismiss()
            popupWindow = null
        }
        else {
            val goHome = Intent(this, HomeActivity::class.java)
            startActivity(goHome)
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val eventName = findViewById<TextView>(R.id.eventNameTextView)
        val coords = LatLng(41.3855398, 2.1639748)
        googleMap?.addMarker(MarkerOptions().position(coords).title(eventName.text.toString()))
        val location = CameraUpdateFactory.newLatLngZoom(coords, 15F)
        googleMap?.animateCamera(location)
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
