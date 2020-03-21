package edu.ub.sportshub.profile

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import edu.ub.sportshub.R
import edu.ub.sportshub.auth.login.LoginActivity
import edu.ub.sportshub.home.HomeActivity

class ProfileActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile2)

        val editProfileText = findViewById<Button>(R.id.btn_profile)
        editProfileText.setOnClickListener(){
            editProfileTextClicked()
        }

        val signout = findViewById<TextView>(R.id.toolbar_signout)
        signout.setOnClickListener(){
            textSignOutClicked()
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

    private fun textSignOutClicked() {
        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
    }

    private fun editProfileTextClicked() {
        val popupIntent = Intent(this, EditProfileActivity::class.java)
        startActivity(popupIntent)
    }
}
