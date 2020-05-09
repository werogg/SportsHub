package edu.ub.sportshub.profile

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.viewpager.widget.ViewPager
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import edu.ub.sportshub.R
import edu.ub.sportshub.data.data.DataAccessObjectFactory
import edu.ub.sportshub.data.events.database.DataEvent
import edu.ub.sportshub.data.events.database.UserLoadedEvent
import edu.ub.sportshub.data.listeners.DataChangeListener
import edu.ub.sportshub.data.models.user.UserDao
import edu.ub.sportshub.handlers.ToolbarHandler
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.models.User
import edu.ub.sportshub.profile.events.ViewPagerAdapterProfile
import edu.ub.sportshub.profile.follow.ProfileUsersActivity
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileActivity : AppCompatActivity(), DataChangeListener {
    private var popupWindow : PopupWindow? = null
    private val mFirebaseAuth = AuthDatabaseHelper()
    private val mStoreDatabaseHelper = StoreDatabaseHelper()
    private var mDots = arrayListOf<TextView>()
    private lateinit var userDao : UserDao
    private lateinit var user : User
    private val toolbarHandler = ToolbarHandler(this)
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val uid = mFirebaseAuth.getCurrentUser()!!.uid
        val fragmentAdapter2 =
            ViewPagerAdapterProfile(
                supportFragmentManager,uid)
        pager_profile.adapter = fragmentAdapter2
        userDao = DataAccessObjectFactory.getUserDao()
        userDao.registerListener(this)
        dialogshow()

        Thread {
            kotlin.run {
                userDao.fetchUser(uid)
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



        val layoutFollowers = findViewById<LinearLayout>(R.id.layout_followers)

        layoutFollowers.setOnClickListener(){
            followersClicked()
        }

        val layoutFollowees = findViewById<LinearLayout>(R.id.layout_followees)

        layoutFollowees.setOnClickListener(){
            followeesClicked()
        }

        val editProfileText = findViewById<Button>(R.id.btn_profile)

        editProfileText.setOnClickListener() {
            editProfileTextClicked()
        }

        toolbarHandler.setupToolbarBasics()
    }

    private fun dialogshow(){
        //Dialog creation for loading data.
        val dialog = Dialog(this,R.style.Theme_Design_Light)
        val view: View = LayoutInflater.from(this).inflate(R.layout.layout_loading, null)
        val params: WindowManager.LayoutParams = dialog.getWindow()!!.getAttributes()
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.setContentView(view)
        this.dialog = dialog
        this.dialog.show()
    }

    private fun editProfileTextClicked() {
        val popupIntent = Intent(this, EditProfileActivity::class.java)
        startActivity(popupIntent)
    }

    private fun followersClicked(){
        val popupIntent = Intent(this, ProfileUsersActivity::class.java)
        popupIntent.putExtra("select",0)
        popupIntent.putExtra("id", user.getUid())
        startActivity(popupIntent)
    }

    private fun followeesClicked(){
        val popupIntent = Intent(this, ProfileUsersActivity::class.java)
        popupIntent.putExtra("select",1)
        popupIntent.putExtra("id", user.getUid())
        startActivity(popupIntent)
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



    private fun updatePhoto(){
        var uid = mFirebaseAuth.getCurrentUser()!!.uid
        mStoreDatabaseHelper.retrieveUser(uid)
            .addOnSuccessListener {
                val data = hashMapOf("profilePicture" to R.mipmap.ic_usuari_foreground)
                uid = mFirebaseAuth.getCurrentUser()!!.uid
                mStoreDatabaseHelper.getUsersCollection().document(uid).update(data as Map<String, Any>)
            }
    }

    private fun loadData(user:User){

        val textName = findViewById<TextView>(R.id.txt_nameprofile)
        val imageProfile = findViewById<CircleImageView>(R.id.img_profile)
        val textDescription = findViewById<TextView>(R.id.txt_descrp)
        val textFollowers = findViewById<TextView>(R.id.txt_nfollowers)
        val textFollowing = findViewById<TextView>(R.id.txt_nfollowing)
        textName.text = user.getUsername()
        //Check Image
        val dpSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130f, resources?.displayMetrics).toInt()
        if (user.getProfilePicture() == ""){
            updatePhoto()
            Picasso.with(this)
                .load(R.mipmap.ic_usuari_foreground)
                .resize(dpSize, dpSize)
                .into(imageProfile)
        } else {
            Picasso.with(this)
                .load(user.getProfilePicture())
                .resize(dpSize, dpSize)
                .into(imageProfile)
        }

        if (user.getBiography() == ""){
            textDescription.text = resources.getText(R.string.default_description)
        }else{
            textDescription.text = user.getBiography()
        }
        textFollowers.text = user.getFollowersUsers().size.toString()
        textFollowing.text = user.getFollowingUsers().size.toString()

    }

    override fun onDataLoaded(event: DataEvent) {
        if(event is UserLoadedEvent){
            loadData(event.user)
            user = event.user
        }
        dialog.dismiss()
    }

    override fun onBackPressed() {
        if (toolbarHandler.isNotificationsPopupVisible()) toolbarHandler.setNotificationsPopupVisibility(ToolbarHandler.NotificationsVisibility.GONE)
        super.onBackPressed()
    }



}
