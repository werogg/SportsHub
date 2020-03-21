package edu.ub.sportshub.profile

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import edu.ub.sportshub.R
import edu.ub.sportshub.home.HomeActivity


class EditProfileActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        val home = findViewById<TextView>(R.id.toolbar_primary_home)
        home.setOnClickListener(){
            buttonHomeClicked()
        }

        val validate = findViewById<ImageButton>(R.id.btn_validate)
        validate.setOnClickListener(){
            buttonSaveClicked()
        }
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
