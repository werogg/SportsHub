package edu.ub.sportshub.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import edu.ub.sportshub.R
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.profile.ProfileActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val textProfile = findViewById<TextView>(R.id.toolbar_secondary_txt_my_profile)

        textProfile.setOnClickListener(){
            val popupIntent = Intent(this, ProfileActivity::class.java)
            startActivity(popupIntent)
        }

        val imageProfile = findViewById<CircleImageView>(R.id.toolbar_secondary_image_my_profile)

        imageProfile.setOnClickListener {
            val popupIntent = Intent(this, ProfileActivity::class.java)
            startActivity(popupIntent)
        }
    }

    fun logout(view : View) {
        val authDatabaseHelper = AuthDatabaseHelper()
        authDatabaseHelper.signOut(this)
    }
}
