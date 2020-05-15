package edu.ub.sportshub.handlers

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import edu.ub.sportshub.R
import edu.ub.sportshub.data.data.DataAccessObjectFactory
import edu.ub.sportshub.data.enums.NotificationType
import edu.ub.sportshub.data.events.database.DataEvent
import edu.ub.sportshub.data.events.database.UserNotificationsLoadedEvent
import edu.ub.sportshub.data.listeners.DataChangeListener
import edu.ub.sportshub.data.models.notification.NotificationDao
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.home.HomeActivity
import edu.ub.sportshub.models.*
import edu.ub.sportshub.profile.ProfileActivity
import edu.ub.sportshub.profile.ProfileOtherActivity

class ToolbarHandler(private val appCompatActivity: AppCompatActivity) : DataChangeListener {

    private var mAuthDatabaseHelper = AuthDatabaseHelper()
    private var mStoreDatabaseHelper = StoreDatabaseHelper()
    private var notificationsToShow = mutableListOf<Pair<Notification, User>>()
    private lateinit var notificationDao : NotificationDao
    private var numRequest = true

    enum class NotificationsVisibility {
        VISIBLE,
        GONE
    }

    var popupWindow : PopupWindow? = null

    fun setupToolbarBasics() {
        notificationDao = DataAccessObjectFactory.getNotificationDao()
        notificationDao.registerListener(this)

        val name = appCompatActivity::class.java.simpleName

        val homeText = appCompatActivity.findViewById<TextView>(R.id.toolbar_home)

        homeText.setOnClickListener {
            onHomeClick(appCompatActivity.applicationContext)
        }

        val notificationsButton = appCompatActivity.findViewById<ImageView>(R.id.toolbar_notifications)

        notificationsButton.setOnClickListener {
            onNotificationsButtonClicked()
        }

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

        numRequest = true
        notificationDao.fetchUserNotifications(mAuthDatabaseHelper.getCurrentUser()!!.uid)

        setupDatabaseListener()
    }

    private fun setupDatabaseListener() {
        val userId = mAuthDatabaseHelper.getCurrentUser()!!.uid
        mStoreDatabaseHelper.getUsersCollection().document(userId).addSnapshotListener { _, _ ->
            numRequest = true
            notificationDao.fetchUserNotifications(mAuthDatabaseHelper.getCurrentUser()!!.uid)
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
            NotificationsVisibility.VISIBLE -> retrieveNotifications()
            NotificationsVisibility.GONE -> hideNotificationsPopup()
        }
    }

    private fun retrieveNotifications() {
        numRequest = false
        notificationDao.fetchUserNotifications(mAuthDatabaseHelper.getCurrentUser()!!.uid)
    }

    private fun showNotificationsPopup() {
        val displayMetrics = appCompatActivity.applicationContext.resources.displayMetrics
        val dpValue1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350f, displayMetrics)
        val dpValue2 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 480f, displayMetrics)
        val inflater = appCompatActivity.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var customView : View?
        val className = appCompatActivity::class.java.simpleName
        var coord: ViewGroup? = null
        var colorBlack = false

        when (className) {
            "HomeActivity" -> {
                coord = appCompatActivity.findViewById<CoordinatorLayout>(R.id.constrLayout)
                colorBlack = true
            }

            "EventActivity" -> {
                coord = appCompatActivity.findViewById<CoordinatorLayout>(R.id.coordinatorLayout)
                colorBlack = true
            }

            "EditEventActivity" -> {
                coord = appCompatActivity.findViewById<CoordinatorLayout>(R.id.edit_event_constraint_layout)
                colorBlack = true
            }

            "CreateEventActivity" -> {
                coord = appCompatActivity.findViewById<CoordinatorLayout>(R.id.create_event_constraint_layout)
                colorBlack = true
            }

            "ProfileActivity" -> {
                coord = appCompatActivity.findViewById<CoordinatorLayout>(R.id.profile_constraint_layout)
                colorBlack = false
            }

            "ProfileOtherActivity" ->  {
                coord = appCompatActivity.findViewById<CoordinatorLayout>(R.id.profile_constraint_layout)
                colorBlack = false
            }

            "EditProfileActivity" ->  {
                coord = appCompatActivity.findViewById<CoordinatorLayout>(R.id.editprofile_constraint_layout)
                colorBlack = false
            }
            else -> {
                throw Exception("Undefined class tried to use the toolbar.")
            }
        }

        customView = inflater.inflate(R.layout.fragment_notifications, null)
        val notificationsLayout = customView.findViewById<LinearLayout>(R.id.notificationsLayout)

        notificationsToShow.sortByDescending {
            it.first.getDate()
        }

        for (notification in notificationsToShow) {
            val notificationView = LayoutInflater.from(appCompatActivity.applicationContext).inflate(R.layout.notification_view, null);
            val notificationPicture = notificationView.findViewById<CircleImageView>(R.id.notificationPicture)
            val notificationText = notificationView.findViewById<TextView>(R.id.notificationText)

            if (colorBlack) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    notificationView.background = appCompatActivity.getDrawable(R.drawable.layout_notifications_white_border)
                } else {
                    notificationView.setBackgroundColor(appCompatActivity.resources.getColor(R.color.colorPrimaryDark))
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    notificationView.background = appCompatActivity.getDrawable(R.drawable.layout_notifications_white_border_primary)
                } else {
                    notificationView.setBackgroundColor(appCompatActivity.resources.getColor(R.color.colorPrimary))
                }
            }

            when (notification.first.getNotificationType()) {
                NotificationType.FOLLOWED -> {
                    notificationText.text = (notification.first as NotificationFollowed).getMessage(appCompatActivity, notification.second.getUsername())
                }

                NotificationType.ASSIST_TO_FOLLOWERS -> {
                    notificationText.text = (notification.first as NotificationAssist).getMessage(appCompatActivity, notification.second.getUsername())
                }

                NotificationType.ASSIST_TO_CREATOR -> {
                    notificationText.text = (notification.first as NotificationAssist).getMessage(appCompatActivity, notification.second.getUsername())
                }

                NotificationType.LIKED -> {
                    notificationText.text = (notification.first as NotificationLiked).getMessage(appCompatActivity, notification.second.getUsername())
                }
            }

            Picasso.with(appCompatActivity.applicationContext)
                .load(notification.second.getProfilePicture())
                .into(notificationPicture)

            notificationView.setOnClickListener {
                val intent = Intent(appCompatActivity, ProfileOtherActivity::class.java)
                intent.putExtra("userId", notification.second.getUid())
                appCompatActivity.startActivity(intent)
            }

            notificationsLayout.addView(notificationView)
        }

        val notificationsToMark = mutableListOf<Notification>()
        for (notification in notificationsToShow) notificationsToMark.add(notification.first)
        notificationDao.markNotificationsAsChecked(notificationsToMark)

        val notificationsNum = appCompatActivity.findViewById<TextView>(R.id.notificationsNum)
        notificationsNum.text = ""


        popupWindow = PopupWindow(customView, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow!!.width = dpValue1.toInt()
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

    override fun onDataLoaded(event: DataEvent) {
        if (event is UserNotificationsLoadedEvent) {
            notificationsToShow = event.notifications

            if (isNotificationsPopupVisible()) {
                hideNotificationsPopup()
                showNotificationsPopup()
                return
            }

            if (numRequest) {
                showNumNotifications()
            } else {
                numRequest = true
                showNotificationsPopup()
            }
        }
    }

    private fun showNumNotifications() {
        val notificationsNum = appCompatActivity.findViewById<TextView>(R.id.notificationsNum)

        var numCount = 0

        for (notification in notificationsToShow) {
            if (!notification.first.isChecked()) numCount += 1
        }

        if (numCount == 0) {
            notificationsNum.text = ""
        } else {
            notificationsNum.text = numCount.toString()
        }
    }
}