package edu.ub.sportshub.profile

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import edu.ub.sportshub.R
import edu.ub.sportshub.home.HomeActivity

class ProfileActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val editProfileText = findViewById<TextView>(R.id.toolbar_edit_my_profile)

        editProfileText.setOnClickListener(){
            editProfileTextClicked()
        }

        val home = findViewById<TextView>(R.id.toolbar_my_profile_home)
        home.setOnClickListener(){
            buttonHomeClicked()
        }
    }

    private fun buttonHomeClicked() {
        val intent = Intent(this,HomeActivity::class.java)
        startActivity(intent)
    }

    private fun editProfileTextClicked() {
        val popupIntent = Intent(this, EditProfileActivity::class.java)
        startActivity(popupIntent)
    }
}
