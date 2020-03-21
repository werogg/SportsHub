package edu.ub.sportshub.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import de.hdodenhof.circleimageview.CircleImageView
import edu.ub.sportshub.R
import edu.ub.sportshub.event.CreateEventActivity
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.profile.ProfileActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlin.system.exitProcess

class HomeActivity : AppCompatActivity() {

    private val authDatabaseHelper = AuthDatabaseHelper()
    private lateinit var signoutToast : Toast
    private var backClicksCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupActivityFunctionalities()
    }

    private fun setupActivityFunctionalities() {
        setupListeners()
        setupFragments()
    }

    /**
     * Setup all listeners in the activity
     */
    private fun setupListeners() {
        val textProfile = findViewById<TextView>(R.id.toolbar_secondary_txt_my_profile)

        textProfile.setOnClickListener(){
            profileClicked()
        }

        val imageProfile = findViewById<CircleImageView>(R.id.toolbar_secondary_image_my_profile)

        imageProfile.setOnClickListener {
            profileClicked()
        }

        val createEventButton = findViewById<ExtendedFloatingActionButton>(R.id.home_create_event_floating_button)

        createEventButton.setOnClickListener {
            createEventButtonClicked()
        }
    }

    private fun setupFragments() {
        val fragmentAdapter = ViewPagerAdapter(supportFragmentManager)
        pager.adapter = fragmentAdapter

        tab_layout.setupWithViewPager(pager)
    }

    fun logout(view : View) {
        authDatabaseHelper.signOut(this)
    }

    private fun profileClicked() {
        val popupIntent = Intent(this, ProfileActivity::class.java)
        startActivity(popupIntent)
    }

    private fun createEventButtonClicked() {
        val intent = Intent(this, CreateEventActivity::class.java)
        startActivity(intent)
    }

    /**
     * 5 clicks to logout
     */
    override fun onBackPressed() {
        exitProcess(0)
    }
}