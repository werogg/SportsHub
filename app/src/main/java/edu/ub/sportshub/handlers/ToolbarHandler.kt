package edu.ub.sportshub.handlers

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
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
        val profileImage = appCompatActivity.findViewById<CircleImageView>(R.id.toolbar_image_my_profile)

        profileImage.setOnClickListener {
            onProfileClick(appCompatActivity.applicationContext)
        }

        val homeText = appCompatActivity.findViewById<TextView>(R.id.toolbar_home)

        homeText.setOnClickListener {
            onHomeClick(appCompatActivity.applicationContext)
        }

        val notificationsButton = appCompatActivity.findViewById<ImageView>(R.id.toolbar_notifications)

        notificationsButton.setOnClickListener {
            onNotificationsButtonClicked()
        }

        val profileText = appCompatActivity.findViewById<TextView>(R.id.toolbar_txt_my_profile)

        profileText.setOnClickListener {
            onProfileClick(appCompatActivity.applicationContext)
        }

        setupUserInfo(profileImage, profileText)
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
        val customView = inflater.inflate(R.layout.fragment_notifications_secondary, null)
        val coord = appCompatActivity.findViewById<CoordinatorLayout>(R.id.coordinatorLayout)
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
}