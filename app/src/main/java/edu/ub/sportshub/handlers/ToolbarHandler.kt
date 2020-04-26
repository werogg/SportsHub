package edu.ub.sportshub.handlers

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import edu.ub.sportshub.R
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.home.HomeActivity
import edu.ub.sportshub.models.User
import edu.ub.sportshub.profile.ProfileActivity

class ToolbarHandler(private val appCompatActivity: AppCompatActivity) {

    private var mAuthDatabaseHelper = AuthDatabaseHelper()
    private var mStoreDatabaseHelper = StoreDatabaseHelper()

    enum class NotificationsVisibility {
        VISIBLE,
        GONE
    }

    var popupWindow : PopupWindow? = null

    fun setupToolbarBasics() {
        val name = appCompatActivity::class.java.simpleName

        val homeText = appCompatActivity.findViewById<TextView>(R.id.toolbar_home)

        homeText.setOnClickListener {
            onHomeClick(appCompatActivity.applicationContext)
        }

        val notificationsButton = appCompatActivity.findViewById<ImageView>(R.id.toolbar_notifications)

        notificationsButton.setOnClickListener {
            onNotificationsButtonClicked()
        }

        if (name == "ProfileActivity" || name == "EditProfileActivity") {
            val signoutButton = appCompatActivity.findViewById<TextView>(R.id.toolbar_signout)

            signoutButton.setOnClickListener {
                onSignOutButton()
            }
        } else {
            val profileImage = appCompatActivity.findViewById<CircleImageView>(R.id.toolbar_image_my_profile)

            profileImage.setOnClickListener {
                onProfileClick(appCompatActivity.applicationContext)
            }

            val profileText = appCompatActivity.findViewById<TextView>(R.id.toolbar_txt_my_profile)

            profileText.setOnClickListener {
                onProfileClick(appCompatActivity.applicationContext)
            }
            setupUserInfo(profileImage, profileText)
        }
    }

    private fun onHomeClick(applicationContext: Context) {
        val goHome = Intent(applicationContext, HomeActivity::class.java)
        goHome.flags = FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(goHome)
    }

    private fun onProfileClick(applicationContext: Context) {
        val goProfile = Intent(applicationContext, ProfileActivity::class.java)
        goProfile.flags = FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(goProfile)
    }

    private fun onNotificationsButtonClicked() {
        setNotificationsPopupVisibility(NotificationsVisibility.VISIBLE)
    }

    fun isNotificationsPopupVisible(): Boolean {
        if (popupWindow != null) {
            return popupWindow!!.isShowing
        }
        return false
    }

    fun setNotificationsPopupVisibility(visibility : NotificationsVisibility) {
        when (visibility) {
            NotificationsVisibility.VISIBLE -> showNotificationsPopup()
            NotificationsVisibility.GONE -> hideNotificationsPopup()
        }
    }

    private fun showNotificationsPopup() {
        val displayMetrics = appCompatActivity.applicationContext.resources.displayMetrics
        val dpValue1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350f, displayMetrics)
        val dpValue2 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 480f, displayMetrics)
        val inflater = appCompatActivity.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var customView : View?
        val className = appCompatActivity::class.java.simpleName
        var coord: ViewGroup? = null

        when (className) {
            "HomeActivity" -> {
                coord = appCompatActivity.findViewById<CoordinatorLayout>(R.id.constrLayout)
                customView = inflater.inflate(R.layout.fragment_notifications_secondary, null)
            }

            "EventActivity" -> {
                coord = appCompatActivity.findViewById<CoordinatorLayout>(R.id.coordinatorLayout)
                customView = inflater.inflate(R.layout.fragment_notifications_secondary, null)
            }

            "EditEventActivity" -> {
                coord = appCompatActivity.findViewById<CoordinatorLayout>(R.id.edit_event_constraint_layout)
                customView = inflater.inflate(R.layout.fragment_notifications_secondary, null)
            }

            "CreateEventActivity" -> {
                coord = appCompatActivity.findViewById<CoordinatorLayout>(R.id.create_event_constraint_layout)
                customView = inflater.inflate(R.layout.fragment_notifications_secondary, null)
            }

            "ProfileActivity" -> {
                coord = appCompatActivity.findViewById<CoordinatorLayout>(R.id.profile_constraint_layout)
                customView = inflater.inflate(R.layout.fragment_notifications_primary, null)
            }

            "ProfileOtherActivity" ->  {
                coord = appCompatActivity.findViewById<CoordinatorLayout>(R.id.profile_constraint_layout)
                customView = inflater.inflate(R.layout.fragment_notifications_primary, null)
            }

            "EditProfileActivity" ->  {
                coord = appCompatActivity.findViewById<CoordinatorLayout>(R.id.editprofile_constraint_layout)
                customView = inflater.inflate(R.layout.fragment_notifications_primary, null)
            }
            else -> {
                throw Exception("Undefined class tried to use the toolbar.")
            }
        }

        popupWindow = PopupWindow(customView, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT, true)
        popupWindow!!.width = dpValue1.toInt()
        popupWindow!!.height = dpValue2.toInt()
        popupWindow!!.showAtLocation(coord, Gravity.TOP,0,220)
    }

    private fun hideNotificationsPopup() {
        if (popupWindow != null) popupWindow!!.dismiss()
    }

    private fun setupUserInfo(profileImage: ImageView, usernameText : TextView) {

        if (mAuthDatabaseHelper.getCurrentUser() != null) {
            val loggedUserUid = mAuthDatabaseHelper.getCurrentUser()!!.uid

            mStoreDatabaseHelper.retrieveUser(loggedUserUid).addOnSuccessListener {
                val loggedUser = it.toObject(User::class.java)!!

                Picasso.with(appCompatActivity.applicationContext)
                    .load(loggedUser.getProfilePicture())
                    .into(profileImage)

                usernameText.text = loggedUser.getUsername()
            }
        }
    }

    private fun onSignOutButton() {
        mAuthDatabaseHelper.signOut(appCompatActivity)
    }
}