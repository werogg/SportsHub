package edu.ub.sportshub.profile

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import edu.ub.sportshub.R
import edu.ub.sportshub.auth.login.LoginActivity
import edu.ub.sportshub.home.HomeActivity


class EditProfileActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile2)

        val home = findViewById<TextView>(R.id.toolbar_my_profile_home)
        home.setOnClickListener(){
            buttonHomeClicked()
        }

        val validate = findViewById<Button>(R.id.btn_validate2)
        validate.setOnClickListener(){
            buttonSaveClicked()
        }

        val signout = findViewById<TextView>(R.id.toolbar_signout)
        signout.setOnClickListener(){
            textSignOutClicked()
        }
    }

    private fun textSignOutClicked() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun buttonSaveClicked() {
        val goProfile = Intent(this, ProfileActivity::class.java)
        startActivity(goProfile)
    }

    private fun buttonHomeClicked() {
        val goHome = Intent(this, HomeActivity::class.java)
        startActivity(goHome)
    }
}
