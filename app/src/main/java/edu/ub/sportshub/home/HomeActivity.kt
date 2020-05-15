package edu.ub.sportshub.home

//import sun.jvm.hotspot.utilities.IntArray

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import de.hdodenhof.circleimageview.CircleImageView
import edu.ub.sportshub.R
import edu.ub.sportshub.event.CreateEventActivity
import edu.ub.sportshub.handlers.ToolbarHandler
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.profile.ProfileActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlin.system.exitProcess


class HomeActivity : AppCompatActivity() {

    private val authDatabaseHelper = AuthDatabaseHelper()
    private val toolbarHandler = ToolbarHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupActivityFunctionalities()
        toolbarHandler.setupToolbarBasics()
    }

    private fun setupActivityFunctionalities() {
        setupListeners()
        setupFragments()
    }

    /**
     * Setup all listeners in the activity
     */
    private fun setupListeners() {
        val createEventButton = findViewById<ExtendedFloatingActionButton>(R.id.home_create_event_floating_button)

        createEventButton.setOnClickListener {
            createEventButtonClicked()
        }

    }

    private fun setupFragments() {
        val fragmentAdapter = ViewPagerAdapter(supportFragmentManager)
        pager.adapter = fragmentAdapter
        tab_layout.setupWithViewPager(pager)
        tab_layout.getTabAt(0)?.text = getString(R.string.eventsFragment)
            tab_layout.getTabAt(1)?.text = getString(R.string.usersFragment)
            tab_layout.getTabAt(2)?.text =getString(R.string.mapFragment)
    }

    private fun createEventButtonClicked() {
        val intent = Intent(this, CreateEventActivity::class.java)
        startActivity(intent)
    }

    fun showEventFragment() {
        pager.currentItem = 0
        pager.adapter?.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        when {
            toolbarHandler.isNotificationsPopupVisible() -> toolbarHandler.setNotificationsPopupVisibility(ToolbarHandler.NotificationsVisibility.GONE)
            pager.currentItem != 0 -> showEventFragment()
            else -> super.onBackPressed()
        }
    }


}