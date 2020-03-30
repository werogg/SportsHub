package edu.ub.sportshub.profile

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import edu.ub.sportshub.R
import edu.ub.sportshub.auth.login.LoginActivity
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.home.HomeActivity
import edu.ub.sportshub.models.User
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileActivity : AppCompatActivity() {
    private var popupWindow : PopupWindow? = null
    private val mFirebaseAuth = AuthDatabaseHelper()
    private val mStoreDatabaseHelper = StoreDatabaseHelper()
    private val storage = FirebaseStorage.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

                val uid = mFirebaseAuth.getCurrentUser()!!.uid
                mStoreDatabaseHelper.retrieveUser(uid)
                .addOnSuccessListener {
                    loadData(it)
                }

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

    }

    private fun followeesclicked(){

    }

    private fun loadEvents(){
        val myevents = findViewById<LinearLayout>(R.id.layout_myevents)
        val myactivities = findViewById<LinearLayout>(R.id.layout_myevents)
        //DatabaseHelper.getEventsAssist()
        //DatabaseHelper.getEventsLiked()
        //DatabaseHelper.getEventsOwned()
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

    private fun loadData(it: DocumentSnapshot){

        var textname = findViewById<TextView>(R.id.txt_nameprofile)
        var imageprofile = findViewById<CircleImageView>(R.id.img_profile)
        var description = findViewById<TextView>(R.id.txt_descrp)
        var nfollowers = findViewById<TextView>(R.id.txt_nfollowers)
        var nfollowing = findViewById<TextView>(R.id.txt_nfollowing)
        textname.text = it.toObject(User::class.java)?.getUsername()
        //Check Image
        val dpSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130f, this?.resources?.displayMetrics).toInt()
        if (it.toObject(User::class.java)?.getProfilePicture().toString().equals("")){
            updatePhoto()
            Picasso.with(this)
                .load(R.mipmap.ic_usuari_foreground)
                .resize(dpSize, dpSize)
                .into(imageprofile)
        } else {
            Picasso.with(this)
                .load(it.toObject(User::class.java)?.getProfilePicture().toString())
                .resize(dpSize, dpSize)
                .into(imageprofile)
        }

        if (it.toObject(User::class.java)?.getBiography().equals("")){
            description.text = "Hey there,\nI'm using SportsHub."
        }else{
            description.text = it.toObject(User::class.java)?.getBiography()
        }
        nfollowers.text = it.toObject(User::class.java)?.getFollowersUsers()?.size.toString()
        nfollowing.text = it.toObject(User::class.java)?.getFollowingUsers()?.size.toString()
    }




}
