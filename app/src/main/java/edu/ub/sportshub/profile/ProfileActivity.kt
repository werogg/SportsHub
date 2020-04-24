package edu.ub.sportshub.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.ViewPager
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import edu.ub.sportshub.R
import edu.ub.sportshub.auth.login.LoginActivity
import edu.ub.sportshub.data.data.DataAccessObjectFactory
import edu.ub.sportshub.data.events.database.DataEvent
import edu.ub.sportshub.data.events.database.UserLoadedEvent
import edu.ub.sportshub.data.listeners.DataChangeListener
import edu.ub.sportshub.data.models.user.UserDao
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.home.HomeActivity
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



                val layoutfollowers = findViewById<LinearLayout>(R.id.layout_followers)
                layoutfollowers.setOnClickListener(){
                    followersclicked()
                }

                val layoutfollowees = findViewById<LinearLayout>(R.id.layout_followees)
                layoutfollowees.setOnClickListener(){
                    followeesclicked()
                }

                val editProfileText = findViewById<Button>(R.id.btn_profile)
                editProfileText.setOnClickListener() {
                    editProfileTextClicked()
                }

                val signout = findViewById<TextView>(R.id.toolbar_signout)
                signout.setOnClickListener() {
                    textSignOutClicked()
                }

                val home = findViewById<TextView>(R.id.toolbar_my_profile_home)
                home.setOnClickListener() {
                    buttonHomeClicked()
                }

                val notificationsButton =
                    findViewById<ImageView>(R.id.profile_toolbar_primary_notifications)

                notificationsButton.setOnClickListener {
                    notificationsButtonClicked()
                }
    }

    private fun notificationsButtonClicked() {
        val displayMetrics = applicationContext.resources.displayMetrics
        val dpValue1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350f, displayMetrics)
        val dpValue2 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 480f, displayMetrics)
        val inflater = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.fragment_notifications_primary, null)
        val coord = findViewById<ConstraintLayout>(R.id.profile_constraint_layout)
        popupWindow = PopupWindow(customView, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT, true)
        popupWindow!!.width = dpValue1.toInt()
        popupWindow!!.height = dpValue2.toInt()
        popupWindow!!.showAtLocation(coord, Gravity.TOP,0,220)
    }

    private fun buttonHomeClicked() {
        val intent = Intent(this,HomeActivity::class.java)
        startActivity(intent)
    }

    private fun textSignOutClicked() {
        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
    }

    private fun editProfileTextClicked() {
        val popupIntent = Intent(this, EditProfileActivity::class.java)
        startActivity(popupIntent)
    }

    private fun followersclicked(){
        val popupIntent = Intent(this, ProfileUsersActivity::class.java)
        popupIntent.putExtra("select",0)
        startActivity(popupIntent)
    }

    private fun followeesclicked(){
        val popupIntent = Intent(this, ProfileUsersActivity::class.java)
        popupIntent.putExtra("select",1)
        startActivity(popupIntent)
    }

    private fun addDots(position : Int){
        mDotLayout.removeAllViews()
        mDots.clear()
        for (i in 0..1){
            val text = TextView(this)
            text.setText(Html.fromHtml("&#8226"))
            text.setTextSize(35F)
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
        val uid = mFirebaseAuth.getCurrentUser()!!.uid
        mStoreDatabaseHelper.retrieveUser(uid)
            .addOnSuccessListener {
                val data = hashMapOf("profilePicture" to R.mipmap.ic_usuari_foreground)
                val uid = mFirebaseAuth.getCurrentUser()!!.uid
                val proces = mStoreDatabaseHelper.getUsersCollection().document(uid).update(data as Map<String, Any>)
            }
    }

    private fun loadData(user:User){

        var textname = findViewById<TextView>(R.id.txt_nameprofile)
        var imageprofile = findViewById<CircleImageView>(R.id.img_profile)
        var description = findViewById<TextView>(R.id.txt_descrp)
        var nfollowers = findViewById<TextView>(R.id.txt_nfollowers)
        var nfollowing = findViewById<TextView>(R.id.txt_nfollowing)
        textname.text = user.getUsername()
        //Check Image
        val dpSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130f, this?.resources?.displayMetrics).toInt()
        if (user.getProfilePicture().toString().equals("")){
            updatePhoto()
            Picasso.with(this)
                .load(R.mipmap.ic_usuari_foreground)
                .resize(dpSize, dpSize)
                .into(imageprofile)
        } else {
            Picasso.with(this)
                .load(user.getProfilePicture().toString())
                .resize(dpSize, dpSize)
                .into(imageprofile)
        }

        if (user.getBiography().equals("")){
            description.text = "Hey there,\nI'm using SportsHub."
        }else{
            description.text = user.getBiography()
        }
        nfollowers.text = user.getFollowersUsers()?.size.toString()
        nfollowing.text = user.getFollowingUsers()?.size.toString()
    }

    override fun onDataLoaded(event: DataEvent) {
        if(event is UserLoadedEvent){
            loadData(event.user)
        }
    }


}
