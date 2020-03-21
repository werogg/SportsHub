package edu.ub.sportshub.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import edu.ub.sportshub.R
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.profile.ProfileActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val textprofile = findViewById<TextView>(R.id.toolbar_secondary_txt_my_profile)
        //entrar al profile.
        textprofile.setOnClickListener(){
            val popupIntent = Intent(this, ProfileActivity::class.java)
            startActivity(popupIntent)
        }

        val fragmentAdapter = ViewPagerAdapter(supportFragmentManager)
        pager.adapter = fragmentAdapter

        tab_layout.setupWithViewPager(pager)
    }

    fun logout(view : View) {
        val authDatabaseHelper = AuthDatabaseHelper()
        authDatabaseHelper.signOut(this)
    }


}