package edu.ub.sportshub.profile

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.viewpager.widget.ViewPager
import com.google.firebase.firestore.FieldValue
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import edu.ub.sportshub.R
import edu.ub.sportshub.auth.login.LoginActivity
import edu.ub.sportshub.data.data.DataAccessObjectFactory
import edu.ub.sportshub.data.enums.NotificationType
import edu.ub.sportshub.data.events.database.DataEvent
import edu.ub.sportshub.data.events.database.UserLoadedEvent
import edu.ub.sportshub.data.listeners.DataChangeListener
import edu.ub.sportshub.data.models.notification.NotificationDao
import edu.ub.sportshub.data.models.user.UserDao
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.home.HomeActivity
import edu.ub.sportshub.models.User
import edu.ub.sportshub.profile.events.ViewPagerAdapterProfile
import edu.ub.sportshub.profile.follow.ProfileUsersActivity
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileOtherActivity : AppCompatActivity(), DataChangeListener {
    private var popupWindow : PopupWindow? = null
    private val mFirebaseAuth = AuthDatabaseHelper()
    private val mStoreDatabaseHelper = StoreDatabaseHelper()
    private var mDots = arrayListOf<TextView>()
    private var userfollow: User? = null
    private lateinit var userDao : UserDao
    private lateinit var notificationDao : NotificationDao
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_other)
        val id = intent.getStringExtra("userId")
        uid = id
        val fragmentAdapter2 = ViewPagerAdapterProfile(supportFragmentManager, id)
        pager_profile.adapter = fragmentAdapter2
        userDao = DataAccessObjectFactory.getUserDao()
        notificationDao = DataAccessObjectFactory.getNotificationDao()
        userDao.registerListener(this)

        Thread {
            kotlin.run {
                userDao.fetchUser(id)
            }
        }.start()
        addDots(0)

                pager_profile?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(state: Int) {
            }
                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
                    override fun onPageSelected(position: Int) {
                        addDots(position)
            }
                })

                val followProfileText = findViewById<Button>(R.id.btn_profile_other)
                followProfileText.setOnClickListener() {
                    followProfileTextClicked()
                }

                val textSignOut = findViewById<TextView>(R.id.toolbar_signout)
                textSignOut.setOnClickListener() {
                    textSignOutClicked()
                }

                val home = findViewById<TextView>(R.id.toolbar_home)
                home.setOnClickListener() {
                    buttonHomeClicked()
                }

                val notificationsButton =
                    findViewById<ImageView>(R.id.toolbar_notifications)

                notificationsButton.setOnClickListener {
                    notificationsButtonClicked()
                }

                val layoutFollowers = findViewById<LinearLayout>(R.id.layout_followers)

                layoutFollowers.setOnClickListener(){
                    followersClicked()
                }

                val layoutFollowees = findViewById<LinearLayout>(R.id.layout_followees)

                layoutFollowees.setOnClickListener(){
                    followeesClicked()
                }
    }

    private fun followeesClicked(){
        val popupIntent = Intent(this, ProfileUsersActivity::class.java)
        popupIntent.putExtra("select",1)
        popupIntent.putExtra("id", uid)
        startActivity(popupIntent)
    }

    private fun followersClicked(){
        val popupIntent = Intent(this, ProfileUsersActivity::class.java)
        popupIntent.putExtra("select", 0)
        popupIntent.putExtra("id", uid)
        startActivity(popupIntent)
    }

    private fun notificationsButtonClicked() {
        val displayMetrics = applicationContext.resources.displayMetrics
        val dpValue1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350f, displayMetrics)
        val dpValue2 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 480f, displayMetrics)
        val inflater = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.fragment_notifications, null)
        val coordinates = findViewById<ConstraintLayout>(R.id.profile_constraint_layout)
        popupWindow = PopupWindow(customView, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT, true)
        popupWindow!!.width = dpValue1.toInt()
        popupWindow!!.height = dpValue2.toInt()
        popupWindow!!.showAtLocation(coordinates, Gravity.TOP,0,220)
    }

    private fun buttonHomeClicked() {
        val intent = Intent(this,HomeActivity::class.java)
        startActivity(intent)
    }

    private fun textSignOutClicked() {
        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
    }

    private fun followProfileTextClicked() {

        val follower = mFirebaseAuth.getCurrentUser()?.uid!!

        if (userfollow != null) {
            val followerDoc = mStoreDatabaseHelper.getUsersCollection().document(follower)
            val followedDoc = mStoreDatabaseHelper.getUsersCollection().document(uid)

            if (!userfollow!!.getFollowersUsers().contains(follower)) {
                followerDoc.update("followingUsers", FieldValue.arrayUnion(userfollow!!.getUid()))
                    .addOnSuccessListener {
                        followedDoc.update("followersUsers", FieldValue.arrayUnion(follower))
                            .addOnSuccessListener {
                                // TODO: notify follow
                                notificationDao.sendNotification(follower, uid, NotificationType.FOLLOWED)
                            }
                    }
            } else {
                followerDoc.update("followingUsers", FieldValue.arrayRemove(userfollow!!.getUid()))
                    .addOnSuccessListener {
                        followedDoc.update("followersUsers", FieldValue.arrayRemove(follower))
                            .addOnSuccessListener {
                                // TODO: notify unfollow
                            }
                            .addOnFailureListener {
                                Log.e("errrorr", it.message)
                            }
                    }
            }
            userDao.fetchUser(uid)
        }
    }

    private fun addDots(position : Int){
        mDotLayout.removeAllViews()
        mDots.clear()
        for (i in 0..1){
            val text = TextView(this)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                text.text = Html.fromHtml("&#8226", HtmlCompat.FROM_HTML_MODE_LEGACY)
            } else {
                text.text = Html.fromHtml("&#8226")
            }
            text.textSize = 35F
            if (i == position){
                text.setTextColor(resources.getColor(R.color.colorPrimaryDark))
            } else {
                text.setTextColor(resources.getColor(R.color.common_google_signin_btn_text_dark_disabled))
            }
            mDots.add(i,text)
            mDotLayout.addView(text)
        }
    }

    private fun loadData(user:User){
        userfollow = user
        val textName = findViewById<TextView>(R.id.txt_nameprofile)
        val imageProfile = findViewById<CircleImageView>(R.id.img_profile)
        val description = findViewById<TextView>(R.id.txt_descrp)
        val textFollowers = findViewById<TextView>(R.id.txt_nfollowers)
        val textFollowing = findViewById<TextView>(R.id.txt_nfollowing)
        val buttonFollow = findViewById<Button>(R.id.btn_profile_other)

        textName.text = user.getUsername()
        //Check Image
        val dpSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130f, resources?.displayMetrics).toInt()
        if (user.getProfilePicture() == ""){
            Picasso.with(this)
                .load(R.mipmap.ic_usuari_foreground)
                .resize(dpSize, dpSize)
                .into(imageProfile)
        } else {
            Picasso.with(this)
                .load(user.getProfilePicture().toString())
                .resize(dpSize, dpSize)
                .into(imageProfile)
        }

        if (user.getBiography() == ""){
            description.text = resources.getText(R.string.default_description)
        }else{
            description.text = user.getBiography()
        }
        textFollowers.text = user.getFollowersUsers().size.toString()
        textFollowing.text = user.getFollowingUsers().size.toString()

        val follower = mFirebaseAuth.getCurrentUser()?.uid!!

        if (userfollow!!.getFollowersUsers().contains(follower)) {
            buttonFollow.text = getString(R.string.unfollow)
        } else {
            buttonFollow.text = getString(R.string.follow)
        }
    }

    override fun onDataLoaded(event: DataEvent) {
        if(event is UserLoadedEvent){
            loadData(event.user)
        }
    }


}
