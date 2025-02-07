package edu.ub.sportshub.profile

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
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
import edu.ub.sportshub.handlers.ToolbarHandler
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
    private lateinit var user : User
    private lateinit var userDao : UserDao
    private lateinit var notificationDao : NotificationDao
    private val toolbarHandler = ToolbarHandler(this)
    private lateinit var uid: String
    private lateinit var dialog: Dialog

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

        dialogShow()

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

                val layoutFollowers = findViewById<LinearLayout>(R.id.layout_followers)

                layoutFollowers.setOnClickListener(){
                    followersClicked()
                }

                val layoutFollowees = findViewById<LinearLayout>(R.id.layout_followees)

                layoutFollowees.setOnClickListener(){
                    followeesClicked()
                }
        toolbarHandler.setupToolbarBasics()
    }

    private fun dialogShow(){
        //Dialog creation for loading data.
        val dialog = Dialog(this,R.style.Theme_Design_Light)
        val view: View = LayoutInflater.from(this).inflate(R.layout.layout_loading, null)
        val params: WindowManager.LayoutParams = dialog.getWindow()!!.getAttributes()
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.setContentView(view)
        this.dialog = dialog
        this.dialog.show()
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
                                notificationDao.sendNotification(follower, uid, NotificationType.FOLLOWED)
                                userDao.fetchUser(user.getUid())
                            }
                    }
            } else {
                followerDoc.update("followingUsers", FieldValue.arrayRemove(userfollow!!.getUid()))
                    .addOnSuccessListener {
                        followedDoc.update("followersUsers", FieldValue.arrayRemove(follower))
                            .addOnSuccessListener {
                                userDao.fetchUser(user.getUid())
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

    private fun loadData(user: User){
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

        dialog.dismiss()
    }

    override fun onDataLoaded(event: DataEvent) {
        if(event is UserLoadedEvent){
            user = event.user
            loadData(event.user)
        }
    }


}
